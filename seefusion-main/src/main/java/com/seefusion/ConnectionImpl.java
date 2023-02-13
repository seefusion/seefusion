/*
 * ConnectionImpl.java
 *
 * Created on July 2, 2004, 10:33 AM
 */

package com.seefusion;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author TheArchitect
 */
class ConnectionImpl implements java.sql.Connection {
	
	private static final Logger LOG = Logger.getLogger(ConnectionImpl.class.getName());

	protected java.sql.Connection c;

	Properties connectionMetadata, sfUrlOptions, connectionProperties;

	DatabaseMetaData dbmd = null;

	Object objectNumLock = new Object();

	static long prevObjectNum = 0;

	long objectNum;

	int lastMaxFieldSize = 0;

	boolean isIgnoringDuplicateMaxFieldSize;

	JdbcConfig jdbcConfig = new JdbcConfig();
	
	protected boolean isTracingAPI = false;

	/**
	 * Creates a new instance of ConnectionImpl
	 */
	ConnectionImpl(java.sql.Connection c, Properties connectionProperties, Properties sfUrlOptions, Properties connectionMetadata, boolean isTracingAPI) {
		super();
		// trace("ConnectionImpl created");
		if (c == null) {
			throw new IllegalArgumentException("Connection cannot be null.");
		}
		this.c = c;
		this.sfUrlOptions = sfUrlOptions;
		this.connectionProperties = connectionProperties;
		this.connectionMetadata = connectionMetadata;
		this.isTracingAPI = isTracingAPI;
		sfConfigure();
	}

	void sfConfigure() {
		String dsn = sfUrlOptions.getProperty("dsn");
		if (dsn != null) {
			connectionMetadata.setProperty("dsn", dsn);
		}
		if (sfUrlOptions.getProperty("logconnection") != null || sfUrlOptions.getProperty("logall") != null) {
			isTracingAPI = true;
			trace("Enabling Logging");
		}
		String propValue;
		propValue = sfUrlOptions.getProperty("hideparameterskeywordlist");
		if (propValue != null && propValue.length() != 0) {
			StringTokenizer st = new StringTokenizer(propValue, ", \t\n\r\f");
			while (st.hasMoreTokens()) {
				jdbcConfig.addHideParametersKeyword(st.nextToken());
			}
		}
		propValue = sfUrlOptions.getProperty("rowlimit");
		if (propValue != null && propValue.length() != 0) {
			try {
				jdbcConfig.setRowLimit(Integer.parseInt(propValue));
			} catch (NumberFormatException e) {
				log("Unable to parse rowLimit to a number: " + propValue);
			}
		}
		propValue = sfUrlOptions.getProperty("rowlimitexception");
		if (propValue != null && propValue.length() != 0) {
			jdbcConfig.setRowLimitThrowingException(Util.parseYesNoParam(propValue));
		}
		propValue = sfUrlOptions.getProperty("notifycount");
		if (propValue != null && propValue.length() != 0) {
			try {
				jdbcConfig.setNotifyCount(Integer.parseInt(propValue));
			} catch (NumberFormatException e) {
				log("Unable to parse notifyCount into integer: " + propValue);
			}
		}

		propValue = sfUrlOptions.getProperty("remindcount");
		if (propValue != null && propValue.length() != 0) {
			try {
				jdbcConfig.setRemindCount(Integer.parseInt(propValue));
			} catch (NumberFormatException e) {
				log("Unable to parse remindCount into integer: " + propValue);
			}
		}
		
		if(sfUrlOptions.getProperty("convertblobtobytes") != null) {
			jdbcConfig.setConvertingBlobToBytes(true);
		}
		

		synchronized (objectNumLock) {
			prevObjectNum++;
			this.objectNum = prevObjectNum;
		}
		// log("Created");
		dbmd = null;
	}

	int getLastMaxFieldSize() {
		return lastMaxFieldSize;
	}

	void setLastMaxFieldSize(int size) {
		lastMaxFieldSize = size;
	}

	long getObjectNumber() {
		return objectNum;
	}

	boolean isIgnoringDuplicateMaxFieldSize() {
		return isIgnoringDuplicateMaxFieldSize;
	}

	void ignoreDuplicateMaxFieldSize(boolean uhmmm) {
		isIgnoringDuplicateMaxFieldSize = uhmmm;
	}

