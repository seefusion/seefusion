/*
 * PreparedStatementImpl.java
 *
 */

package com.seefusion;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Types;

class PreparedStatementImpl extends StatementImpl implements java.sql.PreparedStatement {

	java.sql.PreparedStatement ps;

	boolean firstExecute = true;

	static String CLASSNAME = "PreparedStatement";

	/**
	 * Creates a new instance of PreparedStatementImpl
	 */
	PreparedStatementImpl(ConnectionImpl c, java.sql.PreparedStatement ps, String sql, JdbcConfig jdbcConfig, boolean isTracingAPI)
			throws SQLException {
		super(c, ps, jdbcConfig, isTracingAPI);
		this.ps = ps;
		setSql(sql);
	}

	@Override
	void setSql(String sql) {
		if (isTracingAPI) trace("setSql(sql)"); 
		// never actually modify sql, like Statement does
		boolean hideSQL = jdbcConfig.checkForHideParameters(sql);
		setHidingParameters(hideSQL);
		setQueryText(sql);
	}

	@Override
	void setActive() throws SQLException {
		super.setActive();
		resetParameterList();
	}

	@Override
	public final java.sql.ResultSet executeQuery() throws SQLException {
		if (isTracingAPI) trace("executeQuery()");
		// log("ps.executeQuery()");
		setActive();
		try {
			if (qi == null) {
				return ps.executeQuery();
			}
			else {
				qi.setSimpleQuery(true);
				return wrapResultSet(ps.executeQuery());
			}
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public int executeUpdate() throws SQLException {
		if (isTracingAPI) trace("executeUpdate()"); 
		setActive();
		try {
			int ret = ps.executeUpdate();
			setInactive();
			return ret;
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	@Override
	public boolean execute() throws SQLException {
		if (isTracingAPI) trace("execute()"); 
		setActive();
		try {
			boolean ret = ps.execute();
			if (Util.isSelectStatement(qi.getQueryTextOnly())) {
				// SELECT is simple query
				qi.setSimpleQuery(true);
			}
			else {
				// don't mark inactive if it contains a SELECT statement
				setInactive();
			}
			return ret;
		}
		catch (SQLException e) {
			logException(e);
			throw e;
		}
	}

	void setParameter(int i, String parameterType) throws SQLException {
		if (isTracingAPI) trace("set" + parameterType + "("+i+", ...)"); 
		setParameter(i, parameterType, null);
	}

	void setParameter(int i, String parameterType, String value) throws SQLException {
		if (firstParameter) {
			firstParameter = false;
			if (firstExecute) {
				firstExecute = false;
			}
			else {
				newQuery();
			}
		}
		if (this.isHidingParameters()) {
			qi.addParameter(Integer.toString(i) + ":[" + parameterType + "](Hidden)");
		}
		else if (value == null) {
			qi.addParameter(Integer.toString(i) + ":[" + parameterType + "]NULL");
		}
		else {
			qi.addParameter(Integer.toString(i)
					+ ":["
					+ parameterType
					+ "]"
					+ value.substring(0, Math.min(100, value.length())));
		}
	}

	@Override
	public void addBatch() throws SQLException {
		if (isTracingAPI) trace("addBatch()"); 
		ps.addBatch();
		qi.addParameter("-- New Batch --");
	}

	@Override
	public void clearParameters() throws SQLException {
		if (isTracingAPI) trace("clearParameters()"); 
		resetParameterList();
		ps.clearParameters();
	}

	@Override
	public java.sql.ResultSetMetaData getMetaData() throws SQLException {
		if (isTracingAPI) trace("getMetaData()"); 
		return ps.getMetaData();
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		if (isTracingAPI) trace("getParameterMetaData()"); 
		return ps.getParameterMetaData();
	}

	@Override
	public void setArray(int i, Array x) throws SQLException {
		setParameter(i, "Array");
		ps.setArray(i, x);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		setParameter(parameterIndex, "AsciiStream");
		ps.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setBigDecimal(int parameterIndex, java.math.BigDecimal x) throws SQLException {
		setParameter(parameterIndex, "BigDecimal", x.toString());
		ps.setBigDecimal(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		setParameter(parameterIndex, "BinaryStream");
		ps.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setBlob(int i, Blob x) throws SQLException {
		setParameter(i, "Blob");
		ps.setBlob(i, x);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		setParameter(parameterIndex, "Boolean", Boolean.toString(x));
		ps.setBoolean(parameterIndex, x);
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		setParameter(parameterIndex, "Byte", Byte.toString(x));
		ps.setByte(parameterIndex, x);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		setParameter(parameterIndex, "ByteArray");
		ps.setBytes(parameterIndex, x);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		setParameter(parameterIndex, "CharacterStream");
		ps.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setClob(int i, Clob x) throws SQLException {
		setParameter(i, "Clob");
		ps.setClob(i, x);
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		ps.setCursorName(name);
	}

	@Override
	public void setDate(int parameterIndex, java.sql.Date x) throws SQLException {
		if(x==null) {
			setParameter(parameterIndex, "Date", "null");
		}
		else {
			setParameter(parameterIndex, "Date", x.toString());
		}
		ps.setDate(parameterIndex, x);
	}

	@Override
	public void setDate(int parameterIndex, java.sql.Date x, java.util.Calendar cal) throws SQLException {
		if(x==null) {
			setParameter(parameterIndex, "Date", "null");
		}
		else {
			setParameter(parameterIndex, "Date", x.toString());
		}
		ps.setDate(parameterIndex, x, cal);
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		setParameter(parameterIndex, "Double", Double.toString(x));
		ps.setDouble(parameterIndex, x);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		ps.setEscapeProcessing(enable);
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		ps.setFetchDirection(direction);
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		ps.setFetchSize(rows);
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		setParameter(parameterIndex, "Float", Float.toString(x));
		ps.setFloat(parameterIndex, x);
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		setParameter(parameterIndex, "Int", Integer.toString(x));
		ps.setInt(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		setParameter(parameterIndex, "Long", Long.toString(x));
		ps.setLong(parameterIndex, x);
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		ps.setMaxFieldSize(max);
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		ps.setMaxRows(max);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		setParameter(parameterIndex, "NULL");
		ps.setNull(parameterIndex, sqlType);
	}

	@Override
	public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
		setParameter(paramIndex, "NULL");
		ps.setNull(paramIndex, sqlType, typeName);
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		if(x==null) {
			setParameter(parameterIndex, "Object/null");
		}
		else {
			setParameter(parameterIndex, "Object/" + x.getClass().toString(), x.toString());
		}
		// log("setObject("+parameterIndex+","+x.getClass().getName()+")");
		ps.setObject(parameterIndex, x);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		if(x==null) {
			setParameter(parameterIndex, "Object/null");
		}
		else {
			setParameter(parameterIndex, "Object/" + getSqlType(targetSqlType), x.toString());
		}
		// log("setObject("+parameterIndex+","+x.getClass().getName()+","+targetSqlType+")");
		ps.setObject(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
		if(x==null) {
			setParameter(parameterIndex, "Object/" + getSqlType(targetSqlType), "null");
		}
		else {
			setParameter(parameterIndex, "Object/" + getSqlType(targetSqlType), x.toString());
		}
		// log("setObject("+parameterIndex+","+x.getClass().getName()+","+targetSqlType+","+scale+")");
		ps.setObject(parameterIndex, x, targetSqlType, scale);
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		ps.setQueryTimeout(seconds);
	}

	@Override
	public void setRef(int i, Ref x) throws SQLException {
		if(x==null) {
			setParameter(i, "Ref", "null");
		}
		else {
			setParameter(i, "Ref", x.toString());
		}
		ps.setRef(i, x);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		setParameter(parameterIndex, "Short", Short.toString(x));
		ps.setShort(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		// log("PreparedStatementImpl: setObject("+parameterIndex+",'"+x+"')");
		setParameter(parameterIndex, "String", x);
		ps.setString(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, java.sql.Time x) throws SQLException {
		if(x==null) {
			setParameter(parameterIndex, "Time", "null");
		}
		else {
			setParameter(parameterIndex, "Time", x.toString());
		}
		ps.setTime(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, java.sql.Time x, java.util.Calendar cal) throws SQLException {
		if(x==null) {
			setParameter(parameterIndex, "Time", "null");
		}
		else {
			setParameter(parameterIndex, "Time", x.toString());
		}
		ps.setTime(parameterIndex, x, cal);
	}

	@Override
	public void setTimestamp(int parameterIndex, java.sql.Timestamp x) throws SQLException {
		if(x==null) {
			setParameter(parameterIndex, "Timestamp", "null");
		}
		else {
			setParameter(parameterIndex, "Timestamp", x.toString());
		}
		ps.setTimestamp(parameterIndex, x);
	}

	@Override
	public void setTimestamp(int parameterIndex, java.sql.Timestamp x, java.util.Calendar cal) throws SQLException {
		if(x==null) {
			setParameter(parameterIndex, "Timestamp", "null");
		}
		else {
			setParameter(parameterIndex, "Timestamp", x.toString());
		}
		ps.setTimestamp(parameterIndex, x, cal);
	}

	@Override
	public void setURL(int parameterIndex, java.net.URL x) throws SQLException {
		if(x==null) {
			setParameter(parameterIndex, "URL", "null");
		}
		else {
			setParameter(parameterIndex, "URL", x.toString());
		}
		ps.setURL(parameterIndex, x);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		setParameter(parameterIndex, "UnicodeStream");
		ps.setUnicodeStream(parameterIndex, x, length);
	}

	String getSqlType(int type) {
		switch (type) {
		case Types.ARRAY:
			return "ARRAY";
		case Types.BIGINT:
			return "BIGINT";
		case Types.BINARY:
			return "BINARY";
		case Types.BIT:
			return "BIT";
		case Types.BLOB:
			return "BLOB";
		case Types.CHAR:
			return "CHAR";
		case Types.CLOB:
			return "CLOB";
		case Types.DATE:
			return "DATE";
		case Types.DECIMAL:
			return "DECIMAL";
		case Types.DISTINCT:
			return "DISTINCT";
		case Types.DOUBLE:
			return "DOUBLE";
		case Types.FLOAT:
			return "FLOAT";
		case Types.INTEGER:
			return "INTEGER";
		case Types.JAVA_OBJECT:
			return "JAVA_OBJECT";
		case Types.LONGVARBINARY:
			return "LONGVARBINARY";
		case Types.LONGVARCHAR:
			return "LONGVARCHAR";
		case Types.NULL:
			return "NULL";
		case Types.NUMERIC:
			return "NUMERIC";
		case Types.OTHER:
			return "OTHER";
		case Types.REAL:
			return "REAL";
		case Types.REF:
			return "REF";
		case Types.SMALLINT:
			return "SMALLINT";
		case Types.STRUCT:
			return "STRUCT";
		case Types.TIME:
			return "TIME";
		case Types.TIMESTAMP:
			return "TIMESTAMP";
		case Types.TINYINT:
			return "TINYINT";
		case Types.VARBINARY:
			return "VARBINARY";
		case Types.VARCHAR:
			return "VARCHAR";
		default:
			return "[Unknown]";
		}
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		ps.setAsciiStream(parameterIndex, x);		
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		ps.setAsciiStream(parameterIndex, x, length);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		ps.setBinaryStream(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		ps.setBinaryStream(parameterIndex, x, length);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		ps.setBlob(parameterIndex, inputStream);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		ps.setBlob(parameterIndex, inputStream, length);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		ps.setCharacterStream(parameterIndex, reader);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		ps.setCharacterStream(parameterIndex, reader, length);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		ps.setClob(parameterIndex, reader);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		ps.setClob(parameterIndex, reader, length);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		ps.setNCharacterStream(parameterIndex, value);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		ps.setNCharacterStream(parameterIndex, value, length);
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		ps.setNClob(parameterIndex, value);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		ps.setNClob(parameterIndex, reader);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		ps.setNClob(parameterIndex, reader, length);
	}

	@Override
	public void setNString(int parameterIndex, String value)
			throws SQLException {
		ps.setNString(parameterIndex, value);
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		ps.setRowId(parameterIndex, x);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		ps.setSQLXML(parameterIndex, xmlObject);
	}

	@Override
	public boolean isClosed() throws SQLException {
		return ps.isClosed();
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return ps.isPoolable();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		ps.setPoolable(poolable);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return ps.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return ps.unwrap(iface);
	}

}
