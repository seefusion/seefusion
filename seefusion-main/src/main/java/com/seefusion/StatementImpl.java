/*
 * StatementImpl.java
 *
 * Wraps a JDBC statement object when one is requested from a Connection
 */

package com.seefusion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.AbstractList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * java.sql.Statement wrapper
 * 
 */
class StatementImpl implements java.sql.Statement {
	
	private static final Logger LOG = Logger.getLogger(StatementImpl.class.getName());

	protected java.sql.Statement s;

	protected ConnectionImpl c;

	private Object objectNumLock = new Object();

	private static long prevObjectNum = 0;

	private long objectNum;

	protected QueryInfo qi = null;

	protected RequestInfo pi = null;

	private Thread lastThread = null;

	private boolean isKilled = false;

	static String CLASSNAME = "Statement";

	private AbstractList<String> hideParametersKeywordList;

	private Properties sfInfo;

	private int batchCount = 0;

	// used by PreparedStatement:
	protected boolean firstParameter = true;

	protected JdbcConfig jdbcConfig;

	protected boolean isTracingAPI = false;

	StatementImpl(ConnectionImpl c, java.sql.Statement s, JdbcConfig jdbcConfig, boolean isTracingAPI) throws SQLException {
		this.c = c;
		this.s = s;
		this.isTracingAPI = isTracingAPI;
		if (c.sfUrlOptions.getProperty("logstatement") != null
				|| c.sfUrlOptions.getProperty("logall") != null) {
			isTracingAPI = true;
		}
		this.objectNum = getNextNum();
		this.jdbcConfig = jdbcConfig;
		newQuery();

		// log("Created for connection " + c.getObjectNumber());
	}

	ResultSet wrapResultSet(ResultSet rs) {
		return new ResultSetImpl(this, rs, qi, jdbcConfig, isTracingAPI);
	}
	
	void throwSQLException() throws SQLException {
		log("Throwing SQL Exception for page kill request");
		if (pi != null) {
			pi.hasJustThrownADamnedException(true);
		}
		SeeFusionKillSQLException e;
		if(killMessage == null) {
			e = new SeeFusionKillSQLException();
		}
		else {
			e = new SeeFusionKillSQLException(killMessage);
		}
		setException(e.toString());
		throw e;
	}

	String killMessage;
	boolean kill(String s) {
		killMessage = s;
		return kill();
	}
	
	boolean kill() {
		isKilled = true;
		ResultSetImpl rs = qi.getResultSet();
		if (rs != null) {
			rs.kill();
		}
		try {
			cancel();
			return true;
		}
		catch (SQLException e) {
			return false;
		}
	}

	boolean isHidingParameters = false;

	boolean isHidingParameters() {
		return this.isHidingParameters;
	}

	void setHidingParameters(boolean foo) {
		this.isHidingParameters = foo;
	}

	QueryInfo getQueryInfo() {
		return qi;
	}

	String getSql() {
		return this.qi.getQueryText();
	}

	void setSql(String sql) {
		if (hideParametersKeywordList != null && !hideParametersKeywordList.isEmpty()) {
			sql = hideSQL(sql);
		}
		setQueryText(sql);
	}

	void addSql(String sql) {
		if (hideParametersKeywordList != null && !hideParametersKeywordList.isEmpty()) {
			sql = hideSQL(sql);
		}
		batchCount++;
		if (batchCount == 1) {
			setQueryText("Batch 1: \r\n" + sql);
		}
		else {
			String currentSql = qi.getQueryText();
			setQueryText(currentSql + "Batch " + batchCount + ": \r\n" + sql);
		}
	}

	String hideSQL(String sql) {
		boolean hideSQL = jdbcConfig.checkForHideParameters(sql);
		setHidingParameters(hideSQL);
		if (hideSQL) {
			String sqlLower = sql.toLowerCase();
			int wherePos = sqlLower.indexOf("where");
			int valuesPos = sqlLower.indexOf("values");
			int setPos = sqlLower.indexOf("set");
			if (wherePos == -1) {
				wherePos = Integer.MAX_VALUE;
			}
			else {
				wherePos += 5;
			}
			if (valuesPos == -1) {
				valuesPos = Integer.MAX_VALUE;
			}
			else {
				valuesPos += 6;
			}
			if (setPos == -1) {
				setPos = Integer.MAX_VALUE;
			}
			else {
				setPos += 3;
			}
			int minPos = Math.min(wherePos, Math.min(valuesPos, setPos));
			if (minPos < sql.length()) {
				return (sql.substring(0, minPos) + "...(Parameters Hidden)");
			}
			else {
				return ("(SQL Hidden)");
			}
		}
		else {
			return (sql);
		}
	}

