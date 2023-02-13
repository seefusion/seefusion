/*
 * DbLogger.java
 *
 */

package com.seefusion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logs objects that implement DaoObject.
 * 
 */
class DbLogger extends SeeTask {

	private static final Logger LOG = Logger.getLogger(DbLogger.class.getName());

	static final Map<String, String> DRIVERS;

	static {
		DRIVERS = new HashMap<String, String>();
		DRIVERS.put("MySQL - org.git", "org.gjt.mm.mysql.Driver");
		DRIVERS.put("MySQL - com.mysql", "com.mysql.jdbc.Driver");
		DRIVERS.put("MSSQLServer", "macromedia.jdbc.MacromediaDriver");
		DRIVERS.put("Sybase", "macromedia.jdbc.MacromediaDriver");
		DRIVERS.put("OracleThin", "oracle.jdbc.driver.OracleDriver");
		DRIVERS.put("DB2 OS390", "macromedia.jdbc.MacromediaDriver");
		DRIVERS.put("SybaseJConnect", "com.sybase.jdbc2.jdbc.SybDriver");
		DRIVERS.put("JTDS", "net.sourceforge.jtds.jdbc.Driver");
		DRIVERS.put("PostgreSQL", "org.postgresql.Driver");
		//DRIVERS.put("MSAccess", "macromedia.jdbc.MacromediaDriver");
		//DRIVERS.put("Informix", "macromedia.jdbc.MacromediaDriver");
	}

	protected static final int QUERY_TIMEOUT_SEC = 60;

	protected boolean isDebugMode = false;

	protected int backlog = 200;

	protected Object loggerNotifier = new Object();

	protected Object connectionSemaphore = new Object();

	protected boolean running = false;

	protected PooledThread myThread = null;

	protected SeeFusion sf;

	protected long lastReconnectTime = 0;

	protected boolean allTablesOk = false;

	private DbLoggerConnectionPool pool;

	private Map<Class<? extends DaoObject>, SeeDAO<? extends DaoObject>> daoMap;

	private LinkedList<DaoObject> loggingQueue = new LinkedList<DaoObject>();

	SeeFusion getSeeFusion() {
		return sf;
	}

	/** Creates a new instance of DbLogger */
	DbLogger(SeeFusion sf) {
		this.sf = sf;
		setConnectionPool(pool);
	}

	void setConnectionPool(DbLoggerConnectionPool pool) {
		if(this.pool != null) {
			this.pool.close();
		}
		this.pool = pool;
		createDaoMap(pool);
		if(pool != null && !running) {
			myThread = ThreadPool.start(this);
			this.running = true;
		}
	}

	void createDaoMap(DbLoggerConnectionPool pool) {
		if(pool != null) {
			DbLoggerDialect dialect = DbLoggerDialectFactory.getDialectFor(pool.getUrl());
			LOG.log(Level.INFO, "DBLogger using dialect " + dialect.getName());
		}
		daoMap = new HashMap<Class<? extends DaoObject>, SeeDAO<? extends DaoObject>>();
		daoMap.put(Counters.class, new CountersDao(pool));
		daoMap.put(Incident.class, new IncidentDao(pool));
		daoMap.put(IncidentServer.class, new IncidentServerDao(pool));
		daoMap.put(Profile.class, new ProfileDao(pool));
		daoMap.put(QueryInfo.class, new QueryInfoDao(pool, 100));
		daoMap.put(RequestInfo.class, new RequestInfoDao(pool, 100));
	}

	@Override
	public String getThreadName() {
		return "dbLogger " + sf.getInstanceName();
	}

	boolean isDatabaseOk() {
		return pool != null;
	}

	// don't try to reconnect more than every 10s
	static final long DEFAULT_DELAY_TIME = 10000;
	// double that time every failure until we're reconnecting every 5min
	static final long MAX_DELAY_TIME = 300000;
	long delayTime = DEFAULT_DELAY_TIME;

	private boolean insert(DaoObject obj) {
		SeeDAO<? extends DaoObject> dao = daoMap.get(obj.getClass());
		if(dao == null) {
			LOG.severe("Could not find DAO for " + obj.getClass().getSimpleName());
		}
		try {
			return dao.insert(obj);
		}
		catch (SQLException e0) {
			// try once more....
			LOG.log(Level.WARNING, "Retrying insert into " + dao.getTableName() + " after exception", e0);
			try {
				return dao.insert(obj);
			}
			catch (SQLException e) {
				LOG.log(Level.SEVERE, "Unable to insert " + dao.getTableName(), e);
				return false;
			}
		}

	}

	long lastWarnTime = 0;
	long MIN_WARN_INTERVAL = 10000;

	void log(DaoObject obj) {
		if(obj instanceof QueryInfo) {
			// record the stack while still in the "worker" thread
			((QueryInfo) obj).getStack();
		}
		else if(obj instanceof Incident) {
			RequestList list = sf.getMasterRequestList();
			logIncidentPages(obj.getId(), list);
		}
		if(running) {
			synchronized (loggingQueue) {
				if(loggingQueue.size() <= backlog) {
					loggingQueue.addLast(obj);
					synchronized (loggerNotifier) {
						loggerNotifier.notify();
					}
				}
				else {
					LOG.warning(
							"Unable to add " + daoMap.get(obj.getClass()).getTableName() + " to full logging queue");
				}
			}
		}
		else {
			insert(obj);
		}
	}

	boolean isRunning() {
		return this.running;
	}

