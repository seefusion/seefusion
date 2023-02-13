/**
 * 
 */
package com.seefusion;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleDataFactory;
import oracle.jdbc.OracleResultSet;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
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
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

/**
 * @author Daryl
 *
 */
@SuppressWarnings("deprecation")
class OracleResultSetImpl extends ResultSetImpl implements OracleResultSet {

	OracleResultSet ors;
	
	public OracleResultSetImpl(StatementImpl s, ResultSet oracleResultSet, QueryInfo qi, JdbcConfig jdbcConfig, boolean isTracingAPI) {
		super(s, oracleResultSet, qi, jdbcConfig, isTracingAPI);
		if (oracleResultSet instanceof OracleResultSet) {
			ors = (OracleResultSet) oracleResultSet;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getARRAY(int)
	 */
	@Override
	public ARRAY getARRAY(int arg0) throws SQLException {
		try {
			return ors.getARRAY(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getARRAY(java.lang.String)
	 */
	@Override
	public ARRAY getARRAY(String arg0) throws SQLException {
		try {
			return ors.getARRAY(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getBFILE(int)
	 */
	@Override
	public BFILE getBFILE(int arg0) throws SQLException {
		try {
			return ors.getBFILE(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getBFILE(java.lang.String)
	 */
	@Override
	public BFILE getBFILE(String arg0) throws SQLException {
		try {
			return ors.getBFILE(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getBLOB(int)
	 */
	@Override
	public BLOB getBLOB(int arg0) throws SQLException {
		try {
			return ors.getBLOB(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getBLOB(java.lang.String)
	 */
	@Override
	public BLOB getBLOB(String arg0) throws SQLException {
		try {
			return ors.getBLOB(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getBfile(int)
	 */
	@Override
	public BFILE getBfile(int arg0) throws SQLException {
		try {
			return ors.getBfile(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getBfile(java.lang.String)
	 */
	@Override
	public BFILE getBfile(String arg0) throws SQLException {
		try {
			return ors.getBfile(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getCHAR(int)
	 */
	@Override
	public CHAR getCHAR(int arg0) throws SQLException {
		try {
			return ors.getCHAR(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getCHAR(java.lang.String)
	 */
	@Override
	public CHAR getCHAR(String arg0) throws SQLException {
		try {
			return ors.getCHAR(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getCLOB(int)
	 */
	@Override
	public CLOB getCLOB(int arg0) throws SQLException {
		try {
			return ors.getCLOB(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getCLOB(java.lang.String)
	 */
	@Override
	public CLOB getCLOB(String arg0) throws SQLException {
		try {
			return ors.getCLOB(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getCursor(int)
	 */
	@Override
	public ResultSet getCursor(int arg0) throws SQLException {
		try {
			return new OracleResultSetImpl(s, ors.getCursor(arg0), qi, jdbcConfig, isTracingAPI);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getCursor(java.lang.String)
	 */
	@Override
	public ResultSet getCursor(String arg0) throws SQLException {
		try {
			return new OracleResultSetImpl(s, ors.getCursor(arg0), qi, jdbcConfig, isTracingAPI);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getCustomDatum(int, oracle.sql.CustomDatumFactory)
	 */
	@Override
	public CustomDatum getCustomDatum(int arg0, CustomDatumFactory arg1) throws SQLException {
		try {
			return ors.getCustomDatum(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getCustomDatum(java.lang.String, oracle.sql.CustomDatumFactory)
	 */
	@Override
	public CustomDatum getCustomDatum(String arg0, CustomDatumFactory arg1) throws SQLException {
		try {
			return ors.getCustomDatum(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getDATE(int)
	 */
	@Override
	public DATE getDATE(int arg0) throws SQLException {
		try {
			return ors.getDATE(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getDATE(java.lang.String)
	 */
	@Override
	public DATE getDATE(String arg0) throws SQLException {
		try {
			return ors.getDATE(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getNUMBER(int)
	 */
	@Override
	public NUMBER getNUMBER(int arg0) throws SQLException {
		try {
			return ors.getNUMBER(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getNUMBER(java.lang.String)
	 */
	@Override
	public NUMBER getNUMBER(String arg0) throws SQLException {
		try {
			return ors.getNUMBER(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getOPAQUE(int)
	 */
	@Override
	public OPAQUE getOPAQUE(int arg0) throws SQLException {
		try {
			return ors.getOPAQUE(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getOPAQUE(java.lang.String)
	 */
	@Override
	public OPAQUE getOPAQUE(String arg0) throws SQLException {
		try {
			return ors.getOPAQUE(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getORAData(int, oracle.sql.ORADataFactory)
	 */
	@Override
	public ORAData getORAData(int arg0, ORADataFactory arg1) throws SQLException {
		try {
			return ors.getORAData(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getORAData(java.lang.String, oracle.sql.ORADataFactory)
	 */
	@Override
	public ORAData getORAData(String arg0, ORADataFactory arg1) throws SQLException {
		try {
			return ors.getORAData(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getOracleObject(int)
	 */
	@Override
	public Datum getOracleObject(int arg0) throws SQLException {
		try {
			return ors.getOracleObject(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getOracleObject(java.lang.String)
	 */
	@Override
	public Datum getOracleObject(String arg0) throws SQLException {
		try {
			return ors.getOracleObject(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getRAW(int)
	 */
	@Override
	public RAW getRAW(int arg0) throws SQLException {
		try {
			return ors.getRAW(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getRAW(java.lang.String)
	 */
	@Override
	public RAW getRAW(String arg0) throws SQLException {
		try {
			return ors.getRAW(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getREF(int)
	 */
	@Override
	public REF getREF(int arg0) throws SQLException {
		try {
			return ors.getREF(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getREF(java.lang.String)
	 */
	@Override
	public REF getREF(String arg0) throws SQLException {
		try {
			return ors.getREF(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getROWID(int)
	 */
	@Override
	public ROWID getROWID(int arg0) throws SQLException {
		try {
			return ors.getROWID(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getROWID(java.lang.String)
	 */
	@Override
	public ROWID getROWID(String arg0) throws SQLException {
		try {
			return ors.getROWID(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getSTRUCT(int)
	 */
	@Override
	public STRUCT getSTRUCT(int arg0) throws SQLException {
		try {
			return ors.getSTRUCT(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getSTRUCT(java.lang.String)
	 */
	@Override
	public STRUCT getSTRUCT(String arg0) throws SQLException {
		try {
			return ors.getSTRUCT(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getTIMESTAMP(int)
	 */
	@Override
	public TIMESTAMP getTIMESTAMP(int arg0) throws SQLException {
		try {
			return ors.getTIMESTAMP(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getTIMESTAMP(java.lang.String)
	 */
	@Override
	public TIMESTAMP getTIMESTAMP(String arg0) throws SQLException {
		try {
			return ors.getTIMESTAMP(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getTIMESTAMPLTZ(int)
	 */
	@Override
	public TIMESTAMPLTZ getTIMESTAMPLTZ(int arg0) throws SQLException {
		try {
			return ors.getTIMESTAMPLTZ(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getTIMESTAMPLTZ(java.lang.String)
	 */
	@Override
	public TIMESTAMPLTZ getTIMESTAMPLTZ(String arg0) throws SQLException {
		try {
			return ors.getTIMESTAMPLTZ(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getTIMESTAMPTZ(int)
	 */
	@Override
	public TIMESTAMPTZ getTIMESTAMPTZ(int arg0) throws SQLException {
		try {
			return ors.getTIMESTAMPTZ(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getTIMESTAMPTZ(java.lang.String)
	 */
	@Override
	public TIMESTAMPTZ getTIMESTAMPTZ(String arg0) throws SQLException {
		try {
			return ors.getTIMESTAMPTZ(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateARRAY(int, oracle.sql.ARRAY)
	 */
	@Override
	public void updateARRAY(int arg0, ARRAY arg1) throws SQLException {
		try {
			ors.updateARRAY(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateARRAY(java.lang.String, oracle.sql.ARRAY)
	 */
	@Override
	public void updateARRAY(String arg0, ARRAY arg1) throws SQLException {
		try {
			ors.updateARRAY(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateBFILE(int, oracle.sql.BFILE)
	 */
	@Override
	public void updateBFILE(int arg0, BFILE arg1) throws SQLException {
		try {
			ors.updateBFILE(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateBFILE(java.lang.String, oracle.sql.BFILE)
	 */
	@Override
	public void updateBFILE(String arg0, BFILE arg1) throws SQLException {
		try {
			ors.updateBFILE(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateBLOB(int, oracle.sql.BLOB)
	 */
	@Override
	public void updateBLOB(int arg0, BLOB arg1) throws SQLException {
		try {
			ors.updateBLOB(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateBLOB(java.lang.String, oracle.sql.BLOB)
	 */
	@Override
	public void updateBLOB(String arg0, BLOB arg1) throws SQLException {
		try {
			ors.updateBLOB(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateBfile(int, oracle.sql.BFILE)
	 */
	@Override
	public void updateBfile(int arg0, BFILE arg1) throws SQLException {
		try {
			ors.updateBfile(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateBfile(java.lang.String, oracle.sql.BFILE)
	 */
	@Override
	public void updateBfile(String arg0, BFILE arg1) throws SQLException {
		try {
			ors.updateBfile(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateCHAR(int, oracle.sql.CHAR)
	 */
	@Override
	public void updateCHAR(int arg0, CHAR arg1) throws SQLException {
		try {
			ors.updateCHAR(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateCHAR(java.lang.String, oracle.sql.CHAR)
	 */
	@Override
	public void updateCHAR(String arg0, CHAR arg1) throws SQLException {
		try {
			ors.updateCHAR(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateCLOB(int, oracle.sql.CLOB)
	 */
	@Override
	public void updateCLOB(int arg0, CLOB arg1) throws SQLException {
		try {
			ors.updateCLOB(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateCLOB(java.lang.String, oracle.sql.CLOB)
	 */
	@Override
	public void updateCLOB(String arg0, CLOB arg1) throws SQLException {
		try {
			ors.updateCLOB(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateCustomDatum(int, oracle.sql.CustomDatum)
	 */
	@Override
	public void updateCustomDatum(int arg0, CustomDatum arg1) throws SQLException {
		try {
			ors.updateCustomDatum(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateCustomDatum(java.lang.String, oracle.sql.CustomDatum)
	 */
	@Override
	public void updateCustomDatum(String arg0, CustomDatum arg1) throws SQLException {
		try {
			ors.updateCustomDatum(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateDATE(int, oracle.sql.DATE)
	 */
	@Override
	public void updateDATE(int arg0, DATE arg1) throws SQLException {
		try {
			ors.updateDATE(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateDATE(java.lang.String, oracle.sql.DATE)
	 */
	@Override
	public void updateDATE(String arg0, DATE arg1) throws SQLException {
		try {
			ors.updateDATE(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateNUMBER(int, oracle.sql.NUMBER)
	 */
	@Override
	public void updateNUMBER(int arg0, NUMBER arg1) throws SQLException {
		try {
			ors.updateNUMBER(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateNUMBER(java.lang.String, oracle.sql.NUMBER)
	 */
	@Override
	public void updateNUMBER(String arg0, NUMBER arg1) throws SQLException {
		try {
			ors.updateNUMBER(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateORAData(int, oracle.sql.ORAData)
	 */
	@Override
	public void updateORAData(int arg0, ORAData arg1) throws SQLException {
		try {
			ors.updateORAData(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateORAData(java.lang.String, oracle.sql.ORAData)
	 */
	@Override
	public void updateORAData(String arg0, ORAData arg1) throws SQLException {
		try {
			ors.updateORAData(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateOracleObject(int, oracle.sql.Datum)
	 */
	@Override
	public void updateOracleObject(int arg0, Datum arg1) throws SQLException {
		try {
			ors.updateOracleObject(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateOracleObject(java.lang.String, oracle.sql.Datum)
	 */
	@Override
	public void updateOracleObject(String arg0, Datum arg1) throws SQLException {
		try {
			ors.updateOracleObject(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateRAW(int, oracle.sql.RAW)
	 */
	@Override
	public void updateRAW(int arg0, RAW arg1) throws SQLException {
		try {
			ors.updateRAW(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateRAW(java.lang.String, oracle.sql.RAW)
	 */
	@Override
	public void updateRAW(String arg0, RAW arg1) throws SQLException {
		try {
			ors.updateRAW(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateREF(int, oracle.sql.REF)
	 */
	@Override
	public void updateREF(int arg0, REF arg1) throws SQLException {
		try {
			ors.updateREF(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateREF(java.lang.String, oracle.sql.REF)
	 */
	@Override
	public void updateREF(String arg0, REF arg1) throws SQLException {
		try {
			ors.updateREF(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateROWID(int, oracle.sql.ROWID)
	 */
	@Override
	public void updateROWID(int arg0, ROWID arg1) throws SQLException {
		try {
			ors.updateROWID(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateROWID(java.lang.String, oracle.sql.ROWID)
	 */
	@Override
	public void updateROWID(String arg0, ROWID arg1) throws SQLException {
		try {
			ors.updateROWID(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateSTRUCT(int, oracle.sql.STRUCT)
	 */
	@Override
	public void updateSTRUCT(int arg0, STRUCT arg1) throws SQLException {
		try {
			ors.updateSTRUCT(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateSTRUCT(java.lang.String, oracle.sql.STRUCT)
	 */
	@Override
	public void updateSTRUCT(String arg0, STRUCT arg1) throws SQLException {
		try {
			ors.updateSTRUCT(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getINTERVALYM(int)
	 */
	@Override
	public INTERVALYM getINTERVALYM(int arg0) throws SQLException {
		try {
			return ors.getINTERVALYM(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#getINTERVALYM(java.lang.String)
	 */
	@Override
	public INTERVALYM getINTERVALYM(String arg0) throws SQLException {
		try {
			return ors.getINTERVALYM(arg0);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateINTERVALYM(int, oracle.sql.INTERVALYM)
	 */
	@Override
	public void updateINTERVALYM(int arg0, INTERVALYM arg1) throws SQLException {
		try {
			ors.updateINTERVALYM(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateTIMESTAMP(int, oracle.sql.TIMESTAMP)
	 */
	@Override
	public void updateTIMESTAMP(int arg0, TIMESTAMP arg1) throws SQLException {
		try {
			ors.updateTIMESTAMP(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateTIMESTAMPLTZ(int, oracle.sql.TIMESTAMPLTZ)
	 */
	@Override
	public void updateTIMESTAMPLTZ(int arg0, TIMESTAMPLTZ arg1) throws SQLException {
		try {
			ors.updateTIMESTAMPLTZ(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see oracle.jdbc.OracleResultSet#updateTIMESTAMPTZ(int, oracle.sql.TIMESTAMPTZ)
	 */
	@Override
	public void updateTIMESTAMPTZ(int arg0, TIMESTAMPTZ arg1) throws SQLException {
		try {
			ors.updateTIMESTAMPTZ(arg0, arg1);
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public AuthorizationIndicator getAuthorizationIndicator(int arg0) throws SQLException {
		return ors.getAuthorizationIndicator(arg0);
	}

	@Override
	public AuthorizationIndicator getAuthorizationIndicator(String arg0) throws SQLException {
		return ors.getAuthorizationIndicator(arg0);
	}

	@Override
	public INTERVALDS getINTERVALDS(int arg0) throws SQLException {
		return ors.getINTERVALDS(arg0);
	}

	@Override
	public INTERVALDS getINTERVALDS(String arg0) throws SQLException {
		return ors.getINTERVALDS(arg0);
	}

	@Override
	public Object getObject(int arg0, OracleDataFactory arg1) throws SQLException {
		return ors.getObject(arg0, arg1);
	}

	@Override
	public Object getObject(String arg0, OracleDataFactory arg1) throws SQLException {
		return ors.getObject(arg0, arg1);
	}

	@Override
	public void updateINTERVALDS(int arg0, INTERVALDS arg1) throws SQLException {
		ors.updateINTERVALDS(arg0, arg1);
	}

	@Override
	public void updateINTERVALDS(String arg0, INTERVALDS arg1) throws SQLException {
		ors.updateINTERVALDS(arg0, arg1);
	}

	@Override
	public void updateINTERVALYM(String arg0, INTERVALYM arg1) throws SQLException {
		ors.updateINTERVALYM(arg0, arg1);
	}

	@Override
	public void updateTIMESTAMP(String arg0, TIMESTAMP arg1) throws SQLException {
		ors.updateTIMESTAMP(arg0, arg1);
		
	}

	@Override
	public void updateTIMESTAMPLTZ(String arg0, TIMESTAMPLTZ arg1) throws SQLException {
		ors.updateTIMESTAMPLTZ(arg0, arg1);
		
	}

	@Override
	public void updateTIMESTAMPTZ(String arg0, TIMESTAMPTZ arg1) throws SQLException {
		ors.updateTIMESTAMPTZ(arg0, arg1);
		
	}

	@Override
	public byte[] getCompileKey() throws SQLException {
		return ors.getCompileKey();
	}

	@Override
	public byte[] getRuntimeKey() throws SQLException {
		return ors.getRuntimeKey();
	}

	@Override
	public boolean isFromResultSetCache() throws SQLException {
		return ors.isFromResultSetCache();
	}

}
