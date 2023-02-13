/*
 * CallableStatementImpl.java
 *
 * Created on July 2, 2004, 2:34 PM
 */

package com.seefusion;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;

/**
 *
 * 
 */
@SuppressWarnings("deprecation")
class CallableStatementImpl extends PreparedStatementImpl implements java.sql.CallableStatement {
    
    static String CLASSNAME = "CallableStatement";

    java.sql.CallableStatement cs;
    
    /**
     * Creates a new instance of CallableStatementImpl 
     */
    public CallableStatementImpl(ConnectionImpl c, java.sql.CallableStatement cs, String sql, JdbcConfig jdbcConfig, boolean isTracingAPI) throws SQLException {
        super(c, cs, sql, jdbcConfig, isTracingAPI);
        this.cs = cs;
    }
    
    @Override
	public Array getArray(int i) throws SQLException {
        return cs.getArray(i);
    }
    
    @Override
	public Array getArray(String parameterName) throws SQLException {
        return cs.getArray(parameterName);
    }
    
    @Override
	public java.math.BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return cs.getBigDecimal(parameterName);
    }
    
    @Override
	public java.math.BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return cs.getBigDecimal(parameterIndex);
    }
    
    @Override
	public java.math.BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        return cs.getBigDecimal(parameterIndex, scale);
    }
    
    @Override
	public Blob getBlob(int i) throws SQLException {
        return cs.getBlob(i);
    }
    
    @Override
	public Blob getBlob(String parameterName) throws SQLException {
        return cs.getBlob(parameterName);
    }
    
    @Override
	public boolean getBoolean(int parameterIndex) throws SQLException {
        return cs.getBoolean(parameterIndex);
    }
    
    @Override
	public boolean getBoolean(String parameterName) throws SQLException {
        return cs.getBoolean(parameterName);
    }
    
    @Override
	public byte getByte(int parameterIndex) throws SQLException {
        return cs.getByte(parameterIndex);
    }
    
    @Override
	public byte getByte(String parameterName) throws SQLException {
        return cs.getByte(parameterName);
    }
    
    @Override
	public byte[] getBytes(int parameterIndex) throws SQLException {
        return cs.getBytes(parameterIndex);
    }
    
    @Override
	public byte[] getBytes(String parameterName) throws SQLException {
        return cs.getBytes(parameterName);
    }
    
    @Override
	public Clob getClob(int i) throws SQLException {
        return cs.getClob(i);
    }
    
    @Override
	public Clob getClob(String parameterName) throws SQLException {
        return cs.getClob(parameterName);
    }
    
    @Override
	public Date getDate(int parameterIndex) throws SQLException {
        return cs.getDate(parameterIndex);
    }
    
    @Override
	public Date getDate(String parameterName) throws SQLException {
        return cs.getDate(parameterName);
    }
    
    @Override
	public Date getDate(int parameterIndex, java.util.Calendar cal) throws SQLException {
        return cs.getDate( parameterIndex, cal);
    }
    
    @Override
	public Date getDate(String parameterName, java.util.Calendar cal) throws SQLException {
        return cs.getDate(parameterName, cal);
    }
    
    @Override
	public double getDouble(int parameterIndex) throws SQLException {
        return cs.getDouble(parameterIndex);
    }
    
    @Override
	public double getDouble(String parameterName) throws SQLException {
        return cs.getDouble(parameterName);
    }
    
    @Override
	public float getFloat(int parameterIndex) throws SQLException {
        return cs.getFloat(parameterIndex);
    }
    
    @Override
	public float getFloat(String parameterName) throws SQLException {
        return cs.getFloat(parameterName);
    }
    
    @Override
	public int getInt(String parameterName) throws SQLException {
        return cs.getInt(parameterName);
    }
    
    @Override
	public int getInt(int parameterIndex) throws SQLException {
        return cs.getInt(parameterIndex);
    }
    
    @Override
	public long getLong(int parameterIndex) throws SQLException {
        return cs.getLong(parameterIndex);
    }
    
    @Override
	public long getLong(String parameterName) throws SQLException {
        return cs.getLong(parameterName);
    }
    
    @Override
	public Object getObject(int parameterIndex) throws SQLException {
        return cs.getObject(parameterIndex);
    }
    
    @Override
	public Object getObject(int i, java.util.Map<String, Class<?>> map) throws SQLException {
        return cs.getObject(i, map);
    }
    
    @Override
	public Object getObject(String parameterName, java.util.Map<String, Class<?>> map) throws SQLException {
        return cs.getObject(parameterName, map);
    }
    
    @Override
	public Object getObject(String parameterName) throws SQLException {
        return cs.getObject(parameterName);
    }
    
    @Override
	public Ref getRef(int i) throws SQLException {
        return cs.getRef(i);
    }
    
    @Override
	public Ref getRef(String parameterName) throws SQLException {
        return cs.getRef(parameterName);
    }
    
    @Override
	public short getShort(String parameterName) throws SQLException {
        return cs.getShort(parameterName);
    }
    
    @Override
	public short getShort(int parameterIndex) throws SQLException {
        return cs.getShort(parameterIndex);
    }
    
    @Override
	public String getString(String parameterName) throws SQLException {
        return cs.getString(parameterName);
    }
    
    @Override
	public String getString(int parameterIndex) throws SQLException {
        return cs.getString(parameterIndex);
    }
    
    @Override
	public Time getTime(String parameterName) throws SQLException {
        return cs.getTime(parameterName);
    }
    
    @Override
	public Time getTime(int parameterIndex) throws SQLException {
        return cs.getTime(parameterIndex);
    }
    
    @Override
	public Time getTime(String parameterName, java.util.Calendar cal) throws SQLException {
        return cs.getTime(parameterName, cal);
    }
    
    @Override
	public Time getTime(int parameterIndex, java.util.Calendar cal) throws SQLException {
        return cs.getTime(parameterIndex, cal);
    }
    
    @Override
	public Timestamp getTimestamp(String parameterName) throws SQLException {
        return cs.getTimestamp(parameterName);
    }
    
    @Override
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return cs.getTimestamp(parameterIndex);
    }
    
    @Override
	public Timestamp getTimestamp(String parameterName, java.util.Calendar cal) throws SQLException {
        return cs.getTimestamp(parameterName, cal);
    }
    
    @Override
	public Timestamp getTimestamp(int parameterIndex, java.util.Calendar cal) throws SQLException {
        return cs.getTimestamp(parameterIndex, cal);
    }
    
    @Override
	public java.net.URL getURL(int parameterIndex) throws SQLException {
        return cs.getURL(parameterIndex);
    }
    
    @Override
	public java.net.URL getURL(String parameterName) throws SQLException {
        return cs.getURL(parameterName);
    }
    
    @Override
	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        cs.registerOutParameter(parameterIndex, sqlType);
    }
    
    @Override
	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        cs.registerOutParameter(parameterName, sqlType);
    }
    
    @Override
	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        cs.registerOutParameter(parameterName, sqlType, typeName);
    }
    
    @Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        cs.registerOutParameter(parameterIndex, sqlType, scale);
    }
    
    @Override
	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        cs.registerOutParameter(parameterName, sqlType, scale);
    }
    
    @Override
	public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
        cs.registerOutParameter(paramIndex, sqlType, typeName);
    }
    
    @Override
	public void setDate(String parameterName, Date d) throws SQLException {
        cs.setDate(parameterName, d);
    }
    
    @Override
	public void setDate(String parameterName, Date d, java.util.Calendar c) throws SQLException {
        cs.setDate(parameterName, d, c);
    }
    
    @Override
	public void setTime(String parameterName, Time t) throws SQLException {
        cs.setTime(parameterName, t);
    }
    
    @Override
	public void setTime(String parameterName, Time t, java.util.Calendar c) throws SQLException {
        cs.setTime(parameterName, t, c);
    }
    
    @Override
	public void setTimestamp(String parameterName, Timestamp ts) throws SQLException {
        cs.setTimestamp(parameterName, ts);
    }
    
    @Override
	public void setTimestamp(String parameterName, Timestamp ts, java.util.Calendar c) throws SQLException  {
        cs.setTimestamp(parameterName, ts, c);
    }
    
    @Override
	public void setURL(String parameterName, java.net.URL url) throws SQLException {
        cs.setURL(parameterName, url);
    }
    
    @Override
	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        cs.setAsciiStream(parameterName, x, length);
    }
    
    @Override
	public void setBigDecimal(String parameterName, java.math.BigDecimal x) throws SQLException {
        cs.setBigDecimal(parameterName, x);
    }
    
    @Override
	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        cs.setBinaryStream(parameterName, x, length);
    }
    
    @Override
	public void setBoolean(String parameterName, boolean x) throws SQLException {
        cs.setBoolean(parameterName, x);
    }
    
    @Override
	public void setByte(String parameterName, byte x) throws SQLException {
        cs.setByte(parameterName, x);
    }
    
    @Override
	public void setBytes(String parameterName, byte[] x) throws SQLException {
        cs.setBytes(parameterName, x);
    }
    
    @Override
	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        cs.setCharacterStream(parameterName, reader, length);
    }
    
    @Override
	public void setDouble(String parameterName, double x) throws SQLException {
        cs.setDouble(parameterName, x);
    }
    
    @Override
	public void setFloat(String parameterName, float x) throws SQLException {
        cs.setFloat(parameterName, x);
    }
    
    @Override
	public void setInt(String parameterName, int x) throws SQLException {
        cs.setInt(parameterName, x);
    }
    
    @Override
	public void setLong(String parameterName, long x) throws SQLException {
        cs.setLong(parameterName, x);
    }
    
    @Override
	public void setNull(String parameterName, int sqlType) throws SQLException {
        cs.setNull(parameterName, sqlType);
    }
    
    @Override
	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        cs.setNull(parameterName, sqlType, typeName);
    }
    
    @Override
	public void setObject(String parameterName, Object x) throws SQLException {
        cs.setObject(parameterName, x);
    }
    
    @Override
	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        cs.setObject(parameterName, x, targetSqlType);
    }
    
    @Override
	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        cs.setObject(parameterName, x, targetSqlType, scale);
    }
    
    @Override
	public void setShort(String parameterName, short x) throws SQLException {
        cs.setShort(parameterName, x);
    }
    
    @Override
	public void setString(String parameterName, String x) throws SQLException {
        cs.setString(parameterName, x);
    }
    
    @Override
	public boolean wasNull() throws SQLException {
        return cs.wasNull();
    }

	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return cs.getCharacterStream(parameterIndex);
	}

	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {
		return cs.getCharacterStream(parameterName);
	}

	@Override
	public Reader getNCharacterStream(int arg0) throws SQLException {
		return cs.getNCharacterStream(arg0);
	}

	@Override
	public Reader getNCharacterStream(String arg0) throws SQLException {
		return cs.getNCharacterStream(arg0);
	}

	@Override
	public NClob getNClob(int arg0) throws SQLException {
		return cs.getNClob(arg0);
	}

	@Override
	public NClob getNClob(String arg0) throws SQLException {
		return cs.getNClob(arg0);
	}

	@Override
	public String getNString(int arg0) throws SQLException {
		return cs.getNString(arg0);
	}

	@Override
	public String getNString(String arg0) throws SQLException {
		return cs.getNString(arg0);
	}

	@Override
	public RowId getRowId(int arg0) throws SQLException {
		return cs.getRowId(arg0);
	}

	@Override
	public RowId getRowId(String arg0) throws SQLException {
		return cs.getRowId(arg0);
	}

	@Override
	public SQLXML getSQLXML(int arg0) throws SQLException {
		return cs.getSQLXML(arg0);
	}

	@Override
	public SQLXML getSQLXML(String arg0) throws SQLException {
		return cs.getSQLXML(arg0);
	}

	@Override
	public void setAsciiStream(String arg0, InputStream arg1)
			throws SQLException {
		cs.setAsciiStream(arg0, arg1);
	}

	@Override
	public void setAsciiStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		cs.setAsciiStream(arg0, arg1, arg2);		
	}

	@Override
	public void setBinaryStream(String arg0, InputStream arg1)
			throws SQLException {
		cs.setBinaryStream(arg0, arg1);		
	}

	@Override
	public void setBinaryStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		cs.setBinaryStream(arg0, arg1, arg2);
	}

	@Override
	public void setBlob(String arg0, Blob arg1) throws SQLException {
		cs.setBlob(arg0, arg1);
	}

	@Override
	public void setBlob(String arg0, InputStream arg1) throws SQLException {
		cs.setBlob(arg0, arg1);
	}

	@Override
	public void setBlob(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		cs.setBlob(arg0, arg1, arg2);
	}

	@Override
	public void setCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		cs.setCharacterStream(arg0, arg1);
	}

	@Override
	public void setCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		cs.setCharacterStream(arg0, arg1, arg2);
	}

	@Override
	public void setClob(String arg0, Clob arg1) throws SQLException {
		cs.setClob(arg0, arg1);
	}

	@Override
	public void setClob(String arg0, Reader arg1) throws SQLException {
		cs.setClob(arg0, arg1);
	}

	@Override
	public void setClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		cs.setClob(arg0, arg1, arg2);
	}

	@Override
	public void setNCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		cs.setNCharacterStream(arg0, arg1);
	}

	@Override
	public void setNCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		cs.setNCharacterStream(arg0, arg1, arg2);
	}		

	@Override
	public void setNClob(String arg0, NClob arg1) throws SQLException {
		cs.setNClob(arg0, arg1);
	}

	@Override
	public void setNClob(String arg0, Reader arg1) throws SQLException {
		cs.setNClob(arg0, arg1);
	}

	@Override
	public void setNClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		cs.setNClob(arg0, arg1, arg2);
	}

	@Override
	public void setNString(String arg0, String arg1) throws SQLException {
		cs.setNString(arg0, arg1);
	}

	@Override
	public void setRowId(String arg0, RowId arg1) throws SQLException {
		cs.setRowId(arg0, arg1);
	}

	@Override
	public void setSQLXML(String arg0, SQLXML arg1) throws SQLException {
		cs.setSQLXML(arg0, arg1);
	}

	@Override
	public void setAsciiStream(int arg0, InputStream arg1) throws SQLException {
		cs.setAsciiStream(arg0, arg1);
	}

	@Override
	public void setAsciiStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		cs.setAsciiStream(arg0, arg1, arg2);
	}

	@Override
	public void setBinaryStream(int arg0, InputStream arg1) throws SQLException {
		cs.setBinaryStream(arg0, arg1);
	}

	@Override
	public void setBinaryStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		cs.setBinaryStream(arg0, arg1, arg2);
	}

	@Override
	public void setBlob(int arg0, InputStream arg1) throws SQLException {
		cs.setBlob(arg0, arg1);
	}

	@Override
	public void setBlob(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		cs.setBlob(arg0, arg1, arg2);
	}

	@Override
	public void setCharacterStream(int arg0, Reader arg1) throws SQLException {
		cs.setCharacterStream(arg0, arg1);
	}

	@Override
	public void setCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		cs.setCharacterStream(arg0, arg1, arg2);
	}

	@Override
	public void setClob(int arg0, Reader arg1) throws SQLException {
		cs.setClob(arg0, arg1);
	}

	@Override
	public void setClob(int arg0, Reader arg1, long arg2) throws SQLException {
		cs.setClob(arg0, arg1, arg2);
	}

	@Override
	public void setNCharacterStream(int arg0, Reader arg1) throws SQLException {
		cs.setNCharacterStream(arg0, arg1);
	}

	@Override
	public void setNCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		cs.setNCharacterStream(arg0, arg1, arg2);
	}

	@Override
	public void setNClob(int arg0, NClob arg1) throws SQLException {
		cs.setNClob(arg0, arg1);
	}

	@Override
	public void setNClob(int arg0, Reader arg1) throws SQLException {
		cs.setNClob(arg0, arg1);
	}

	@Override
	public void setNClob(int arg0, Reader arg1, long arg2) throws SQLException {
		cs.setNClob(arg0, arg1, arg2);
	}

	@Override
	public void setNString(int arg0, String arg1) throws SQLException {
		cs.setNString(arg0, arg1);
	}

	@Override
	public void setRowId(int arg0, RowId arg1) throws SQLException {
		cs.setRowId(arg0, arg1);
	}

	@Override
	public void setSQLXML(int arg0, SQLXML arg1) throws SQLException {
		cs.setSQLXML(arg0, arg1);
	}

	@Override
	public boolean isClosed() throws SQLException {
		return cs.isClosed();
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return cs.isPoolable();
	}

	@Override
	public void setPoolable(boolean arg0) throws SQLException {
		cs.setPoolable(arg0);
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return cs.isWrapperFor(arg0);
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return cs.unwrap(arg0);
	}

	@Override
	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return cs.getObject(parameterIndex, type);
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return cs.getObject(parameterName, type);
	}
	
	// from Statement7:
	
	@Override
	ResultSet wrapResultSet(ResultSet rs) {
		return new ResultSetImpl(this, rs, qi, jdbcConfig, isTracingAPI);
	}
	
	@Override
	public void registerOutParameter(int arg0, SQLType arg1) throws SQLException {
		cs.registerOutParameter(arg0, arg1);
	}

	@Override
	public void registerOutParameter(String arg0, SQLType arg1) throws SQLException {
		cs.registerOutParameter(arg0, arg1);
	}

	@Override
	public void registerOutParameter(int arg0, SQLType arg1, int arg2) throws SQLException {
		cs.registerOutParameter(arg0, arg1, arg2);
	}

	@Override
	public void registerOutParameter(int arg0, SQLType arg1, String arg2) throws SQLException {
		cs.registerOutParameter(arg0, arg1, arg2);
	}

	@Override
	public void registerOutParameter(String arg0, SQLType arg1, int arg2) throws SQLException {
		cs.registerOutParameter(arg0, arg1, arg2);
	}

	@Override
	public void registerOutParameter(String arg0, SQLType arg1, String arg2) throws SQLException {
		cs.registerOutParameter(arg0, arg1, arg2);
	}

	@Override
	public void setObject(String arg0, Object arg1, SQLType arg2) throws SQLException {
		cs.setObject(arg0, arg1, arg2);
	}

	@Override
	public void setObject(String arg0, Object arg1, SQLType arg2, int arg3) throws SQLException {
		cs.setObject(arg0, arg1, arg2, arg3);
	}
}
