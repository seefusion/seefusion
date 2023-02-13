/**
 * 
 */
package com.seefusion;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleDataFactory;
import oracle.jdbc.OracleParameterMetaData;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CustomDatum;
import oracle.sql.CustomDatumFactory;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

/**
 * @author Daryl
 *
 */
@SuppressWarnings("deprecation")
class OracleCallableStatementImpl extends CallableStatementImpl implements OracleCallableStatement {

	OracleCallableStatement os;

	OracleCallableStatementImpl(ConnectionImpl c, CallableStatement cs, String sql, JdbcConfig jdbcConfig,
			boolean isTracingAPI) throws SQLException {
		super(c, cs, sql, jdbcConfig, isTracingAPI);
		if (ps instanceof OracleCallableStatement) {
			os = (OracleCallableStatement) cs;
		}
	}
	
	@Override
	ResultSet wrapResultSet(ResultSet rs) {
		return new OracleResultSetImpl(this, rs, qi, jdbcConfig, isTracingAPI);
	}
	
	////////////////////////// ORACLE CALLABLESTATEMENT METHODS //////////////////////////
	
	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getARRAY(int)
	 */
	public ARRAY getARRAY(int arg0) throws SQLException {
		try {
			return os.getARRAY(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getAsciiStream(int)
	 */
	public InputStream getAsciiStream(int arg0) throws SQLException {
		try {
			return os.getAsciiStream(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getBFILE(int)
	 */
	public BFILE getBFILE(int arg0) throws SQLException {
		try {
			return os.getBFILE(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getBLOB(int)
	 */
	public BLOB getBLOB(int arg0) throws SQLException {
		try {
			return os.getBLOB(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getBinaryStream(int)
	 */
	public InputStream getBinaryStream(int arg0) throws SQLException {
		try {
			return os.getBinaryStream(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getCHAR(int)
	 */
	public CHAR getCHAR(int arg0) throws SQLException {
		try {
			return os.getCHAR(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getCLOB(int)
	 */
	public CLOB getCLOB(int arg0) throws SQLException {
		try {
			return os.getCLOB(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getCursor(int)
	 */
	public ResultSet getCursor(int arg0) throws SQLException {
		try {
			return os.getCursor(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getCustomDatum(int, oracle.sql.CustomDatumFactory)
	 */
	public Object getCustomDatum(int arg0, CustomDatumFactory arg1) throws SQLException {
		try {
			return os.getCustomDatum(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getDATE(int)
	 */
	public DATE getDATE(int arg0) throws SQLException {
		try {
			return os.getDATE(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getNUMBER(int)
	 */
	public NUMBER getNUMBER(int arg0) throws SQLException {
		try {
			return os.getNUMBER(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getOPAQUE(int)
	 */
	public OPAQUE getOPAQUE(int arg0) throws SQLException {
		try {
			return os.getOPAQUE(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getORAData(int, oracle.sql.ORADataFactory)
	 */
	public Object getORAData(int arg0, ORADataFactory arg1) throws SQLException {
		try {
			return os.getORAData(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getOracleObject(int)
	 */
	public Datum getOracleObject(int arg0) throws SQLException {
		try {
			return os.getOracleObject(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getOraclePlsqlIndexTable(int)
	 */
	public Datum[] getOraclePlsqlIndexTable(int arg0) throws SQLException {
		try {
			return os.getOraclePlsqlIndexTable(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getPlsqlIndexTable(int)
	 */
	public Object getPlsqlIndexTable(int arg0) throws SQLException {
		try {
			return os.getPlsqlIndexTable(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getPlsqlIndexTable(int, java.lang.Class)
	 */
	public Object getPlsqlIndexTable(int arg0, @SuppressWarnings("rawtypes") Class arg1) throws SQLException {
		try {
			return os.getPlsqlIndexTable(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getRAW(int)
	 */
	public RAW getRAW(int arg0) throws SQLException {
		try {
			return os.getRAW(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getREF(int)
	 */
	public REF getREF(int arg0) throws SQLException {
		try {
			return os.getREF(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getROWID(int)
	 */
	public ROWID getROWID(int arg0) throws SQLException {
		try {
			return os.getROWID(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getSTRUCT(int)
	 */
	public STRUCT getSTRUCT(int arg0) throws SQLException {
		try {
			return os.getSTRUCT(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getTIMESTAMP(int)
	 */
	public TIMESTAMP getTIMESTAMP(int arg0) throws SQLException {
		try {
			return os.getTIMESTAMP(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getTIMESTAMPLTZ(int)
	 */
	public TIMESTAMPLTZ getTIMESTAMPLTZ(int arg0) throws SQLException {
		try {
			return os.getTIMESTAMPLTZ(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getTIMESTAMPTZ(int)
	 */
	public TIMESTAMPTZ getTIMESTAMPTZ(int arg0) throws SQLException {
		try {
			return os.getTIMESTAMPTZ(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getUnicodeStream(int)
	 */
	public InputStream getUnicodeStream(int arg0) throws SQLException {
		try {
			return os.getUnicodeStream(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#registerIndexTableOutParameter(int, int, int, int)
	 */
	public void registerIndexTableOutParameter(int arg0, int arg1, int arg2, int arg3) throws SQLException {
		try {
			os.registerIndexTableOutParameter(arg0, arg1, arg2, arg3);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#registerOutParameter(int, int, int, int)
	 */
	public void registerOutParameter(int arg0, int arg1, int arg2, int arg3) throws SQLException {
		try {
			os.registerOutParameter(arg0, arg1, arg2, arg3);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}
	
	// //////////////////////// ORACLE PREPAREDSTATEMENT METHODS //////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#defineParameterType(int, int,
	 *      int)
	 */
	public void defineParameterType(int arg0, int arg1, int arg2) throws SQLException {
		try {
			os.defineParameterType(arg0, arg1, arg2);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#getExecuteBatch()
	 */
	public int getExecuteBatch() {
		return os.getExecuteBatch();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#sendBatch()
	 */
	public int sendBatch() throws SQLException {
		try {
			return os.sendBatch();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setARRAY(int, oracle.sql.ARRAY)
	 */
	public void setARRAY(int arg0, ARRAY arg1) throws SQLException {
		setParameter(arg0, "Oracle ARRAY");
		try {
			os.setARRAY(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setBFILE(int, oracle.sql.BFILE)
	 */
	public void setBFILE(int arg0, BFILE arg1) throws SQLException {
		setParameter(arg0, "Oracle BFILE");
		try {
			os.setBFILE(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setBLOB(int, oracle.sql.BLOB)
	 */
	public void setBLOB(int arg0, BLOB arg1) throws SQLException {
		setParameter(arg0, "Oracle BLOB");
		try {
			os.setBLOB(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setBfile(int, oracle.sql.BFILE)
	 */
	public void setBfile(int arg0, BFILE arg1) throws SQLException {
		setParameter(arg0, "Oracle BFILE");
		try {
			os.setBfile(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setCHAR(int, oracle.sql.CHAR)
	 */
	public void setCHAR(int arg0, CHAR arg1) throws SQLException {
		setParameter(arg0, "Oracle CHAR");
		try {
			os.setCHAR(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setCLOB(int, oracle.sql.CLOB)
	 */
	public void setCLOB(int arg0, CLOB arg1) throws SQLException {
		setParameter(arg0, "Oracle CLOB");
		try {
			os.setCLOB(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setCheckBindTypes(boolean)
	 */
	public void setCheckBindTypes(boolean arg0) {
		os.setCheckBindTypes(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setCursor(int,
	 *      java.sql.ResultSet)
	 */
	public void setCursor(int arg0, ResultSet arg1) throws SQLException {
		setParameter(arg0, "Oracle Cursor (ResultSet)");
		try {
			os.setCursor(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setCustomDatum(int,
	 *      oracle.sql.CustomDatum)
	 */
	public void setCustomDatum(int arg0, CustomDatum arg1) throws SQLException {
		setParameter(arg0, "Oracle CustomDatum");
		try {
			os.setCustomDatum(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setDATE(int, oracle.sql.DATE)
	 */
	public void setDATE(int arg0, DATE arg1) throws SQLException {
		setParameter(arg0, "Oracle DATE");
		try {
			os.setDATE(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setDisableStmtCaching(boolean)
	 */
	public void setDisableStmtCaching(boolean arg0) {
		os.setDisableStmtCaching(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setExecuteBatch(int)
	 */
	public void setExecuteBatch(int arg0) throws SQLException {
		try {
			os.setExecuteBatch(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setFixedCHAR(int,
	 *      java.lang.String)
	 */
	public void setFixedCHAR(int arg0, String arg1) throws SQLException {
		setParameter(arg0, "Oracle FixedCHAR");
		try {
			os.setFixedCHAR(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setFormOfUse(int, short)
	 */
	public void setFormOfUse(int arg0, short arg1) {
		os.setFormOfUse(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setNUMBER(int,
	 *      oracle.sql.NUMBER)
	 */
	public void setNUMBER(int arg0, NUMBER arg1) throws SQLException {
		setParameter(arg0, "Oracle NUMBER");
		try {
			os.setNUMBER(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setOPAQUE(int,
	 *      oracle.sql.OPAQUE)
	 */
	public void setOPAQUE(int arg0, OPAQUE arg1) throws SQLException {
		setParameter(arg0, "Oracle OPAQUE");
		try {
			os.setOPAQUE(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setORAData(int,
	 *      oracle.sql.ORAData)
	 */
	public void setORAData(int arg0, ORAData arg1) throws SQLException {
		setParameter(arg0, "Oracle ORAData");
		try {
			os.setORAData(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setOracleObject(int,
	 *      oracle.sql.Datum)
	 */
	public void setOracleObject(int arg0, Datum arg1) throws SQLException {
		setParameter(arg0, "Oracle Datum");
		try {
			os.setOracleObject(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setPlsqlIndexTable(int,
	 *      java.lang.Object, int, int, int, int)
	 */
	public void setPlsqlIndexTable(int arg0, Object arg1, int arg2, int arg3, int arg4, int arg5) throws SQLException {
		setParameter(arg0, "Oracle PL-SQL IndexTable");
		try {
			os.setPlsqlIndexTable(arg0, arg1, arg2, arg3, arg4, arg5);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setRAW(int, oracle.sql.RAW)
	 */
	public void setRAW(int arg0, RAW arg1) throws SQLException {
		setParameter(arg0, "Oracle RAW");
		try {
			os.setRAW(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setREF(int, oracle.sql.REF)
	 */
	public void setREF(int arg0, REF arg1) throws SQLException {
		setParameter(arg0, "Oracle REF");
		try {
			os.setREF(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setROWID(int, oracle.sql.ROWID)
	 */
	public void setROWID(int arg0, ROWID arg1) throws SQLException {
		setParameter(arg0, "Oracle ROWID");
		try {
			os.setROWID(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setRefType(int, oracle.sql.REF)
	 */
	public void setRefType(int arg0, REF arg1) throws SQLException {
		setParameter(arg0, "Oracle REF");
		try {
			os.setRefType(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setSTRUCT(int,
	 *      oracle.sql.STRUCT)
	 */
	public void setSTRUCT(int arg0, STRUCT arg1) throws SQLException {
		setParameter(arg0, "Oracle STRUCT");
		try {
			os.setSTRUCT(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setStructDescriptor(int,
	 *      oracle.sql.StructDescriptor)
	 */
	public void setStructDescriptor(int arg0, StructDescriptor arg1) throws SQLException {
		setParameter(arg0, "Oracle StructDescriptor");
		try {
			os.setStructDescriptor(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setTIMESTAMP(int,
	 *      oracle.sql.TIMESTAMP)
	 */
	public void setTIMESTAMP(int arg0, TIMESTAMP arg1) throws SQLException {
		setParameter(arg0, "Oracle TIMESTAMP");
		try {
			os.setTIMESTAMP(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setTIMESTAMPLTZ(int,
	 *      oracle.sql.TIMESTAMPLTZ)
	 */
	public void setTIMESTAMPLTZ(int arg0, TIMESTAMPLTZ arg1) throws SQLException {
		setParameter(arg0, "Oracle TIMESTAMPLTZ");
		try {
			os.setTIMESTAMPLTZ(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oracle.jdbc.OraclePreparedStatement#setTIMESTAMPTZ(int,
	 *      oracle.sql.TIMESTAMPTZ)
	 */
	public void setTIMESTAMPTZ(int arg0, TIMESTAMPTZ arg1) throws SQLException {
		setParameter(arg0, "Oracle TIMESTAMPTZ");
		try {
			os.setTIMESTAMPTZ(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	////////////////////////// ORACLE STATEMENT METHODS //////////////////////////
	
	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
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
	 * @see oracle.jdbc.OracleCallableStatement#getAnyDataEmbeddedObject(int)
	 */
	public Object getAnyDataEmbeddedObject(int arg0) throws SQLException {
		try {
			return os.getAnyDataEmbeddedObject(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int arg0) throws SQLException {
		try {
			return os.getCharacterStream(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#getINTERVALYM(int)
	 */
	public INTERVALYM getINTERVALYM(int arg0) throws SQLException {
		try {
			return os.getINTERVALYM(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#registerOutParameterBytes(int, int, int, int)
	 */
	public void registerOutParameterBytes(int arg0, int arg1, int arg2, int arg3) throws SQLException {
		try {
			os.registerOutParameterBytes(arg0, arg1, arg2, arg3);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleCallableStatement#registerOutParameterChars(int, int, int, int)
	 */
	public void registerOutParameterChars(int arg0, int arg1, int arg2, int arg3) throws SQLException {
		try {
			os.registerOutParameterChars(arg0, arg1, arg2, arg3);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OraclePreparedStatement#OracleGetParameterMetaData()
	 */
	public OracleParameterMetaData OracleGetParameterMetaData() throws SQLException {
		try {
			return os.OracleGetParameterMetaData();
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OraclePreparedStatement#defineParameterTypeBytes(int, int, int)
	 */
	public void defineParameterTypeBytes(int arg0, int arg1, int arg2) throws SQLException {
		try {
			os.defineParameterTypeBytes(arg0, arg1, arg2);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OraclePreparedStatement#defineParameterTypeChars(int, int, int)
	 */
	public void defineParameterTypeChars(int arg0, int arg1, int arg2) throws SQLException {
		try {
			os.defineParameterTypeChars(arg0, arg1, arg2);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OraclePreparedStatement#setINTERVALYM(int, oracle.sql.INTERVALYM)
	 */
	public void setINTERVALYM(int arg0, INTERVALYM arg1) throws SQLException {
		try {
			os.setINTERVALYM(arg0, arg1);
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
	public ResultSet getReturnResultSet() throws SQLException {
		return os.getReturnResultSet();
	}

	@Override
	public void registerReturnParameter(int arg0, int arg1) throws SQLException {
		os.registerReturnParameter(arg0, arg1);
	}

	@Override
	public void registerReturnParameter(int arg0, int arg1, int arg2) throws SQLException {
		os.registerReturnParameter(arg0, arg1, arg2);
	}

	@Override
	public void registerReturnParameter(int arg0, int arg1, String arg2) throws SQLException {
		os.registerReturnParameter(arg0, arg1, arg2);
	}

	@Override
	public void setARRAYAtName(String arg0, ARRAY arg1) throws SQLException {
		os.setARRAYAtName(arg0, arg1);
	}

	@Override
	public void setArrayAtName(String arg0, Array arg1) throws SQLException {
		os.setArrayAtName(arg0, arg1);
	}

	@Override
	public void setAsciiStreamAtName(String arg0, InputStream arg1) throws SQLException {
		os.setAsciiStreamAtName(arg0, arg1);
	}

	@Override
	public void setAsciiStreamAtName(String arg0, InputStream arg1, int arg2) throws SQLException {
		os.setAsciiStreamAtName(arg0, arg1, arg2);
	}

	@Override
	public void setAsciiStreamAtName(String arg0, InputStream arg1, long arg2) throws SQLException {
		os.setAsciiStreamAtName(arg0, arg1, arg2);
	}

	@Override
	public void setBFILEAtName(String arg0, BFILE arg1) throws SQLException {
		os.setBFILEAtName(arg0, arg1);
	}

	@Override
	public void setBfileAtName(String arg0, BFILE arg1) throws SQLException {
		os.setBFILEAtName(arg0, arg1);
	}
	
	@Override
	public void setBLOBAtName(String arg0, BLOB arg1) throws SQLException {
		os.setBLOBAtName(arg0, arg1);
	}

	@Override
	public void setBigDecimalAtName(String arg0, BigDecimal arg1) throws SQLException {
		os.setBigDecimalAtName(arg0, arg1);
	}

	@Override
	public void setBinaryDouble(int arg0, double arg1) throws SQLException {
		os.setBinaryDouble(arg0, arg1);
	}

	@Override
	public void setBinaryDouble(int arg0, BINARY_DOUBLE arg1) throws SQLException {
		os.setBinaryDouble(arg0, arg1);
	}

	@Override
	public void setBinaryDoubleAtName(String arg0, double arg1) throws SQLException {
		os.setBinaryDoubleAtName(arg0, arg1);
	}

	@Override
	public void setBinaryDoubleAtName(String arg0, BINARY_DOUBLE arg1) throws SQLException {
		os.setBinaryDoubleAtName(arg0, arg1);
	}

	@Override
	public void setBinaryFloat(int arg0, float arg1) throws SQLException {
		os.setBinaryFloat(arg0, arg1);
	}		

	@Override
	public void setBinaryFloat(int arg0, BINARY_FLOAT arg1) throws SQLException {
		os.setBinaryFloat(arg0, arg1);
	}

	@Override
	public void setBinaryFloatAtName(String arg0, float arg1) throws SQLException {
		os.setBinaryFloatAtName(arg0, arg1);
	}

	@Override
	public void setBinaryFloatAtName(String arg0, BINARY_FLOAT arg1) throws SQLException {
		os.setBinaryFloatAtName(arg0, arg1);
	}

	@Override
	public void setBinaryStreamAtName(String arg0, InputStream arg1) throws SQLException {
		os.setBinaryStreamAtName(arg0, arg1);
	}

	@Override
	public void setBinaryStreamAtName(String arg0, InputStream arg1, int arg2) throws SQLException {
		os.setBinaryStreamAtName(arg0, arg1);
	}

	@Override
	public void setBinaryStreamAtName(String arg0, InputStream arg1, long arg2) throws SQLException {
		os.setBinaryStreamAtName(arg0, arg1, arg2);
	}

	@Override
	public void setBlobAtName(String arg0, Blob arg1) throws SQLException {
		os.setBlobAtName(arg0, arg1);
	}

	@Override
	public void setBlobAtName(String arg0, InputStream arg1) throws SQLException {
		os.setBlobAtName(arg0, arg1);
	}

	@Override
	public void setBlobAtName(String arg0, InputStream arg1, long arg2) throws SQLException {
		os.setBlobAtName(arg0, arg1, arg2);
	}

	@Override
	public void setBooleanAtName(String arg0, boolean arg1) throws SQLException {
		os.setBooleanAtName(arg0, arg1);
	}

	@Override
	public void setByteAtName(String arg0, byte arg1) throws SQLException {
		os.setByteAtName(arg0, arg1);
	}

	@Override
	public void setBytesAtName(String arg0, byte[] arg1) throws SQLException {
		os.setBytesAtName(arg0, arg1);
	}

	@Override
	public void setBytesForBlob(int arg0, byte[] arg1) throws SQLException {
		os.setBytesForBlob(arg0, arg1);
	}

	@Override
	public void setBytesForBlobAtName(String arg0, byte[] arg1) throws SQLException {
		os.setBytesForBlobAtName(arg0, arg1);
	}

	@Override
	public void setCHARAtName(String arg0, CHAR arg1) throws SQLException {
		os.setCHARAtName(arg0, arg1);
	}

	@Override
	public void setCLOBAtName(String arg0, CLOB arg1) throws SQLException {
		os.setCLOBAtName(arg0, arg1);
	}

	@Override
	public void setCharacterStreamAtName(String arg0, Reader arg1) throws SQLException {
		os.setCharacterStreamAtName(arg0, arg1);
	}

	@Override
	public void setCharacterStreamAtName(String arg0, Reader arg1, long arg2) throws SQLException {
		os.setCharacterStreamAtName(arg0, arg1);
	}

	@Override
	public void setClobAtName(String arg0, Clob arg1) throws SQLException {
		os.setClobAtName(arg0, arg1);
	}

	@Override
	public void setClobAtName(String arg0, Reader arg1) throws SQLException {
		os.setClobAtName(arg0, arg1);
	}

	@Override
	public void setClobAtName(String arg0, Reader arg1, long arg2) throws SQLException {
		os.setClobAtName(arg0, arg1, arg2);		
	}

	@Override
	public void setCursorAtName(String arg0, ResultSet arg1) throws SQLException {
		os.setCursorAtName(arg0, arg1);
	}

	@Override
	public void setCustomDatumAtName(String arg0, CustomDatum arg1) throws SQLException {
		os.setCustomDatumAtName(arg0, arg1);
	}

	@Override
	public void setDATEAtName(String arg0, DATE arg1) throws SQLException {
		os.setDATEAtName(arg0, arg1);
	}

	@Override
	public void setDateAtName(String arg0, Date arg1) throws SQLException {
		os.setDateAtName(arg0, arg1);
	}

	@Override
	public void setDateAtName(String arg0, Date arg1, Calendar arg2) throws SQLException {
		os.setDateAtName(arg0, arg1, arg2);
	}

	@Override
	public void setDoubleAtName(String arg0, double arg1) throws SQLException {
		os.setDoubleAtName(arg0, arg1);
	}

	@Override
	public void setFixedCHARAtName(String arg0, String arg1) throws SQLException {
		os.setFixedCHARAtName(arg0, arg1);
	}

	@Override
	public void setFloatAtName(String arg0, float arg1) throws SQLException {
		os.setFloatAtName(arg0, arg1);
	}

	@Override
	public void setINTERVALDS(int arg0, INTERVALDS arg1) throws SQLException {
		os.setINTERVALDS(arg0, arg1);
	}

	@Override
	public void setINTERVALDSAtName(String arg0, INTERVALDS arg1) throws SQLException {
		os.setINTERVALDSAtName(arg0, arg1);
	}

	@Override
	public void setINTERVALYMAtName(String arg0, INTERVALYM arg1) throws SQLException {
		os.setINTERVALYMAtName(arg0, arg1);
	}

	@Override
	public void setIntAtName(String arg0, int arg1) throws SQLException {
		os.setIntAtName(arg0, arg1);
	}

	@Override
	public void setLongAtName(String arg0, long arg1) throws SQLException {
		os.setLongAtName(arg0, arg1);
	}

	@Override
	public void setNCharacterStreamAtName(String arg0, Reader arg1) throws SQLException {
		os.setNCharacterStreamAtName(arg0, arg1);
	}

	@Override
	public void setNCharacterStreamAtName(String arg0, Reader arg1, long arg2) throws SQLException {
		os.setNCharacterStreamAtName(arg0, arg1, arg2);
	}

	@Override
	public void setNClobAtName(String arg0, NClob arg1) throws SQLException {
		os.setNClobAtName(arg0, arg1);
	}

	@Override
	public void setNClobAtName(String arg0, Reader arg1) throws SQLException {
		os.setNClobAtName(arg0, arg1);
	}

	@Override
	public void setNClobAtName(String arg0, Reader arg1, long arg2) throws SQLException {
		os.setNClobAtName(arg0, arg1, arg2);
	}

	@Override
	public void setNStringAtName(String arg0, String arg1) throws SQLException {
		os.setNStringAtName(arg0, arg1);
	}

	@Override
	public void setNUMBERAtName(String arg0, NUMBER arg1) throws SQLException {
		os.setNUMBERAtName(arg0, arg1);
	}

	@Override
	public void setNullAtName(String arg0, int arg1) throws SQLException {
		os.setNullAtName(arg0, arg1);
	}

	@Override
	public void setNullAtName(String arg0, int arg1, String arg2) throws SQLException {
		os.setNullAtName(arg0, arg1, arg2);
	}

	@Override
	public void setOPAQUEAtName(String arg0, OPAQUE arg1) throws SQLException {
		os.setOPAQUEAtName(arg0, arg1);
	}

	@Override
	public void setORADataAtName(String arg0, ORAData arg1) throws SQLException {
		os.setORADataAtName(arg0, arg1);		
	}

	@Override
	public void setObjectAtName(String arg0, Object arg1) throws SQLException {
		os.setObjectAtName(arg0, arg1);
	}

	@Override
	public void setObjectAtName(String arg0, Object arg1, int arg2) throws SQLException {
		os.setObjectAtName(arg0, arg1, arg2);
	}

	@Override
	public void setObjectAtName(String arg0, Object arg1, int arg2, int arg3) throws SQLException {
		os.setObjectAtName(arg0, arg1, arg2, arg3);
	}

	@Override
	public void setOracleObjectAtName(String arg0, Datum arg1) throws SQLException {
		os.setOracleObjectAtName(arg0, arg1);
	}

	@Override
	public void setRAWAtName(String arg0, RAW arg1) throws SQLException {
		os.setRAWAtName(arg0, arg1);
	}

	@Override
	public void setREFAtName(String arg0, REF arg1) throws SQLException {
		os.setREFAtName(arg0, arg1);
	}

	@Override
	public void setROWIDAtName(String arg0, ROWID arg1) throws SQLException {
		os.setROWIDAtName(arg0, arg1);
	}

	@Override
	public void setRefAtName(String arg0, Ref arg1) throws SQLException {
		os.setRefAtName(arg0, arg1);
	}

	@Override
	public void setRefTypeAtName(String arg0, REF arg1) throws SQLException {
		os.setRefTypeAtName(arg0, arg1);
	}

	@Override
	public void setRowIdAtName(String arg0, RowId arg1) throws SQLException {
		os.setRowIdAtName(arg0, arg1);
	}

	@Override
	public void setSQLXMLAtName(String arg0, SQLXML arg1) throws SQLException {
		os.setSQLXMLAtName(arg0, arg1);
	}

	@Override
	public void setSTRUCTAtName(String arg0, STRUCT arg1) throws SQLException {
		os.setSTRUCTAtName(arg0, arg1);
	}

	@Override
	public void setShortAtName(String arg0, short arg1) throws SQLException {
		os.setShortAtName(arg0, arg1);
	}

	@Override
	public void setStringAtName(String arg0, String arg1) throws SQLException {
		os.setStringAtName(arg0, arg1);
	}

	@Override
	public void setStringForClob(int arg0, String arg1) throws SQLException {
		os.setStringForClob(arg0, arg1);
	}

	@Override
	public void setStringForClobAtName(String arg0, String arg1) throws SQLException {
		os.setStringForClobAtName(arg0, arg1);
	}

	@Override
	public void setStructDescriptorAtName(String arg0, StructDescriptor arg1) throws SQLException {
		os.setStructDescriptorAtName(arg0, arg1);
	}

	@Override
	public void setTIMESTAMPAtName(String arg0, TIMESTAMP arg1) throws SQLException {
		os.setTIMESTAMPAtName(arg0, arg1);
	}

	@Override
	public void setTIMESTAMPLTZAtName(String arg0, TIMESTAMPLTZ arg1) throws SQLException {
		os.setTIMESTAMPLTZAtName(arg0, arg1);
	}

	@Override
	public void setTIMESTAMPTZAtName(String arg0, TIMESTAMPTZ arg1) throws SQLException {
		os.setTIMESTAMPTZAtName(arg0, arg1);
	}

	@Override
	public void setTimeAtName(String arg0, Time arg1) throws SQLException {
		os.setTimeAtName(arg0, arg1);
	}

	@Override
	public void setTimeAtName(String arg0, Time arg1, Calendar arg2) throws SQLException {
		os.setTimeAtName(arg0, arg1, arg2);
	}

	@Override
	public void setTimestampAtName(String arg0, Timestamp arg1) throws SQLException {
		os.setTimestampAtName(arg0, arg1);
	}

	@Override
	public void setTimestampAtName(String arg0, Timestamp arg1, Calendar arg2) throws SQLException {
		os.setTimestampAtName(arg0, arg1, arg2);
	}

	@Override
	public void setURLAtName(String arg0, URL arg1) throws SQLException {
		os.setURLAtName(arg0, arg1);
	}

	@Override
	public void setUnicodeStreamAtName(String arg0, InputStream arg1, int arg2) throws SQLException {
		os.setUnicodeStreamAtName(arg0, arg1, arg2);
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
	public BFILE getBfile(int arg0) throws SQLException {
		return os.getBfile(arg0);
	}

	@Override
	public InputStream getBinaryStream(String arg0) throws SQLException {
		return os.getBinaryStream(arg0);
	}

	@Override
	public INTERVALDS getINTERVALDS(int arg0) throws SQLException {
		return os.getINTERVALDS(arg0);
	}

	@Override
	public Object getObject(int arg0, OracleDataFactory arg1) throws SQLException {
		return os.getObject(arg0, arg1);
	}

	@Override
	public InputStream getUnicodeStream(String arg0) throws SQLException {
		return os.getUnicodeStream(arg0);
	}

	@Override
	public void registerOutParameter(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		os.registerOutParameter(arg0, arg1, arg2, arg3);
	}

	@Override
	public void setARRAY(String arg0, ARRAY arg1) throws SQLException {
		os.setARRAY(arg0, arg1);
	}

	@Override
	public void setArray(String arg0, Array arg1) throws SQLException {
		os.setArray(arg0, arg1);
	}

	@Override
	public void setBFILE(String arg0, BFILE arg1) throws SQLException {
		os.setBFILE(arg0, arg1);
	}

	@Override
	public void setBLOB(String arg0, BLOB arg1) throws SQLException {
		os.setBLOB(arg0, arg1);
	}

	@Override
	public void setBfile(String arg0, BFILE arg1) throws SQLException {
		os.setBFILE(arg0, arg1);
	}

	@Override
	public void setBinaryDouble(String arg0, BINARY_DOUBLE arg1) throws SQLException {
		os.setBinaryDouble(arg0, arg1);
	}

	@Override
	public void setBinaryDouble(String arg0, double arg1) throws SQLException {
		os.setBinaryDouble(arg0, arg1);
	}

	@Override
	public void setBinaryFloat(String arg0, BINARY_FLOAT arg1) throws SQLException {
		os.setBinaryFloat(arg0, arg1);
	}

	@Override
	public void setBinaryFloat(String arg0, float arg1) throws SQLException {
		os.setBinaryFloat(arg0, arg1);
	}

	@Override
	public void setBytesForBlob(String arg0, byte[] arg1) throws SQLException {
		os.setBytesForBlob(arg0, arg1);
	}

	@Override
	public void setCHAR(String arg0, CHAR arg1) throws SQLException {
		os.setCHAR(arg0, arg1);
	}

	@Override
	public void setCLOB(String arg0, CLOB arg1) throws SQLException {
		os.setCLOB(arg0, arg1);
	}

	@Override
	public void setCursor(String arg0, ResultSet arg1) throws SQLException {
		os.setCursor(arg0, arg1);
	}

	@Override
	public void setCustomDatum(String arg0, CustomDatum arg1) throws SQLException {
		os.setCustomDatum(arg0, arg1);
	}

	@Override
	public void setDATE(String arg0, DATE arg1) throws SQLException {
		os.setDATE(arg0, arg1);
	}

	@Override
	public void setFixedCHAR(String arg0, String arg1) throws SQLException {
		os.setFixedCHAR(arg0, arg1);
	}

	@Override
	public void setINTERVALDS(String arg0, INTERVALDS arg1) throws SQLException {
		os.setINTERVALDS(arg0, arg1);
	}

	@Override
	public void setINTERVALYM(String arg0, INTERVALYM arg1) throws SQLException {
		os.setINTERVALYM(arg0, arg1);
	}

	@Override
	public void setNUMBER(String arg0, NUMBER arg1) throws SQLException {
		os.setNUMBER(arg0, arg1);
	}

	@Override
	public void setOPAQUE(String arg0, OPAQUE arg1) throws SQLException {
		os.setOPAQUE(arg0, arg1);
	}

	@Override
	public void setORAData(String arg0, ORAData arg1) throws SQLException {
		os.setORAData(arg0, arg1);
	}

	@Override
	public void setOracleObject(String arg0, Datum arg1) throws SQLException {
		os.setOracleObject(arg0, arg1);
	}

	@Override
	public void setRAW(String arg0, RAW arg1) throws SQLException {
		os.setRAW(arg0, arg1);
	}

	@Override
	public void setREF(String arg0, REF arg1) throws SQLException {
		os.setREF(arg0, arg1);
	}

	@Override
	public void setROWID(String arg0, ROWID arg1) throws SQLException {
		os.setROWID(arg0, arg1);
	}

	@Override
	public void setRef(String arg0, Ref arg1) throws SQLException {
		os.setRef(arg0, arg1);
	}

	@Override
	public void setRefType(String arg0, REF arg1) throws SQLException {
		os.setRefType(arg0, arg1);
	}

	@Override
	public void setSTRUCT(String arg0, STRUCT arg1) throws SQLException {
		os.setSTRUCT(arg0, arg1);
	}

	@Override
	public void setStringForClob(String arg0, String arg1) throws SQLException {
		os.setStringForClob(arg0, arg1);
	}

	@Override
	public void setStructDescriptor(String arg0, StructDescriptor arg1) throws SQLException {
		os.setStructDescriptor(arg0, arg1);
	}

	@Override
	public void setTIMESTAMP(String arg0, TIMESTAMP arg1) throws SQLException {
		os.setTIMESTAMP(arg0, arg1);
	}

	@Override
	public void setTIMESTAMPLTZ(String arg0, TIMESTAMPLTZ arg1) throws SQLException {
		os.setTIMESTAMPLTZ(arg0, arg1);
	}

	@Override
	public void setTIMESTAMPTZ(String arg0, TIMESTAMPTZ arg1) throws SQLException {
		os.setTIMESTAMPTZ(arg0, arg1);
	}

	@Override
	public void setUnicodeStream(String arg0, InputStream arg1, int arg2) throws SQLException {
		os.setUnicodeStream(arg0, arg1, arg2);
	}

	@Override
	public void registerOutParameterAtName(String arg0, int arg1) throws SQLException {
		os.registerOutParameterAtName(arg0, arg1);
	}

	@Override
	public void registerOutParameterAtName(String arg0, int arg1, int arg2) throws SQLException {
		os.registerOutParameterAtName(arg0, arg1, arg2);
	}

	@Override
	public void registerOutParameterAtName(String arg0, int arg1, String arg2) throws SQLException {
		os.registerOutParameterAtName(arg0, arg1, arg2);
	}


}