	@Override
	public void run() {
		try {
			// delay to prevent race conditions during unit tests
			Thread.sleep(50L);
		}
		catch (InterruptedException e) {
		}
		LOG.info("DbLogger Started.");
		myThread.setPriority(Thread.MIN_PRIORITY);
		while (running) {
			try {
				DaoObject obj;
				while (!loggingQueue.isEmpty()) {
					synchronized (loggingQueue) {
						obj = loggingQueue.isEmpty() ? null : loggingQueue.removeFirst();
					}
					if(obj != null && running) {
						// log item into db
						synchronized (this.connectionSemaphore) {
							insert(obj);
						}
						LOG.fine("Logged item of type " + obj.getClass().getSimpleName());
					}
				}
				if(running) {
					synchronized (loggerNotifier) {
						// Logger.debug("DBLogger::run loggerNotifier.wait");
						loggerNotifier.wait();
						// Logger.debug("DBLogger::run loggerNotifier.wait was
						// notified");
					}
				}
			}
			catch (Throwable t) {
				LOG.log(Level.SEVERE, "Unhandled exception", t);
			}
		}
		if(pool != null) {
			pool.close();
		}
		LOG.info("DbLogger Stopped.");
	}

	QueryResult doPreparedQuery(String query, Object[] parms) throws SQLException {
		return doPreparedQuery(query, parms, 0);
	}

	QueryResult doPreparedQuery(String query, Object[] parms, int maxRows) throws SQLException {
		Connection c = pool.getConnection();
		try {
			PreparedStatement ps = c.prepareStatement(query);
			try {
				for (int i = 0; i < parms.length; i++) {
					ps.setObject(i + 1, parms[i]);
				}
				QueryResult ret = QueryResult.getQueryResult(ps.executeQuery(), maxRows);
				return ret;
			}
			finally {
				ps.close();
			}
		}
		finally {
			c.close();
		}
	}

	QueryResult doQuery(String query, int maxRows) throws SQLException {
		Connection c = pool.getConnection();
		try {
			Statement ps = c.createStatement();
			try {
				QueryResult ret = QueryResult.getQueryResult(ps.executeQuery(query), maxRows);
				return ret;
			}
			finally {
				ps.close();
			}
		}
		finally {
			c.close();
		}
	}

	void doPreparedUpdate(String query, Object[] parms) throws SQLException {
		Connection c = pool.getConnection();
		try {
			PreparedStatement ps = c.prepareStatement(query);
			try {
				for (int i = 0; i < parms.length; i++) {
					ps.setObject(i + 1, parms[i]);
				}
				ps.executeUpdate();
			}
			finally {
				ps.close();
			}
		}
		finally {
			c.close();
		}
	}

	/**
	 * Used by test cases
	 */
	void flush() {
		if(!running) {
			LOG.warning("flush() called when dbLogger is not running; ignored.");
		}
		else {
			if(myThread == null) {
				throw new RuntimeException("Attempt to DbLogger.flush() when logger thread is not alive.");
			}
			else {
				myThread.setPriority(Thread.NORM_PRIORITY);
				synchronized (loggerNotifier) {
					loggerNotifier.notify();
				}
				// wait up to 10sec for queue to flush
				int tries = 0;
				while (!loggingQueue.isEmpty() && tries++ < 100) {
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						// ignore
					}
					if(!loggingQueue.isEmpty()) {
						synchronized (loggerNotifier) {
							loggerNotifier.notify();
						}
						LOG.fine("Still " + loggingQueue.size() + " elements in queue:" + loggingQueue.toString());
					}
				}
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					// ignore
				}
				myThread.setPriority(Thread.MIN_PRIORITY);
			}
		}
	}

	@Override
	void shutdown() {
		if(myThread != null) {
			LOG.info("DbLogger Stopping...");
			this.running = false;
			synchronized (loggerNotifier) {
				loggerNotifier.notify();
			}
			try {
				myThread.join(10000);
			}
			catch (InterruptedException e) {
				// ignore
			}
			if(myThread.getTask() != null) {
				LOG.warning("Unable to stop current dbLogger thread.");
			}
			myThread = null;
		}
	}

	void doDdlChain(Connection c, String nameForException, String[] aDdl) throws SQLException {
		Statement s = c.createStatement();
		SQLException ex[] = new SQLException[aDdl.length];
		int exceptionCount = 0;
		for (int i = 0; i < aDdl.length; ++i) {
			try {
				s.executeUpdate(aDdl[i]);
			}
			catch (SQLException e) {
				ex[exceptionCount] = e;
				exceptionCount++;
			}
		}
		s.close();
		if(exceptionCount == 1) {
			throw ex[0];
		}
		else if(exceptionCount > 1) {
			SQLException e = new SQLException(nameForException);
			for (int i = 0; i < exceptionCount; ++i) {
				e.setNextException(ex[i]);
			}
		}
	}

	public SeeDAO<? extends DaoObject> getDao(Class<? extends DaoObject> clazz) {
		return daoMap.get(clazz);
	}

	Map<Class<? extends DaoObject>, SeeDAO<? extends DaoObject>> getDaoMap() {
		return daoMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.RequestList#doLogIncidentPages(int,
	 * com.seefusion.SeeFusion, com.seefusion.DbLogger)
	 */
	public void logIncidentPages(String incidentID, RequestList list) {
		LOG.fine("Logging " + list.getCurrentRequestCount() + " incident pages for incident " + incidentID);
		LinkedList<RequestInfo> infos = new LinkedList<RequestInfo>();
		synchronized (list) {
			RequestInfo reqInfo;
			for (Iterator<Entry<String, RequestInfo>> iter = list.getCurrentRequests().iterator(); iter.hasNext();) {
				reqInfo = iter.next().getValue();
				reqInfo = (RequestInfo) reqInfo.clone();
				infos.add(reqInfo);
			}
		}
		for (RequestInfo reqInfo : infos) {
			reqInfo.setIncidentID(incidentID);
			log(reqInfo);
		}
	}
}
