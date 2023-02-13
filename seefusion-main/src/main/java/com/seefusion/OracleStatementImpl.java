/**
 * 
 */
package com.seefusion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.OracleStatement;
import oracle.jdbc.dcn.DatabaseChangeRegistration;

/**
 * @author Daryl
 *
 */
@SuppressWarnings("deprecation")
class OracleStatementImpl extends StatementImpl implements OracleStatement {
	
	OracleStatement os;
	
	OracleStatementImpl(OracleConnectionImpl c, Statement s, JdbcConfig jdbcConfig, boolean isTracingAPI) throws SQLException {
		super(c, s, jdbcConfig, isTracingAPI);
		if( s instanceof OracleStatement ) {
			this.os = (OracleStatement)s;
		}
	}
	
	////////////////////////// JDBC STATEMENT METHODS //////////////////////////
	// (required so we return Oracle objects, not JDBC objects)

	public ResultSet getGeneratedKeys() throws SQLException {
		if (isTracingAPI) trace("getGeneratedKeys()");
		try {
			if (pi == null) {
				return s.getGeneratedKeys();
			}
			else {
				return new OracleResultSetImpl(this, s.getGeneratedKeys(), qi, jdbcConfig, isTracingAPI);
			}
		}
		catch (SQLException e) {
			if (isTracingAPI) trace("getGeneratedKeys() threw exception");
			//logException(e);
			throw e;
		}
	}
	
	////////////////////////// ORACLE METHODS ////////////////////////
	
	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleStatement#clearDefines()
	 */
	public void clearDefines() throws SQLException {
		try {
			os.clearDefines();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleStatement#closeWithKey(java.lang.String)
	 */
	public void closeWithKey(String arg0) throws SQLException {
		try {
			os.closeWithKey(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleStatement#creationState()
	 */
	public int creationState() {
			return os.creationState();
	}
	
	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleStatement#defineColumnType(int, int)
	 */
	public void defineColumnType(int arg0, int arg1) throws SQLException {
		try {
			os.defineColumnType(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}	
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleStatement#defineColumnType(int, int, int)
	 */
	public void defineColumnType(int arg0, int arg1, int arg2) throws SQLException {
		try {
			os.defineColumnType(arg0, arg1, arg2);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleStatement#defineColumnType(int, int, java.lang.String)
	 */
	public void defineColumnType(int arg0, int arg1, String arg2) throws SQLException {
		try {
			os.defineColumnType(arg0, arg1, arg2);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleStatement#defineColumnTypeChars(int, int, int)
	 */
	public void defineColumnTypeChars(int arg0, int arg1, int arg2) throws SQLException {
		try {
			os.defineColumnTypeChars(arg0, arg1, arg2);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleStatement#getRowPrefetch()
	 */
	public int getRowPrefetch() {
		return os.getRowPrefetch();
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleStatement#setRowPrefetch(int)
	 */
	public void setRowPrefetch(int arg0) throws SQLException {
		try {
			os.setRowPrefetch(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleStatement#defineColumnTypeBytes(int, int, int)
	 */
	public void defineColumnTypeBytes(int arg0, int arg1, int arg2) throws SQLException {
		try {
			os.defineColumnTypeBytes(arg0, arg1, arg2);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleStatement#isNCHAR(int)
	 */
	public boolean isNCHAR(int arg0) throws SQLException {
		try {
			return os.isNCHAR(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public void defineColumnType(int arg0, int arg1, int arg2, short arg3) throws SQLException {
		os.defineColumnType(arg0, arg1, arg2, arg3);
	}

	@Override
	public int getLobPrefetchSize() throws SQLException {
		return os.getLobPrefetchSize();
	}

	@Override
	public long getRegisteredQueryId() throws SQLException {
		return os.getRegisteredQueryId();
	}

	@Override
	public String[] getRegisteredTableNames() throws SQLException {
		return os.getRegisteredTableNames();
	}

	@Override
	public void setDatabaseChangeRegistration(DatabaseChangeRegistration arg0) throws SQLException {
		os.setDatabaseChangeRegistration(arg0);
	}

	@Override
	public void setLobPrefetchSize(int arg0) throws SQLException {
		os.setLobPrefetchSize(arg0);
	}

	@Override
	ResultSet wrapResultSet(ResultSet rs) {
		return new OracleResultSetImpl(this, rs, qi, jdbcConfig, isTracingAPI);
	}
	
}