	void setQueryText(String sql) {
		if (qi != null) {
			this.qi.setQueryText(sql);
		}
	}

	void newQuery() throws SQLException {
		java.util.List<String> debugStackTargets = null;
		// is this statement cached and reused by multiple threads or pages?
		if (pi == null || !pi.isActive() || Thread.currentThread() != this.lastThread) {
			// (re)associate to the currently relevant PageInfo
			pi = RequestInfo.getCurrent();
			if (pi != null) {
				if (pi.isKilled()) {
					setInactive();
					if (pi.hasAlreadyThrownADamnedException()) {
						log("Throwing Unchecked Exception for page kill request (application tried to query again after SQLException)");
						SeeFusionKillError e = new SeeFusionKillError();
						setException(e.toString());
						throw e;
					}
					else {
						throwSQLException();
					}
				}
				debugStackTargets = pi.getSeeFusion().getDebugStackTargets();
			}
		}
		// because preparedStatement/CallableStatement can be called several
		// times
		// we create a new queryInfo once this is inactive
		// so that the PageInfo can properly track LongestQuery
		if (qi == null) {
			qi = new QueryInfo(this, pi, debugStackTargets);
		}
		else {
			// copy query text
			qi = new QueryInfo(this, pi, debugStackTargets, qi);
		}
		if (pi != null) {
			pi.setQueryInfo(qi);
		}
	}

	void setActive() throws SQLException {
		// it's possible for this.qi to be null if the page is flagged to be
		// killed while the constructor is being called
		if (this.qi != null) {
			this.qi.setActive();
		}
	}

	void setInactive() throws SQLException {
		if (this.qi != null) {
			this.qi.setInactive();
		}
	}

	void setException(String exceptionText) {
		if (qi != null) {
			qi.setException(exceptionText);
		}
	}

	long getNextNum() {
		synchronized (objectNumLock) {
			prevObjectNum++;
			return prevObjectNum;
		}
	}

	@Override
	public void close() throws SQLException {
		if (isTracingAPI) trace("close()");
		s.close();
		setInactive();
	}

	@Override
	public java.sql.Connection getConnection() throws SQLException {
		return c;
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		if (isTracingAPI) trace("addBatch(sql)");
		setSql(sql);
		s.addBatch(sql);
	}
	