	protected Statement wrapStatement(Statement s) throws SQLException {
		return new StatementImpl(this, s, jdbcConfig, isTracingAPI);
	}
	
	protected PreparedStatement wrapPreparedStatement(PreparedStatement ps, String sql) throws SQLException {
		return new PreparedStatementImpl(this, ps, sql, jdbcConfig, isTracingAPI);
	}

	protected CallableStatement wrapCallableStatement(CallableStatement c, String sql) throws SQLException {
		return new CallableStatementImpl(this, c, sql, jdbcConfig, isTracingAPI);
	}
	
	/**
	 * Creates a CallableStatementImpl object for calling database stored
	 * procedures.
	 */
	@Override
	public final CallableStatement prepareCall(String sql) throws SQLException {
		if(isTracingAPI) trace("prepareCall(sql=\"" + sql + "\")");
		try {
			return wrapCallableStatement(c.prepareCall(sql), sql);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}
	
	/**
	 * Creates a CallableStatementImpl object that will generate ResultSet
	 * objects with the given type and concurrency.
	 */
	@Override
	public final CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		if(isTracingAPI) trace("prepareCall(sql=\"" + sql + "\", resultSetType=" + resultSetType + ", resultSetConcurrency="
				+ resultSetConcurrency + ")");
		try {
			return wrapCallableStatement(c.prepareCall(sql, resultSetType, resultSetConcurrency), sql);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public final CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		if(isTracingAPI) trace("prepareCall(sql=\"" + sql + "\", resultSetType=" + resultSetType + ", resultSetConcurrency="
				+ resultSetConcurrency + ", resultSetHoldability=" + resultSetHoldability + ")");
		try {
			return wrapCallableStatement(c.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		// log("ConnectionImpl returning metadata");
		// return new DatabaseMetaData(c.getMetaData());
		return c.getMetaData();
	}

	@Override
	public void clearWarnings() throws SQLException {
		c.clearWarnings();
	}

	boolean closed = false;

	@Override
	public void close() throws SQLException {
		if(isTracingAPI) trace("Closed.");
		closed = true;
		try {
			c.close();
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	protected void finalize() {
		if (!closed) {
			try {
				close();
			} catch (SQLException e) {
				// ignore
			}
		}
	}

	@Override
	public final Statement createStatement() throws SQLException {
		if(isTracingAPI) trace("createStatement()");
		try {
			return wrapStatement(c.createStatement());
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public final Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		if(isTracingAPI) trace("createStatement(resultSetType=" + resultSetType + ", resultSetConcurrency=" + resultSetConcurrency + ")");
		try {
			return wrapStatement(c.createStatement(resultSetType, resultSetConcurrency));
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public final Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if(isTracingAPI) trace("createStatement(resultSetType=" + resultSetType + ", resultSetConcurrency=" + resultSetConcurrency
				+ ", resultSetHoldability=" + resultSetHoldability + ")");
		try {
			return wrapStatement(c.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public final PreparedStatement prepareStatement(String sql) throws SQLException {
		if(isTracingAPI) trace("prepareStatement(sql=\"" + sql + "\")");
		try {
			return wrapPreparedStatement(c.prepareStatement(sql), sql);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public final PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		if(isTracingAPI) trace("prepareStatement(sql=\"" + sql + "\", columnNames[" + columnNames.length + "])");
		try {
			return wrapPreparedStatement(c.prepareStatement(sql, columnNames), sql);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public final PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		if(isTracingAPI) trace("prepareStatement(sql=\"" + sql + "\", autogeneratedKeys=" + autoGeneratedKeys + ")");
		try {
			return wrapPreparedStatement(c.prepareStatement(sql, autoGeneratedKeys), sql);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public final PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		if(isTracingAPI) trace("prepareStatement(sql=\"" + sql + "\", " + columnIndexes.length + " columnIndexes)");
		try {
			return wrapPreparedStatement(c.prepareStatement(sql, columnIndexes), sql);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public final PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		if(isTracingAPI) trace("prepareStatement(sql=\"" + sql + "\", resultSetType=" + resultSetType + ", resultSetConcurrency=" + resultSetConcurrency + ")");
		try {
			return wrapPreparedStatement(c.prepareStatement(sql, resultSetType, resultSetConcurrency), sql);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public final PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		if(isTracingAPI) trace("prepareStatement(sql=\"" + sql + "\", resultSetType=" + resultSetType + ", resultSetConcurrency=" + resultSetConcurrency + ", resultSetHoldability=" + resultSetHoldability + ")");
		try {
			return wrapPreparedStatement(c.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	///////////////////////////////////////
	// begin non-altered wrapped methods //
	///////////////////////////////////////
	@Override
	public void commit() throws SQLException {
		if(isTracingAPI) trace("commit()");
		c.commit();
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		boolean ret = c.getAutoCommit(); 
		if(isTracingAPI) trace("getAutoCommit() = " + ret);
		return ret;
	}

	@Override
	public String getCatalog() throws SQLException {
		String ret = c.getCatalog();
		if(isTracingAPI) trace("getCatalog() = " + ret);
		return ret;
	}

	@Override
	public int getHoldability() throws SQLException {
		int ret = c.getHoldability();
		if(isTracingAPI) trace("getHoldability() = " + ret);
		return ret;
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		int ret = c.getTransactionIsolation();
		if(isTracingAPI) trace("getTransactionIsolation() = " + ret);
		return ret;
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		if(isTracingAPI) trace("getTypeMap()");
		return c.getTypeMap();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		SQLWarning ret = c.getWarnings();
		if(isTracingAPI) trace("getWarnings() = " + ret);
		return ret;
	}

	@Override
	public boolean isClosed() throws SQLException {
		boolean ret = c.isClosed();
		if(isTracingAPI) trace("isClosed() = " + ret);
		return ret;
	}
	
	@Override
	public boolean isReadOnly() throws SQLException {
		boolean ret = c.isReadOnly();
		if(isTracingAPI) trace("isReadOnly() = " + ret);
		return ret;
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		String ret = c.nativeSQL(sql);
		if(isTracingAPI) trace("nativeSQL(sql) = " + ret);
		return ret;
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		if(isTracingAPI) trace("releaseSavepoint(savepoint)");
		c.releaseSavepoint(savepoint);
	}

	@Override
	public void rollback() throws SQLException {
		if(isTracingAPI) trace("rollback()");
		c.rollback();
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		if(isTracingAPI) trace("rollback(savepoint)");
		c.rollback(savepoint);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if(isTracingAPI) trace("setAutoCommit("+autoCommit+")");
		c.setAutoCommit(autoCommit);
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		if(isTracingAPI) trace("setCatalog("+catalog+")");
		c.setCatalog(catalog);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		if(isTracingAPI) trace("setHoldability("+holdability+")");
		c.setHoldability(holdability);
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		if(isTracingAPI) trace("setReadOnly("+readOnly+")");
		c.setReadOnly(readOnly);
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		if(isTracingAPI) trace("setSavepoint()");
		return c.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		if(isTracingAPI) trace("setSavepoint(\""+name+"\")");
		return c.setSavepoint(name);
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		if(isTracingAPI) trace("setTransactionIsolation("+level+")");
		c.setTransactionIsolation(level);
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		if(isTracingAPI) trace("setTypeMap(map)");
		c.setTypeMap(map);
	}

	protected void logException(Exception e) {
		LOG.log(Level.SEVERE, "Connection[" + objectNum + "]", e);
	}

	void trace(String s) {
		if (isTracingAPI) {
			log(s);
		}
	}

	protected void log(String s) {
		LOG.info("Connection[" + objectNum + "]: " + s);
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		return c.createArrayOf(typeName, elements);
	}

	@Override
	public Blob createBlob() throws SQLException {
		return c.createBlob();
	}

	@Override
	public Clob createClob() throws SQLException {
		return c.createClob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return c.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return c.createSQLXML();
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return c.createStruct(typeName, attributes);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return c.getClientInfo();
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return c.getClientInfo(name);
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return c.isValid(timeout);
	}

	@Override
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		c.setClientInfo(properties);
	}

	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		c.setClientInfo(name, value);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return c.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return c.unwrap(iface);
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		c.abort(executor);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return c.getNetworkTimeout();
	}

	@Override
	public String getSchema() throws SQLException {
		return c.getSchema();
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		c.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		c.setSchema(schema);
	}
	
}
