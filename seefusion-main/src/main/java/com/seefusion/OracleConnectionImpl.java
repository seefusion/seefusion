/**
 * 
 */
package com.seefusion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.Executor;

import oracle.jdbc.LogicalTransactionId;
import oracle.jdbc.LogicalTransactionIdEventListener;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleOCIFailover;
import oracle.jdbc.OracleSavepoint;
import oracle.jdbc.OracleShardingKey;
import oracle.jdbc.aq.AQDequeueOptions;
import oracle.jdbc.aq.AQEnqueueOptions;
import oracle.jdbc.aq.AQMessage;
import oracle.jdbc.aq.AQNotificationRegistration;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.jdbc.pool.OracleConnectionCacheCallback;
import oracle.sql.ARRAY;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.DATE;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;
import oracle.sql.TypeDescriptor;

/**
 * @author Daryl
 * 
 */
@SuppressWarnings("deprecation")
class OracleConnectionImpl extends ConnectionImpl implements OracleConnection {

	OracleConnection oc;

	/**
	 * @param c
	 * @param props
	 * @param isTracingAPI
	 */
	public OracleConnectionImpl(Connection c, Properties connectionProperties, Properties sfUrlConfig, Properties connectionMetadata, boolean isTracingApi) {
		super(c, connectionProperties, sfUrlConfig, connectionMetadata, isTracingApi);
		if(c instanceof OracleConnection) {
			oc = (OracleConnection) c;
		}
		else {
			throw new IllegalArgumentException(c.getClass().getName() + " does not implement OracleConnection!");
		}
	}
	
	@Override
	protected Statement wrapStatement(Statement s) throws SQLException {
		return new OracleStatementImpl(this, s, jdbcConfig, isTracingAPI);
	}
	
	@Override
	protected PreparedStatement wrapPreparedStatement(PreparedStatement ps, String sql) throws SQLException {
		return new OraclePreparedStatementImpl(this, ps, sql, jdbcConfig, isTracingAPI);
	}

	@Override
	protected CallableStatement wrapCallableStatement(CallableStatement c, String sql) throws SQLException {
		return new OracleCallableStatementImpl(this, c, sql, jdbcConfig, isTracingAPI);
	}
	