	@Override
	public final ResultSet executeQuery(String sql) throws SQLException {
		if (isTracingAPI) trace("executeQuery(sql)");
		setSql(sql);
		setActive();
		qi.setSimpleQuery(true);
		try {
			if (pi == null) {
				return s.executeQuery(sql);
			}
			else {
				return wrapResultSet(s.executeQuery(sql));
			}
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
		finally {
			setInactive();
		}
	}
	
	@Override
	public boolean execute(String sql) throws SQLException {
		if (isTracingAPI) trace("execute(sql)");
		setSql(sql);
		setActive();
		try {
			boolean ret = s.execute(sql);
			if (isKilled) {
				throwSQLException();
			}
			return ret;
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
		finally {
			// don't mark inactive if it contains a SELECT statement
			if (Util.isSelectStatement(qi.getQueryTextOnly())) {
				qi.setSimpleQuery(true);
			}
			setInactive();
		}
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		if (isTracingAPI) {
			if (columnNames == null) {
				trace("execute(sql, columnNames[]=null)");
			}
			else {
				trace("execute(sql, columnNames[" + columnNames.length + "])");
			}
		}
		setSql(sql);
		setActive();
		try {
			return s.execute(sql, columnNames);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
		finally {
			setInactive();
		}

	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		if (isTracingAPI) {
			if (columnIndexes == null) {
				trace("execute(sql, columnIndexes[]=null)");
			}
			else {
				trace("execute(sql, columnIndexes[" + columnIndexes.length + "])");
			}
		}
		setSql(sql);
		setActive();
		try {
			return s.execute(sql, columnIndexes);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
		finally {
			setInactive();
		}
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		if (isTracingAPI) trace("execute(sql, autoGeneratedKeys=" + autoGeneratedKeys + ")");
		setSql(sql);
		setActive();
		try {
			return s.execute(sql, autoGeneratedKeys);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
		finally {
			setInactive();
		}
	}

	@Override
	public int[] executeBatch() throws SQLException {
		if (isTracingAPI) trace("executeBatch()");
		setActive();
		try {
			int[] ret = s.executeBatch();
			batchCount = 0;
			return ret;
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
		finally {
			setInactive();
		}
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		if (isTracingAPI) trace("executeUpdate(sql)");
		setSql(sql);
		setActive();
		try {
			int ret = s.executeUpdate(sql);
			return ret;
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
		finally {
			setInactive();
		}
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		if (isTracingAPI) trace("executeUpdate(sql, autoGeneratedKeys=" + autoGeneratedKeys + ")");
		setSql(sql);
		setActive();
		try {
			int ret = s.executeUpdate(sql, autoGeneratedKeys);
			return ret;
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
		finally {
			setInactive();
		}
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		if (isTracingAPI) {
			if (columnIndexes == null) {
				trace("executeUpdate(sql, columnIndexes[]=null)");
			}
			else {
				trace("executeUpdate(sql, columnIndexes[" + columnIndexes.length + "])");
			}
		}
		setSql(sql);
		setActive();
		try {
			int ret = s.executeUpdate(sql, columnIndexes);
			return ret;
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
		finally {
			setInactive();
		}
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		if (isTracingAPI) {
			if (columnNames == null) {
				trace("executeUpdate(sql, columnIndexes[]=null)");
			}
			else {
				trace("executeUpdate(sql, columnIndexes[" + columnNames.length + "])");
			}
		}
		setSql(sql);
		setActive();
		try {
			int ret = s.executeUpdate(sql, columnNames);
			return ret;
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
		finally {
			setInactive();
		}
	}

	@Override
	public void cancel() throws SQLException {
		if (isTracingAPI) trace("cancel()");
		s.cancel();
	}

	void resetParameterList() {
		this.firstParameter = true;
	}

	@Override
	public void clearBatch() throws SQLException {
		if (isTracingAPI) trace("clearBatch()");
		resetParameterList();
		this.batchCount = 0;
		s.clearBatch();
	}

	@Override
	public void clearWarnings() throws SQLException {
		try {
			if (isTracingAPI) trace("clearWarnings()");
			s.clearWarnings();
		}
		catch (SQLException e) {
			if (isTracingAPI) trace("clearWarnings() threw exception");
			logException(e);
			throw e;
		}
	}

	@Override
	public int getFetchDirection() throws SQLException {
		try {
			int ret = s.getFetchDirection();
			if (isTracingAPI) trace("getFetchDirection() = " + ret);
			return ret;
		}
		catch (SQLException e) {
			if (isTracingAPI) trace("getFetchDirection() threw exception");
			logException(e);
			throw e;
		}
	}

	@Override
	public int getFetchSize() throws SQLException {
		try {
			int ret = s.getFetchSize();
			if (isTracingAPI) trace("getFetchSize() = " + ret);
			return ret;
		}
		catch (SQLException e) {
			if (isTracingAPI) trace("getFetchSize() threw exception");
			logException(e);
			throw e;
		}
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		if (isTracingAPI) trace("getGeneratedKeys()");
		try {
			return s.getGeneratedKeys();
		}
		catch (SQLException e) {
			if (isTracingAPI) trace("getGeneratedKeys() threw exception");
			// commented 20071031 because CF is calling this on updates, and it's filling logs
			//logException(e);
			throw e;
		}
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		if (isTracingAPI) trace("getMaxFieldSize()");
		return s.getMaxFieldSize();
	}

	@Override
	public int getMaxRows() throws SQLException {
		if (isTracingAPI) trace("getMaxRows()");
		return s.getMaxRows();
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		if (isTracingAPI) trace("getQueryTimeout()");
		return s.getQueryTimeout();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		if (isTracingAPI) trace("getResultSetConcurrency()");
		return s.getResultSetConcurrency();
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		if (isTracingAPI) trace("getResultSetHoldability()");
		return s.getResultSetHoldability();
	}

	@Override
	public int getResultSetType() throws SQLException {
		if (isTracingAPI) trace("getResultSetType()");
		return s.getResultSetType();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		try {
			SQLWarning ret = s.getWarnings(); 
			if (isTracingAPI) trace("getWarnings() = " + ret);
			return ret;
		}
		catch (SQLException e) {
			if (isTracingAPI) trace("getWarnings() threw exception");
			logException(e);
			throw e;
		}
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		if (isTracingAPI) trace("setCursorName(" + name + ")");
		s.setCursorName(name);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		if (isTracingAPI) trace("setEscapeProcessing(" + enable + ")");
		s.setEscapeProcessing(enable);
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		if (isTracingAPI) trace("setFetchDirection(" + direction + ")");
		s.setFetchDirection(direction);
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		if (isTracingAPI) trace("setFetchSize(" + rows + ")");
		s.setFetchSize(rows);
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		if (isTracingAPI) trace("setMaxFieldSize(" + max + ")");
		if (c.isIgnoringDuplicateMaxFieldSize()) {
			if (max != c.getLastMaxFieldSize()) {
				// log("setMaxFieldSize("+max+")");
				c.setLastMaxFieldSize(max);
				s.setMaxFieldSize(max);
			}
		}
		else {
			s.setMaxFieldSize(max);
		}
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		if (isTracingAPI) trace("setMaxRows(" + max + ")");
		s.setMaxRows(max);
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		if (isTracingAPI) trace("setQueryTimeout(" + seconds + "s)");
		s.setQueryTimeout(seconds);
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		try {
			if(s.getMoreResults()) {
				if (isTracingAPI) trace("getMoreResults() = true");
				return true;
			} else {
				if (isTracingAPI) trace("getMoreResults() = false");
				return false;
			}
		}
		catch (SQLException e) {
			if (isTracingAPI) trace("getMoreResults() threw exception");
			logException(e);
			throw e;
		}
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		try {
			if(s.getMoreResults(current)) {
				if (isTracingAPI) trace("getMoreResults(" + current + ") = true");
				return true;
			} else {
				if (isTracingAPI) trace("getMoreResults(" + current + ") = false");
				return false;
			}
		}
		catch (SQLException e) {
			if (isTracingAPI) trace("getMoreResults(" + current + ") threw exception");
			logException(e);
			throw e;
		}
	}

	@Override
	public final ResultSet getResultSet() throws SQLException {
		try {
			ResultSet ret = s.getResultSet();
			if (isTracingAPI) { if(ret==null) trace("getResultSet() = null"); else trace("getResultSet() = ResultSet"); } 
			// debug("getResultSet(): " + ret.toString());
			if (ret == null) {
				return ret;
			}
			else {
				return wrapResultSet(ret);
			}
		}
		catch (SQLException e) {
			if (isTracingAPI) trace("getResultSet() threw exception"); 
			logException(e);
			setInactive();
			throw e;
		}
	}

	@Override
	public int getUpdateCount() throws SQLException {
		try {
			int ret = s.getUpdateCount(); 
			if (isTracingAPI) trace("getUpdateCount() = " + ret); 
			return ret;
		}
		catch (SQLException e) {
			if (isTracingAPI) trace("getUpdateCount() threw exception"); 
			logException(e);
			setInactive();
			throw e;
		}
	}

	Properties getSfInfo() {
		return sfInfo;
	}

	String getSfInfo(String name) {
		return sfInfo.getProperty(name);
	}

	void logException(Exception e) throws SQLException {
		setException(e.toString());
		LOG.log(Level.SEVERE, CLASSNAME + "[" + objectNum + "]: ", e);
		setInactive();
	}

	void trace(String s) {
		if (isTracingAPI) {
			log(s);
		}
	}

	void debug(String s) {
		LOG.fine(CLASSNAME + "[" + objectNum + "]: " + s);
	}

	void log(String s) {
		LOG.info(CLASSNAME + "[" + objectNum + "]: " + s);
	}

	@Override
	public boolean isClosed() throws SQLException {
		return s.isClosed();
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return s.isPoolable();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		s.setPoolable(poolable);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(iface.isInstance(s))
			return true;
		else 	
			return s.isWrapperFor(iface);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(s))
			return (T) s;
		else 	
			return s.unwrap(iface);
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		s.closeOnCompletion();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return s.isCloseOnCompletion();
	}
	@Override
	public long[] executeLargeBatch() throws SQLException {
		return s.executeLargeBatch();
	}

	@Override
	public long executeLargeUpdate(String arg0, int arg1) throws SQLException {
		return s.executeLargeUpdate(arg0, arg1);
	}

	@Override
	public long executeLargeUpdate(String arg0, int[] arg1) throws SQLException {
		return s.executeLargeUpdate(arg0, arg1);
	}

	@Override
	public long executeLargeUpdate(String arg0, String[] arg1) throws SQLException {
		return s.executeLargeUpdate(arg0, arg1);
	}

	@Override
	public long executeLargeUpdate(String arg0) throws SQLException {
		return s.executeLargeUpdate(arg0);
	}

	@Override
	public long getLargeMaxRows() throws SQLException {
		return s.getLargeMaxRows();
	}

	@Override
	public long getLargeUpdateCount() throws SQLException {
		return s.getLargeUpdateCount();
	}

	@Override
	public void setLargeMaxRows(long arg0) throws SQLException {
		s.setLargeMaxRows(arg0);
	}


}
