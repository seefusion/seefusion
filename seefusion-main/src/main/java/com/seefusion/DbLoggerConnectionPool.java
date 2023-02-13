package com.seefusion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbLoggerConnectionPool implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(DbLoggerConnectionPool.class.getName());

	private static final long TIMEOUT = 10L * 60L * 1000L;

	private String url, username, password;

	LinkedList<DbLoggerPooledConnection> pool = new LinkedList<DbLoggerPooledConnection>();

	private boolean poolClosed = false;

	private Object needNewConnectionSemaphore = new Object();

	Thread thread;

	final DbLoggerDialect dialect;

	DbLoggerConnectionPool(String url, String username, String password) throws SQLException {
		this.url = url;
		this.dialect = DbLoggerDialectFactory.getDialectFor(url);
		this.username = username;
		this.password = password;
		// only instantiate (and start the thread) if we can actually connect
		Connection c = DriverManager.getConnection(url, username, password);
		addConnection(new DbLoggerPooledConnection(c, this));
		thread = new Thread(this);
		thread.setDaemon(true);
		thread.setName("SeeFusion dbLogger Connection Pool");
		thread.start();
	}

	void setURL(String url) {
		this.url = url;
	}

	void setUsername(String username) {
		this.username = username;
	}

	void setPassword(String password) {
		this.password = password;
	}

	/**
	 * close the pool
	 */
	void close() {
		// Logger.debug("DbLoggerConnectionPool::close(" + this.toString() + ")");
		poolClosed = true;
		thread.interrupt();
		DbLoggerPooledConnection c;
		// not using iterator to avoid potential
		// for ConcurrentModificationException
		// Logger.debug("DbLoggerConnectionPool::closing connections");
		while (!pool.isEmpty() && (c = pool.removeFirst()) != null) {
			try {
				c._close();
				// Logger.debug("DbLoggerConnectionPool::close(" + this.toString() + ") " + c.toString() + " closed");
			}
			catch (SQLException e) {
				// ignore
			}
		}
		// Logger.debug("DbLoggerConnectionPool::close(" + this.toString() + ") complete");
	}

	void validate() throws SQLException {
		Connection c = DriverManager.getConnection(url, username, password);
		
		if(!dialect.validate(c)) {
			throw new SQLException("Invalid connection returned.");
		}
		c.close();
	}

	Connection getConnection() throws SQLException {
		return getConnection(true);
	}

	Connection getConnection(boolean retry) throws SQLException {
		// Logger.debug("DbLoggerConnectionPool::getConnection(" + this.toString() + ")", pool.isEmpty() , new Exception());
		Connection ret = null;
		if(poolClosed) {
			return null;
		}
		synchronized (pool) {
			if(pool.isEmpty()) {
				synchronized (needNewConnectionSemaphore) {
					// Logger.debug("DbLoggerConnectionPool::getConnection(" + this.toString() + ") pool is empty, notifying thread to get new connection", new Exception());
					needNewConnectionSemaphore.notify();
				}
				try {
					pool.wait();
				}
				catch (InterruptedException e) {
					Thread.interrupted();
				}
			}
			ret = pool.removeFirst();
		}
		try {
			if(!ret.isValid(1000) && retry) {
				return getConnection(false);
			}
		}
		catch (SQLException e) {
			return getConnection(false);
		}
		// Logger.debug("DbLoggerConnectionPool::getConnection(" + this.toString() + ") returning " + ret.toString());
		return ret;
	}

	@Override
	public void run() {
		while (!poolClosed) {
			boolean needsNewConnection;
			synchronized (pool) {
				needsNewConnection = pool.isEmpty() && !poolClosed;
			}
			if(needsNewConnection) {
				LOG.fine("DbLoggerConnectionPool::run(" + this.toString() + ") adding connection to empty pool");
				try {
					Connection c = DriverManager.getConnection(url, username, password);
					addConnection(new DbLoggerPooledConnection(c, this));
				}
				catch (SQLException e) {
					LOG.log(Level.SEVERE, "Unable to acquire connection for dbLogger pool", e);
					try {
						Thread.sleep(10000);
					}
					catch (InterruptedException e1) {
						Thread.interrupted();
					}
				}
			}
			if(!poolClosed) {
				synchronized (needNewConnectionSemaphore) {
					try {
						synchronized (pool) {
							needsNewConnection = pool.isEmpty() && !poolClosed;
						}
						if(!needsNewConnection) {
							needNewConnectionSemaphore.wait();
						}
					}
					catch (InterruptedException e) {
						Thread.interrupted();
					}
				}
			}
		}
	}

	void addConnection(DbLoggerPooledConnection c) {
		// Logger.debug("DbLoggerConnectionPool::addConnection(" + this.toString() + ") Pooling connection " + c.toString());
		try {
			if(isExpired(c)) {
				c._close();
			}
			else if(!c.isClosed()) {
				if(poolClosed) {
					close(c);
				}
				else {
					synchronized (pool) {
						pool.add(c);
						pool.notify();
					}
				}
			}
		}
		catch (SQLException e) {
			// ignore
		}
	}

	private boolean isExpired(DbLoggerPooledConnection c) {
		return System.currentTimeMillis() > c.getCreationTick() + TIMEOUT;
	}

	void close(DbLoggerPooledConnection c) {
		try {
			c._close();
		}
		catch (SQLException e) {
			// ignore
		}
		synchronized (pool) {
			pool.notifyAll();
		}
	}

	public String getUrl() {
		return url;
	}

	public DbLoggerDialect getDialect() {
		return dialect;
	}

}