	////////////////////////// ORACLE METHODS ////////////////////////
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#_getPC()
	 */
	@Override
	public Connection _getPC() {
		return oc._getPC();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#archive(int, int, java.lang.String)
	 */
	@Override
	public void archive(int arg0, int arg1, String arg2) throws SQLException {
		try {
			oc.archive(arg0, arg1, arg2);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getAutoClose()
	 */
	@Override
	public boolean getAutoClose() throws SQLException {
		try {
			return oc.getAutoClose();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getCreateStatementAsRefCursor()
	 */
	@Override
	public boolean getCreateStatementAsRefCursor() {
		return oc.getCreateStatementAsRefCursor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getDefaultExecuteBatch()
	 */
	@Override
	public int getDefaultExecuteBatch() {
		return oc.getDefaultExecuteBatch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getDefaultRowPrefetch()
	 */
	@Override
	public int getDefaultRowPrefetch() {
		return oc.getDefaultRowPrefetch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getDescriptor(java.lang.String)
	 */
	@Override
	public Object getDescriptor(String arg0) {
		return oc.getDescriptor(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getIncludeSynonyms()
	 */
	@Override
	public boolean getIncludeSynonyms() {
		return oc.getIncludeSynonyms();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getJavaObject(java.lang.String)
	 */
	@Override
	public Object getJavaObject(String arg0) throws SQLException {
		try {
			return oc.getJavaObject(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getRemarksReporting()
	 */
	@Override
	public boolean getRemarksReporting() {
		return oc.getRemarksReporting();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getRestrictGetTables()
	 */
	@Override
	public boolean getRestrictGetTables() {
		return oc.getRestrictGetTables();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getSQLType(java.lang.Object)
	 */
	@Override
	public String getSQLType(Object arg0) throws SQLException {
		try {
			return oc.getSQLType(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getSessionTimeZone()
	 */
	@Override
	public String getSessionTimeZone() {
		return oc.getSessionTimeZone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getStmtCacheSize()
	 */
	@Override
	public int getStmtCacheSize() {
		return oc.getStmtCacheSize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getStructAttrCsId()
	 */
	@Override
	public short getStructAttrCsId() throws SQLException {
		try {
			return oc.getStructAttrCsId();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getUserName()
	 */
	@Override
	public String getUserName() throws SQLException {
		try {
			return oc.getUserName();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getUsingXAFlag()
	 */
	@Override
	public boolean getUsingXAFlag() {
		return oc.getUsingXAFlag();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#getXAErrorFlag()
	 */
	@Override
	public boolean getXAErrorFlag() {
		return oc.getXAErrorFlag();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#isLogicalConnection()
	 */
	@Override
	public boolean isLogicalConnection() {
		return oc.isLogicalConnection();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#pingDatabase(int)
	 */
	@Override
	public int pingDatabase(int arg0) throws SQLException {
		try {
			return oc.pingDatabase(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#prepareCallWithKey(java.lang.String)
	 */
	@Override
	public CallableStatement prepareCallWithKey(String arg0) throws SQLException {
		try {
			return new OracleCallableStatementImpl(this, oc.prepareCallWithKey(arg0), arg0, jdbcConfig, isTracingAPI);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#prepareStatementWithKey(java.lang.String)
	 */
	@Override
	public PreparedStatement prepareStatementWithKey(String arg0) throws SQLException {
		try {
			return new OraclePreparedStatementImpl(this, oc.prepareStatementWithKey(arg0), arg0, jdbcConfig, isTracingAPI);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#putDescriptor(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void putDescriptor(String arg0, Object arg1) throws SQLException {
		try {
			oc.putDescriptor(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#registerSQLType(java.lang.String,
	 *      java.lang.Class)
	 */
	@Override
	public void registerSQLType(String arg0, @SuppressWarnings("rawtypes") Class arg1) throws SQLException {
		try {
			oc.registerSQLType(arg0, arg1);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#registerSQLType(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void registerSQLType(String arg0, String arg1) throws SQLException {
		try {
			oc.registerSQLType(arg0, arg1);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#registerTAFCallback(oracle.jdbc.OracleOCIFailover,
	 *      java.lang.Object)
	 */
	@Override
	public void registerTAFCallback(OracleOCIFailover arg0, Object arg1) throws SQLException {
		try {
			oc.registerTAFCallback(arg0, arg1);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setAutoClose(boolean)
	 */
	@Override
	public void setAutoClose(boolean arg0) throws SQLException {
		try {
			oc.setAutoClose(arg0);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setCreateStatementAsRefCursor(boolean)
	 */
	@Override
	public void setCreateStatementAsRefCursor(boolean arg0) {
		oc.setCreateStatementAsRefCursor(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setDefaultExecuteBatch(int)
	 */
	@Override
	public void setDefaultExecuteBatch(int arg0) throws SQLException {
		try {
			oc.setDefaultExecuteBatch(arg0);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setDefaultRowPrefetch(int)
	 */
	@Override
	public void setDefaultRowPrefetch(int arg0) throws SQLException {
		try {
			oc.setDefaultRowPrefetch(arg0);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setIncludeSynonyms(boolean)
	 */
	@Override
	public void setIncludeSynonyms(boolean arg0) {
		oc.setIncludeSynonyms(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setRemarksReporting(boolean)
	 */
	@Override
	public void setRemarksReporting(boolean arg0) {
		oc.setRemarksReporting(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setRestrictGetTables(boolean)
	 */
	@Override
	public void setRestrictGetTables(boolean arg0) {
		oc.setRestrictGetTables(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setSessionTimeZone(java.lang.String)
	 */
	@Override
	public void setSessionTimeZone(String arg0) throws SQLException {
		try {
			oc.setSessionTimeZone(arg0);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setStmtCacheSize(int)
	 */
	@Override
	public void setStmtCacheSize(int arg0) throws SQLException {
		try {
			oc.setStmtCacheSize(arg0);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setStmtCacheSize(int, boolean)
	 */
	@Override
	public void setStmtCacheSize(int arg0, boolean arg1) throws SQLException {
		try {
			oc.setStmtCacheSize(arg0, arg1);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setUsingXAFlag(boolean)
	 */
	@Override
	public void setUsingXAFlag(boolean arg0) {
		oc.setUsingXAFlag(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#setXAErrorFlag(boolean)
	 */
	@Override
	public void setXAErrorFlag(boolean arg0) {
		oc.setXAErrorFlag(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OracleConnection#startup(java.lang.String, int)
	 */
	@Override
	public void startup(String arg0, int arg1) throws SQLException {
		try {
			oc.startup(arg0, arg1);
		} catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#getCallWithKey(java.lang.String)
	 */
	@Override
	public CallableStatement getCallWithKey(String arg0) throws SQLException {
		try {
			return oc.getCallWithKey(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#getExplicitCachingEnabled()
	 */
	@Override
	public boolean getExplicitCachingEnabled() throws SQLException {
		try {
			return oc.getExplicitCachingEnabled();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#getImplicitCachingEnabled()
	 */
	@Override
	public boolean getImplicitCachingEnabled() throws SQLException {
		try {
			return oc.getImplicitCachingEnabled();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#getStatementCacheSize()
	 */
	@Override
	public int getStatementCacheSize() throws SQLException {
		try {
			return oc.getStatementCacheSize();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#getStatementWithKey(java.lang.String)
	 */
	@Override
	public PreparedStatement getStatementWithKey(String arg0) throws SQLException {
		try {
			return oc.getStatementWithKey(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#oracleReleaseSavepoint(oracle.jdbc.OracleSavepoint)
	 */
	@Override
	public void oracleReleaseSavepoint(OracleSavepoint arg0) throws SQLException {
		try {
			oc.oracleReleaseSavepoint(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#oracleRollback(oracle.jdbc.OracleSavepoint)
	 */
	@Override
	public void oracleRollback(OracleSavepoint arg0) throws SQLException {
		try {
			oc.oracleRollback(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#oracleSetSavepoint()
	 */
	@Override
	public OracleSavepoint oracleSetSavepoint() throws SQLException {
		try {
			return oc.oracleSetSavepoint();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#oracleSetSavepoint(java.lang.String)
	 */
	@Override
	public OracleSavepoint oracleSetSavepoint(String arg0) throws SQLException {
		try {
			return oc.oracleSetSavepoint(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#purgeExplicitCache()
	 */
	@Override
	public void purgeExplicitCache() throws SQLException {
		try {
			oc.purgeExplicitCache();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#purgeImplicitCache()
	 */
	@Override
	public void purgeImplicitCache() throws SQLException {
		try {
			oc.purgeImplicitCache();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#setExplicitCachingEnabled(boolean)
	 */
	@Override
	public void setExplicitCachingEnabled(boolean arg0) throws SQLException {
		try {
			oc.setExplicitCachingEnabled(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#setImplicitCachingEnabled(boolean)
	 */
	@Override
	public void setImplicitCachingEnabled(boolean arg0) throws SQLException {
		try {
			oc.setImplicitCachingEnabled(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#setStatementCacheSize(int)
	 */
	@Override
	public void setStatementCacheSize(int arg0) throws SQLException {
		try {
			oc.setStatementCacheSize(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#setWrapper(oracle.jdbc.OracleConnection)
	 */
	@Override
	public void setWrapper(OracleConnection arg0) {
		oc.setWrapper(arg0);
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#unwrap()
	 */
	@Override
	public OracleConnection unwrap() {
		return oc.unwrap();
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleConnection#getProperties()
	 */
	@Override
	public Properties getProperties() {
		return oc.getProperties();
	}

	@Override
	public void abort() throws SQLException {
		oc.abort();
		
	}

	@Override
	public void applyConnectionAttributes(Properties arg0) throws SQLException {
		oc.applyConnectionAttributes(arg0);
	}

	@Override
	public void cancel() throws SQLException {
		oc.cancel();
	}

	@Override
	public void clearAllApplicationContext(String arg0) throws SQLException {
		oc.clearAllApplicationContext(arg0);
	}

	@Override
	public void close(Properties arg0) throws SQLException {
		oc.close(arg0);
	}

	@Override
	public void close(int arg0) throws SQLException {
		oc.close(arg0);
	}

	@Override
	public void commit(EnumSet<CommitOption> arg0) throws SQLException {
		oc.commit(arg0);
	}

	@Override
	public ARRAY createARRAY(String arg0, Object arg1) throws SQLException {
		return oc.createARRAY(arg0, arg1);
	}

	@Override
	public BINARY_DOUBLE createBINARY_DOUBLE(double arg0) throws SQLException {
		return oc.createBINARY_DOUBLE(arg0);
	}

	@Override
	public BINARY_FLOAT createBINARY_FLOAT(float arg0) throws SQLException {
		return oc.createBINARY_FLOAT(arg0);
	}

	@Override
	public DATE createDATE(Date arg0) throws SQLException {
		return oc.createDATE(arg0);
	}

	@Override
	public DATE createDATE(Time arg0) throws SQLException {
		return oc.createDATE(arg0);
	}

	@Override
	public DATE createDATE(Timestamp arg0) throws SQLException {
		return oc.createDATE(arg0);
	}

	@Override
	public DATE createDATE(String arg0) throws SQLException {
		return oc.createDATE(arg0);
	}

	@Override
	public DATE createDATE(Date arg0, Calendar arg1) throws SQLException {
		return oc.createDATE(arg0, arg1);
	}

	@Override
	public DATE createDATE(Time arg0, Calendar arg1) throws SQLException {
		return oc.createDATE(arg0, arg1);
	}

	@Override
	public DATE createDATE(Timestamp arg0, Calendar arg1) throws SQLException {
		return oc.createDATE(arg0, arg1);
	}

	@Override
	public INTERVALDS createINTERVALDS(String arg0) throws SQLException {
		return oc.createINTERVALDS(arg0);
	}

	@Override
	public INTERVALYM createINTERVALYM(String arg0) throws SQLException {
		return oc.createINTERVALYM(arg0);
	}

	@Override
	public NUMBER createNUMBER(boolean arg0) throws SQLException {
		return oc.createNUMBER(arg0);
	}

	@Override
	public NUMBER createNUMBER(byte arg0) throws SQLException {
		return oc.createNUMBER(arg0);
	}

	@Override
	public NUMBER createNUMBER(short arg0) throws SQLException {
		return oc.createNUMBER(arg0);
	}

	@Override
	public NUMBER createNUMBER(int arg0) throws SQLException {
		return oc.createNUMBER(arg0);
	}

	@Override
	public NUMBER createNUMBER(long arg0) throws SQLException {
		return oc.createNUMBER(arg0);
	}

	@Override
	public NUMBER createNUMBER(float arg0) throws SQLException {
		return oc.createNUMBER(arg0);
	}

	@Override
	public NUMBER createNUMBER(double arg0) throws SQLException {
		return oc.createNUMBER(arg0);
	}

	@Override
	public NUMBER createNUMBER(BigDecimal arg0) throws SQLException {
		return oc.createNUMBER(arg0);
	}

	@Override
	public NUMBER createNUMBER(BigInteger arg0) throws SQLException {
		return oc.createNUMBER(arg0);
	}

	@Override
	public NUMBER createNUMBER(String arg0, int arg1) throws SQLException {
		return oc.createNUMBER(arg0, arg1);
	}

	@Override
	public Array createOracleArray(String arg0, Object arg1) throws SQLException {
		return oc.createOracleArray(arg0, arg1);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Date arg0) throws SQLException {
		return oc.createTIMESTAMP(arg0);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(DATE arg0) throws SQLException {
		return oc.createTIMESTAMP(arg0);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Time arg0) throws SQLException {
		return oc.createTIMESTAMP(arg0);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Timestamp arg0) throws SQLException {
		return oc.createTIMESTAMP(arg0);
	}

	@Override
	public TIMESTAMP createTIMESTAMP(String arg0) throws SQLException {
		return oc.createTIMESTAMP(arg0);
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(Date arg0, Calendar arg1) throws SQLException {
		return oc.createTIMESTAMPLTZ(arg0, arg1);
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(Time arg0, Calendar arg1) throws SQLException {
		return oc.createTIMESTAMPLTZ(arg0, arg1);
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(Timestamp arg0, Calendar arg1) throws SQLException {
		return oc.createTIMESTAMPLTZ(arg0, arg1);
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(String arg0, Calendar arg1) throws SQLException {
		return oc.createTIMESTAMPLTZ(arg0, arg1);
	}

	@Override
	public TIMESTAMPLTZ createTIMESTAMPLTZ(DATE arg0, Calendar arg1) throws SQLException {
		return oc.createTIMESTAMPLTZ(arg0, arg1);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Date arg0) throws SQLException {
		return oc.createTIMESTAMPTZ(arg0);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Time arg0) throws SQLException {
		return oc.createTIMESTAMPTZ(arg0);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp arg0) throws SQLException {
		return oc.createTIMESTAMPTZ(arg0);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(String arg0) throws SQLException {
		return oc.createTIMESTAMPTZ(arg0);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(DATE arg0) throws SQLException {
		return oc.createTIMESTAMPTZ(arg0);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Date arg0, Calendar arg1) throws SQLException {
		return oc.createTIMESTAMPTZ(arg0, arg1);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Time arg0, Calendar arg1) throws SQLException {
		return oc.createTIMESTAMPTZ(arg0, arg1);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp arg0, Calendar arg1) throws SQLException {
		return oc.createTIMESTAMPTZ(arg0, arg1);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(String arg0, Calendar arg1) throws SQLException {
		return oc.createTIMESTAMPTZ(arg0, arg1);
	}

	@Override
	public AQMessage dequeue(String arg0, AQDequeueOptions arg1, byte[] arg2) throws SQLException {
		return oc.dequeue(arg0, arg1, arg2);
	}

	@Override
	public AQMessage dequeue(String arg0, AQDequeueOptions arg1, String arg2) throws SQLException {
		return oc.dequeue(arg0, arg1, arg2);
	}

	@Override
	public void enqueue(String arg0, AQEnqueueOptions arg1, AQMessage arg2) throws SQLException {
		oc.enqueue(arg0, arg1, arg2);
	}

	@Override
	public TypeDescriptor[] getAllTypeDescriptorsInCurrentSchema() throws SQLException {
		return oc.getAllTypeDescriptorsInCurrentSchema();
	}

	@Override
	public String getAuthenticationAdaptorName() throws SQLException {
		return oc.getAuthenticationAdaptorName();
	}

	@Override
	public Properties getConnectionAttributes() throws SQLException {
		return oc.getConnectionAttributes();
	}

	@Override
	public int getConnectionReleasePriority() throws SQLException {
		return oc.getConnectionReleasePriority();
	}

	@Override
	public String getCurrentSchema() throws SQLException {
		return oc.getCurrentSchema();
	}

	@Override
	public String getDataIntegrityAlgorithmName() throws SQLException {
		return oc.getDataIntegrityAlgorithmName();
	}

	@Override
	public DatabaseChangeRegistration getDatabaseChangeRegistration(int arg0) throws SQLException {
		return oc.getDatabaseChangeRegistration(arg0);
	}

	@Override
	public TimeZone getDefaultTimeZone() throws SQLException {
		return oc.getDefaultTimeZone();
	}

	@Override
	public String getEncryptionAlgorithmName() throws SQLException {
		return oc.getEncryptionAlgorithmName();
	}

	@Override
	public short getEndToEndECIDSequenceNumber() throws SQLException {
		return oc.getEndToEndECIDSequenceNumber();
	}

	@Override
	public String[] getEndToEndMetrics() throws SQLException {
		return oc.getEndToEndMetrics();
	}

	@Override
	public String getSessionTimeZoneOffset() throws SQLException {
		return oc.getSessionTimeZoneOffset();
	}

	@Override
	public TypeDescriptor[] getTypeDescriptorsFromList(String[][] arg0) throws SQLException {
		return oc.getTypeDescriptorsFromList(arg0);
	}

	@Override
	public TypeDescriptor[] getTypeDescriptorsFromListInCurrentSchema(String[] arg0) throws SQLException {
		return oc.getTypeDescriptorsFromListInCurrentSchema(arg0);
	}

	@Override
	public Properties getUnMatchedConnectionAttributes() throws SQLException {
		return oc.getUnMatchedConnectionAttributes();
	}

	@Override
	public boolean isProxySession() {
		return oc.isProxySession();
	}

	@Override
	public boolean isUsable() {
		return oc.isUsable();
	}

	@Override
	public void openProxySession(int arg0, Properties arg1) throws SQLException {
		oc.openProxySession(arg0, arg1);
	}

	@Override
	public oracle.jdbc.internal.OracleConnection physicalConnectionWithin() {
		return oc.physicalConnectionWithin();
	}

	@Override
	public int pingDatabase() throws SQLException {
		return oc.pingDatabase();
	}

	@Override
	public AQNotificationRegistration[] registerAQNotification(String[] arg0, Properties[] arg1, Properties arg2)
			throws SQLException {
		return oc.registerAQNotification(arg0, arg1, arg2);
	}

	@Override
	public void registerConnectionCacheCallback(OracleConnectionCacheCallback arg0, Object arg1, int arg2)
			throws SQLException {
		oc.registerConnectionCacheCallback(arg0, arg1, arg2);
	}

	@Override
	public DatabaseChangeRegistration registerDatabaseChangeNotification(Properties arg0) throws SQLException {
		return oc.registerDatabaseChangeNotification(arg0);
	}

	@Override
	public void setApplicationContext(String arg0, String arg1, String arg2) throws SQLException {
		oc.setApplicationContext(arg0, arg1, arg2);
	}

	@Override
	public void setConnectionReleasePriority(int arg0) throws SQLException {
		oc.setConnectionReleasePriority(arg0);
	}

	@Override
	public void setDefaultTimeZone(TimeZone arg0) throws SQLException {
		oc.setDefaultTimeZone(arg0);
	}

	@Override
	public void setEndToEndMetrics(String[] arg0, short arg1) throws SQLException {
		oc.setEndToEndMetrics(arg0, arg1);
	}

	@Override
	public void setPlsqlWarnings(String arg0) throws SQLException {
		oc.setPlsqlWarnings(arg0);
	}

	@Override
	public void shutdown(DatabaseShutdownMode arg0) throws SQLException {
		oc.shutdown(arg0);
	}

	@Override
	public void startup(DatabaseStartupMode arg0) throws SQLException {
		oc.startup(arg0);
	}

	@Override
	public void unregisterAQNotification(AQNotificationRegistration arg0) throws SQLException {
		oc.unregisterAQNotification(arg0);
	}

	@Override
	public void unregisterDatabaseChangeNotification(DatabaseChangeRegistration arg0) throws SQLException {
		oc.unregisterDatabaseChangeNotification(arg0);
	}

	@Override
	public void unregisterDatabaseChangeNotification(int arg0) throws SQLException {
		oc.unregisterDatabaseChangeNotification(arg0);
	}

	@Override
	public void unregisterDatabaseChangeNotification(long arg0, String arg1) throws SQLException {
		oc.unregisterDatabaseChangeNotification(arg0, arg1);
	}

	@Override
	public void unregisterDatabaseChangeNotification(int arg0, String arg1, int arg2) throws SQLException {
		oc.unregisterDatabaseChangeNotification(arg0, arg1, arg2);
	}

	@Override
	public void addLogicalTransactionIdEventListener(LogicalTransactionIdEventListener arg0) throws SQLException {
		oc.addLogicalTransactionIdEventListener(arg0);
	}

	@Override
	public void addLogicalTransactionIdEventListener(LogicalTransactionIdEventListener arg0, Executor arg1)
			throws SQLException {
		oc.addLogicalTransactionIdEventListener(arg0, arg1);
	}

	@Override
	public boolean attachServerConnection() throws SQLException {
		return oc.attachServerConnection();
	}

	@Override
	public void detachServerConnection(String arg0) throws SQLException {
		oc.detachServerConnection(arg0);
	}

	@Override
	public LogicalTransactionId getLogicalTransactionId() throws SQLException {
		return oc.getLogicalTransactionId();
	}

	@Override
	public boolean isDRCPEnabled() throws SQLException {
		return oc.isDRCPEnabled();
	}

	@Override
	public boolean needToPurgeStatementCache() throws SQLException {
		return oc.needToPurgeStatementCache();
	}

	@Override
	public void removeLogicalTransactionIdEventListener(LogicalTransactionIdEventListener arg0) throws SQLException {
		oc.removeLogicalTransactionIdEventListener(arg0);
	}

	@Override
	public void beginRequest() throws SQLException {
		oc.beginRequest();
	}

	@Override
	public TIMESTAMP createTIMESTAMP(Timestamp arg0, Calendar arg1) throws SQLException {
		return oc.createTIMESTAMP(arg0, arg1);
	}

	@Override
	public TIMESTAMPTZ createTIMESTAMPTZ(Timestamp arg0, ZoneId arg1) throws SQLException {
		return oc.createTIMESTAMPTZ(arg0, arg1);
	}

	@Override
	public void endRequest() throws SQLException {
		oc.endRequest();
	}

	@Override
	public String getDRCPPLSQLCallbackName() throws SQLException {
		return oc.getDRCPPLSQLCallbackName();
	}

	@Override
	public String getDRCPReturnTag() throws SQLException {
		return oc.getDRCPReturnTag();
	}

	@Override
	public DRCPState getDRCPState() throws SQLException {
		return oc.getDRCPState();
	}

	@Override
	public boolean isDRCPMultitagEnabled() throws SQLException {
		return oc.isDRCPMultitagEnabled();
	}

	@Override
	public boolean isValid(ConnectionValidation arg0, int arg1) throws SQLException {
		return oc.isValid(arg0, arg1);
	}

	@Override
	public void setShardingKey(OracleShardingKey arg0, OracleShardingKey arg1) throws SQLException {
		oc.setShardingKey(arg0, arg1);
	}

	@Override
	public boolean setShardingKeyIfValid(OracleShardingKey arg0, OracleShardingKey arg1, int arg2) throws SQLException {
		return oc.setShardingKeyIfValid(arg0, arg1, arg2);
	}

	@Override
	public void startup(DatabaseStartupMode arg0, String arg1) throws SQLException {
		oc.startup(arg0, arg1);
		
	}

}
