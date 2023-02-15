/*
 * ResultSetImpl.java
 *
 * Wraps a connection's ResultSet object so SeeFusion can track relative time elapsed and number of rows returned
 */

package com.seefusion;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.logging.Level;
import java.util.logging.Logger;


@SuppressWarnings("deprecation")
class ResultSetImpl implements java.sql.ResultSet {
	
	private static final Logger LOG = Logger.getLogger(ResultSetImpl.class.getName());

	final String CLASSNAME = "ResultSet";

	java.sql.ResultSet rs;

	// this will actually be some form of seefusion.StatementImpl:
	StatementImpl s;
	
	QueryInfo qi;

	Object objectNumLock = new Object();

	static long prevObjectNum = 0;

	boolean exceedsThreshold = false;

	long objectNum;

	int currentRow = 0;

	int notifyCount = 0;

	int remindCount = 0;

	boolean isKilled = false;

	int rowLimit;

	boolean rowLimitThrowsException;

	JdbcConfig jdbcConfig;

	boolean isActive = true;

	boolean isTracingAPI = false;

	boolean isTracingAPIdetail = true;
	
	long startTick = 0;

	long endTick = 0;
	
	/**
	 * Creates a new instance of ResultSetImpl
	 */
	public ResultSetImpl(StatementImpl s, java.sql.ResultSet rs, QueryInfo qi, JdbcConfig jdbcConfig, boolean isTracingAPI) {
		this.s = s;
		this.rs = rs;
		this.qi = qi;
		this.qi.setResultSet(this);
		this.jdbcConfig = jdbcConfig;
		this.rowLimit = jdbcConfig.getRowLimit();
		this.rowLimitThrowsException = jdbcConfig.isRowLimitThrowingException();
		this.notifyCount = jdbcConfig.getNotifyCount();
		this.remindCount = jdbcConfig.getRemindCount();
		this.isTracingAPI = isTracingAPI;
		this.startTick = System.currentTimeMillis();
		this.endTick = 0;

		synchronized (objectNumLock) {
			prevObjectNum++;
			this.objectNum = prevObjectNum;
		}
		if (isTracingAPI) LOG.finer("Created");

	}
	
	long sfGetElapsedTime() {
		if (startTick == 0) {
			return -2;
		} else if (endTick == 0) {
			return System.currentTimeMillis() - startTick;
		} else {
			return endTick - startTick;
		}
	}

	int sfGetRowCount() {
		return currentRow;
	}

	void kill() {
		isKilled = true;
	}

	@Override
	public boolean next() throws SQLException {
		if (isKilled) {
			s.setInactive();
			s.throwSQLException();
		}

		if (notifyCount == 0) {
			// do nothing
		}
		else if (currentRow == notifyCount) {
			LOG.info("resultSet at " + notifyCount + " rows for statement " + s.getSql() + "\r\n");
			exceedsThreshold = true;
		}
		else if ((currentRow > notifyCount) && (remindCount > 0) && ((currentRow - notifyCount) % remindCount == 0)) {
			LOG.info("resultSet at " + currentRow + " rows");
		}
		boolean next;
		try {
			next = rs.next();
		}
		catch (SQLException e) {
			s.setInactive();
			s.setException(e.toString());
			throw e;
		}
		if (isTracingAPIdetail) LOG.finer("next()==" + next);
		if (next) {
			if (rowLimit > 0 && currentRow == rowLimit) {
				if (this.rowLimitThrowsException) {
					throw new SeeFusionKillSQLException("Resultset met rowLimit of " + rowLimit + " rows.");
				}
				else {
					s.setException("WARNING: Row limit exceeded, resultset closed.");
					return false;
				}
			}
			currentRow++;
			return true;
		}
		else {
			setInactive();
			return false;
		}
	}

	@Override
	public void close() throws SQLException {
		if (isTracingAPI) LOG.finer("close(): " + currentRow + " rows");
		if (exceedsThreshold) {
			LOG.info("resultSet closed at " + currentRow + " rows");
		}
		setInactive();
		rs.close();
	}

	private void setInactive() throws SQLException {
		//Logger.debug("ResultSet.setInactive()");
		if (this.isActive) {
			this.isActive = false;
			this.endTick = System.currentTimeMillis();
			this.s.setInactive();
		}
	}

	@Override
	public void updateInt(String columnName, int x) throws SQLException {
		rs.updateInt(columnName, x);
	}

	@Override
	public int getConcurrency() throws SQLException {
		return rs.getConcurrency();
	}

	@Override
	public String getString(String columnName) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getString('"+columnName+"')");
		return rs.getString(columnName);
	}

	@Override
	public boolean absolute(int row) throws SQLException {
		return rs.absolute(row);
	}

	@Override
	public boolean isFirst() throws SQLException {
		return rs.isFirst();
	}

	@Override
	public java.sql.Time getTime(String columnName, java.util.Calendar cal) throws SQLException {
		return rs.getTime(columnName, cal);
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		rs.updateDouble(columnIndex, x);
	}

	@Override
	public void updateRow() throws SQLException {
		rs.updateRow();
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getCharacterStream("+columnIndex+")");
		return rs.getCharacterStream(columnIndex);
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		rs.updateByte(columnIndex, x);
	}

	@Override
	public void updateNull(String columnName) throws SQLException {
		rs.updateNull(columnName);
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		rs.cancelRowUpdates();
	}

	@Override
	public short getShort(String columnName) throws SQLException {
		return rs.getShort(columnName);
	}

	@Override
	public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
		rs.updateAsciiStream(columnName, x, length);
	}

	@Override
	public java.sql.Array getArray(int i) throws SQLException {
		return rs.getArray(i);
	}

	@Override
	public boolean first() throws SQLException {
		return rs.first();
	}

	@Override
	public void updateBigDecimal(String columnName, java.math.BigDecimal x) throws SQLException {
		rs.updateBigDecimal(columnName, x);
	}

	@Override
	public java.sql.Time getTime(String columnName) throws SQLException {
		return rs.getTime(columnName);
	}

	@Override
	public java.net.URL getURL(int columnIndex) throws SQLException {
		return rs.getURL(columnIndex);
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		return rs.getBoolean(columnIndex);
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		rs.updateShort(columnIndex, x);
	}

	@Override
	public String getCursorName() throws SQLException {
		return rs.getCursorName();
	}

	@Override
	public java.sql.Array getArray(String colName) throws SQLException {
		return rs.getArray(colName);
	}

	@Override
	public void updateDouble(String columnName, double x) throws SQLException {
		rs.updateDouble(columnName, x);
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		return rs.rowDeleted();
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getBytes("+columnIndex+")");
		return rs.getBytes(columnIndex);
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		rs.updateInt(columnIndex, x);
	}

	@Override
	public void clearWarnings() throws SQLException {
		rs.clearWarnings();
	}

	@Override
	public java.sql.Timestamp getTimestamp(String columnName) throws SQLException {
		return rs.getTimestamp(columnName);
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		rs.updateCharacterStream(columnIndex, x, length);
	}

	@Override
	public void refreshRow() throws SQLException {
		rs.refreshRow();
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
		rs.updateObject(columnIndex, x, scale);
	}

	@Override
	public boolean rowInserted() throws SQLException {
		return rs.rowInserted();
	}

	@Override
	public int getType() throws SQLException {
		return rs.getType();
	}

	@Override
	public void updateByte(String columnName, byte x) throws SQLException {
		rs.updateByte(columnName, x);
	}

	@Override
	public java.sql.ResultSetMetaData getMetaData() throws SQLException {
		return rs.getMetaData();
	}

	@Override
	public void updateClob(String columnName, java.sql.Clob x) throws SQLException {
		rs.updateClob(columnName, x);
	}

	@Override
	public java.sql.Clob getClob(int i) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getClob('"+i+"')");
		return rs.getClob(i);
	}

	@Override
	public java.sql.Time getTime(int columnIndex) throws SQLException {
		return rs.getTime(columnIndex);
	}

	@Override
	public java.sql.Date getDate(int columnIndex) throws SQLException {
		return rs.getDate(columnIndex);
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return rs.isBeforeFirst();
	}

	@Override
	public java.sql.Time getTime(int columnIndex, java.util.Calendar cal) throws SQLException {
		return rs.getTime(columnIndex, cal);
	}

	@Override
	public int findColumn(String columnName) throws SQLException {
		return rs.findColumn(columnName);
	}

	@Override
	public void updateTime(String columnName, java.sql.Time x) throws SQLException {
		rs.updateTime(columnName, x);
	}

	@Override
	public void updateShort(String columnName, short x) throws SQLException {
		rs.updateShort(columnName, x);
	}

	@Override
	public void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
		rs.updateDate(columnIndex, x);
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		rs.updateNull(columnIndex);
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		rs.updateBoolean(columnIndex, x);
	}

	@Override
	public float getFloat(int columnIndex) throws SQLException {
		return rs.getFloat(columnIndex);
	}

	@Override
	public java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException {
		return rs.getTimestamp(columnIndex);
	}

	@Override
	public void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
		rs.updateClob(columnIndex, x);
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		return rs.getShort(columnIndex);
	}

	@Override
	public void updateArray(String columnName, java.sql.Array x) throws SQLException {
		rs.updateArray(columnName, x);
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		rs.updateBlob(columnIndex, x);
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		return rs.getByte(columnIndex);
	}

	@Override
	public java.net.URL getURL(String columnName) throws SQLException {
		return rs.getURL(columnName);
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		rs.setFetchDirection(direction);
	}

	@Override
	public void updateObject(String columnName, Object x) throws SQLException {
		rs.updateObject(columnName, x);
	}

	@Override
	public void updateBlob(String columnName, Blob x) throws SQLException {
		rs.updateBlob(columnName, x);
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		rs.updateLong(columnIndex, x);
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		return rs.getLong(columnIndex);
	}

	@Override
	public void updateBigDecimal(int columnIndex, java.math.BigDecimal x) throws SQLException {
		rs.updateBigDecimal(columnIndex, x);
	}

	@Override
	public byte getByte(String columnName) throws SQLException {
		return rs.getByte(columnName);
	}

	@Override
	public Object getObject(int i, java.util.Map<String, Class<?>> map) throws SQLException {
		Object ret = rs.getObject(i, map);
		if (isTracingAPIdetail) {
			if(ret==null) {
				LOG.finer("getObject("+i+", [map]): null");
			}
			else {
				String className = ret.getClass().getName();
				//if(ret instanceof Blob) {
				//	ret = new BlobImpl((Blob)ret, currentRow);
				//}
				LOG.finer("getObject("+i+", [map]): " + className);
			}
		}
		if(ret instanceof Blob && jdbcConfig.isConvertingBlobToBytes()) {
			// hack to make ColdFusion work correctly with BLOBs
			Blob blob = (Blob)ret;
			LOG.finer("Converting Blob to byte[" + blob.length() + "]");
			ret = blob.getBytes(0, (int)blob.length());
		}
		return ret;
	}

	@Override
	public java.sql.Date getDate(String columnName) throws SQLException {
		return rs.getDate(columnName);
	}

	@Override
	public boolean wasNull() throws SQLException {
		return rs.wasNull();
	}

	@Override
	public void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
		rs.updateRef(columnIndex, x);
	}

	@Override
	public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
		rs.updateArray(columnIndex, x);
	}

	@Override
	public int getRow() throws SQLException {
		return rs.getRow();
	}

	@Override
	public int getFetchSize() throws SQLException {
		return rs.getFetchSize();
	}

	@Override
	public void updateTimestamp(String columnName, java.sql.Timestamp x) throws SQLException {
		rs.updateTimestamp(columnName, x);
	}

	@Override
	public long getLong(String columnName) throws SQLException {
		return rs.getLong(columnName);
	}

	@Override
	public java.sql.Statement getStatement() throws SQLException {
		return s;
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		rs.updateBytes(columnIndex, x);
	}

	@Override
	public java.math.BigDecimal getBigDecimal(String columnName) throws SQLException {
		return rs.getBigDecimal(columnName);
	}

	@Override
	public boolean previous() throws SQLException {
		return rs.previous();
	}

	@Override
	public void updateCharacterStream(String columnName, Reader reader, int length)
			throws SQLException {
		rs.updateCharacterStream(columnName, reader, length);
	}

	@Override
	public void updateLong(String columnName, long x) throws SQLException {
		rs.updateLong(columnName, x);
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		rs.updateBinaryStream(columnIndex, x, length);
	}

	@Override
	public Reader getCharacterStream(String columnName) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getCharacterStream('"+columnName+"')");
		return rs.getCharacterStream(columnName);
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		Object ret = rs.getObject(columnIndex); 
		if (isTracingAPIdetail) {
			if(ret==null) {
				LOG.finer("getObject("+columnIndex+"): null");
			}
			else {
				String className = ret.getClass().getName();
				//if(ret instanceof Blob) {
					//ret = new BlobImpl((Blob)ret, currentRow);
				//}
				LOG.finer("getObject("+columnIndex+"): " + className);
			}
		}
		if(ret instanceof Blob) {
			// hack to make ColdFusion work correctly with BLOBs
			Blob blob = (Blob)ret;
			int len = (int)blob.length();
			byte[] ret2 = blob.getBytes(1, (int)blob.length());
			ret = ret2;
			if (isTracingAPIdetail) {
				LOG.finer("API reports " + len + " bytes; byte array returned has length==" + ret2.length);
			}
		}
		return ret;
	}

	@Override
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getUnicodeStream('"+columnName+"')");
		return rs.getUnicodeStream(columnName);
	}

	@Override
	public String getString(int columnIndex) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getString("+columnIndex+")");
		return rs.getString(columnIndex);
	}

	@Override
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		rs.updateBytes(columnName, x);
	}

	@Override
	public byte[] getBytes(String columnName) throws SQLException {
		return rs.getBytes(columnName);
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		rs.updateAsciiStream(columnIndex, x, length);
	}

	@Override
	public Object getObject(String colName, java.util.Map<String, Class<?>> map) throws SQLException {
		Object ret = rs.getObject(colName, map);
		if (isTracingAPIdetail) {
			if(ret==null) {
				LOG.finer("getObject('"+colName+"', [map]): null");
			}
			else {
				String className = ret.getClass().getName();
				//if(ret instanceof Blob) {
					//ret = new BlobImpl((Blob)ret, currentRow);
				//}
				LOG.finer("getObject('"+colName+"', [map]): " + className);
			}
		}
		if(ret instanceof Blob) {
			// hack to make ColdFusion work correctly with BLOBs
			Blob blob = (Blob)ret;
			ret = blob.getBytes(0, (int)blob.length());
		}
		return ret;
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		rs.moveToInsertRow();
	}

	@Override
	public java.sql.SQLWarning getWarnings() throws SQLException {
		return rs.getWarnings();
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		return rs.relative(rows);
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		rs.updateString(columnIndex, x);
	}

	@Override
	public void updateDate(String columnName, java.sql.Date x) throws SQLException {
		rs.updateDate(columnName, x);
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		return rs.getDouble(columnIndex);
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		rs.setFetchSize(rows);
	}

	@Override
	public void updateRef(String columnName, java.sql.Ref x) throws SQLException {
		rs.updateRef(columnName, x);
	}

	@Override
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		rs.updateBoolean(columnName, x);
	}

	@Override
	public Object getObject(String columnName) throws SQLException {
		Object ret = rs.getObject(columnName);
		if (isTracingAPIdetail) {
			if(ret==null) {
				LOG.finer("getObject('"+columnName+"'): null");
			}
			else {
				String className = ret.getClass().getName();
				//if(ret instanceof Blob) {
				//	ret = new BlobImpl((Blob)ret, currentRow);
				//}
				LOG.finer("getObject('"+columnName+"'): " + className);
			}
		}
		if(ret instanceof Blob) {
			// hack to make ColdFusion work correctly with BLOBs
			Blob blob = (Blob)ret;
			ret = blob.getBytes(0, (int)blob.length());
		}
		return ret;
	}

	@Override
	public boolean last() throws SQLException {
		return rs.last();
	}

	@Override
	public int getInt(String columnName) throws SQLException {
		return rs.getInt(columnName);
	}

	@Override
	public void beforeFirst() throws SQLException {
		rs.beforeFirst();
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return rs.getFetchDirection();
	}

	@Override
	public Blob getBlob(int i) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getBlob("+i+")");
		return rs.getBlob(i);
	}

	@Override
	public java.sql.Ref getRef(int i) throws SQLException {
		return rs.getRef(i);
	}

	@Override
	public void insertRow() throws SQLException {
		rs.insertRow();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		rs.moveToCurrentRow();
	}

	@Override
	public java.math.BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return rs.getBigDecimal(columnIndex, scale);
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		rs.updateObject(columnIndex, x);
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return rs.isAfterLast();
	}

	@Override
	public java.sql.Clob getClob(String colName) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getClob('"+colName+"')");
		return rs.getClob(colName);
	}

	@Override
	public java.sql.Date getDate(int columnIndex, java.util.Calendar cal) throws SQLException {
		return rs.getDate(columnIndex, cal);
	}

	@Override
	public InputStream getAsciiStream(String columnName) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getAsciiStream('"+columnName+"')");
		return rs.getAsciiStream(columnName);
	}

	@Override
	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
		rs.updateBinaryStream(columnName, x, length);
	}

	@Override
	public double getDouble(String columnName) throws SQLException {
		return rs.getDouble(columnName);
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		return rs.getInt(columnIndex);
	}

	@Override
	public java.sql.Timestamp getTimestamp(String columnName, java.util.Calendar cal) throws SQLException {
		return rs.getTimestamp(columnName, cal);
	}

	@Override
	public java.sql.Date getDate(String columnName, java.util.Calendar cal) throws SQLException {
		return rs.getDate(columnName, cal);
	}

	@Override
	public void updateTime(int columnIndex, java.sql.Time x) throws SQLException {
		rs.updateTime(columnIndex, x);
	}

	@Override
	public boolean rowUpdated() throws SQLException {
		return rs.rowUpdated();
	}

	@Override
	public void updateObject(String columnName, Object x, int scale) throws SQLException {
		rs.updateObject(columnName, x, scale);
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getBinaryStream("+columnIndex+")");
		return rs.getBinaryStream(columnIndex);
	}

	@Override
	public java.sql.Timestamp getTimestamp(int columnIndex, java.util.Calendar cal) throws SQLException {
		return rs.getTimestamp(columnIndex, cal);
	}

	@Override
	public float getFloat(String columnName) throws SQLException {
		return rs.getFloat(columnName);
	}

	@Override
	public void deleteRow() throws SQLException {
		rs.deleteRow();
	}

	@Override
	public void afterLast() throws SQLException {
		rs.afterLast();
	}

	@Override
	public InputStream getBinaryStream(String columnName) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getBinaryStream('"+columnName+"')");
		return rs.getBinaryStream(columnName);
	}

	@Override
	public void updateTimestamp(int columnIndex, java.sql.Timestamp x) throws SQLException {
		rs.updateTimestamp(columnIndex, x);
	}

	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getUnicodeStream("+columnIndex+")");
		return rs.getUnicodeStream(columnIndex);
	}

	@Override
	public void updateString(String columnName, String x) throws SQLException {
		rs.updateString(columnName, x);
	}

	@Override
	public java.math.BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		return rs.getBigDecimal(columnName, scale);
	}

	@Override
	public boolean getBoolean(String columnName) throws SQLException {
		return rs.getBoolean(columnName);
	}

	@Override
	public boolean isLast() throws SQLException {
		return rs.isLast();
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getAsciiStream("+columnIndex+")");
		return rs.getAsciiStream(columnIndex);
	}

	@Override
	public java.math.BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return rs.getBigDecimal(columnIndex);
	}

	@Override
	public void updateFloat(String columnName, float x) throws SQLException {
		rs.updateFloat(columnName, x);
	}

	@Override
	public java.sql.Ref getRef(String colName) throws SQLException {
		return rs.getRef(colName);
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		rs.updateFloat(columnIndex, x);
	}

	@Override
	public Blob getBlob(String colName) throws SQLException {
		if (isTracingAPIdetail) LOG.finer("getBlob('"+colName+"')");
		return rs.getBlob(colName);
	}

	@Override
	public int getHoldability() throws SQLException {
		return rs.getHoldability();
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return rs.getNCharacterStream(columnIndex);
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return rs.getNCharacterStream(columnLabel);
	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		return rs.getNClob(columnIndex);
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		return rs.getNClob(columnLabel);
	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		return rs.getNString(columnIndex);
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		return rs.getNString(columnLabel);
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		return rs.getRowId(columnIndex);
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		return rs.getRowId(columnLabel);
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return rs.getSQLXML(columnIndex);
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return rs.getSQLXML(columnLabel);
	}

	@Override
	public boolean isClosed() throws SQLException {
		return rs.isClosed();
	}

	@Override
	public void updateAsciiStream(int arg0, InputStream arg1)
			throws SQLException {
		rs.updateAsciiStream(arg0, arg1);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream arg1)
			throws SQLException {
		rs.updateAsciiStream(columnLabel, arg1);
	}

	@Override
	public void updateAsciiStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		rs.updateAsciiStream(arg0, arg1, arg2);
	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream arg1, long arg2)
			throws SQLException {
		rs.updateAsciiStream(columnLabel, arg1, arg2);
	}

	@Override
	public void updateBinaryStream(int arg0, InputStream arg1)
			throws SQLException {
		rs.updateBinaryStream(arg0, arg1);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream arg1)
			throws SQLException {
		rs.updateBinaryStream(columnLabel, arg1);
	}

	@Override
	public void updateBinaryStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		rs.updateBinaryStream(arg0, arg1, arg2);
	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream arg1, long arg2)
			throws SQLException {
		rs.updateBinaryStream(columnLabel, arg1, arg2);
	}

	@Override
	public void updateBlob(int arg0, InputStream arg1) throws SQLException {
		rs.updateBlob(arg0, arg1);
	}

	@Override
	public void updateBlob(String columnLabel, InputStream arg1) throws SQLException {
		rs.updateBlob(columnLabel, arg1);
	}

	@Override
	public void updateBlob(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		rs.updateBlob(arg0, arg1, arg2);
	}

	@Override
	public void updateBlob(String columnLabel, InputStream arg1, long arg2)
			throws SQLException {
		rs.updateBlob(columnLabel, arg1, arg2);
	}

	@Override
	public void updateCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		rs.updateCharacterStream(arg0, arg1);		
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader arg1)
			throws SQLException {
		rs.updateCharacterStream(columnLabel, arg1);
	}

	@Override
	public void updateCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		rs.updateCharacterStream(arg0, arg1, arg2);
	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader arg1, long arg2)
			throws SQLException {
		rs.updateCharacterStream(columnLabel, arg1, arg2);
	}

	@Override
	public void updateClob(int arg0, Reader arg1) throws SQLException {
		rs.updateClob(arg0, arg1);
	}

	@Override
	public void updateClob(String columnLabel, Reader arg1) throws SQLException {
		rs.updateClob(columnLabel, arg1);
	}

	@Override
	public void updateClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		rs.updateClob(arg0, arg1, arg2);
	}

	@Override
	public void updateClob(String columnLabel, Reader arg1, long arg2)
			throws SQLException {
		rs.updateClob(columnLabel, arg1, arg2);
	}

	@Override
	public void updateNCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		rs.updateNCharacterStream(arg0, arg1);
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader arg1)
			throws SQLException {
		rs.updateNCharacterStream(columnLabel, arg1);
	}

	@Override
	public void updateNCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		rs.updateNCharacterStream(arg0, arg1, arg2);
	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader arg1, long arg2)
			throws SQLException {
		rs.updateNCharacterStream(columnLabel, arg1, arg2);
	}

	@Override
	public void updateNClob(int arg0, NClob arg1) throws SQLException {
		rs.updateNClob(arg0, arg1);
	}

	@Override
	public void updateNClob(String columnLabel, NClob arg1) throws SQLException {
		rs.updateNClob(columnLabel, arg1);
	}

	@Override
	public void updateNClob(int arg0, Reader arg1) throws SQLException {
		rs.updateNClob(arg0, arg1);		
	}

	@Override
	public void updateNClob(String columnLabel, Reader arg1) throws SQLException {
		rs.updateNClob(columnLabel, arg1);
	}

	@Override
	public void updateNClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		rs.updateNClob(arg0, arg1, arg2);
	}

	@Override
	public void updateNClob(String columnLabel, Reader arg1, long arg2)
			throws SQLException {
		rs.updateNClob(columnLabel, arg1, arg2);
	}

	@Override
	public void updateNString(int arg0, String arg1) throws SQLException {
		rs.updateNString(arg0, arg1);
	}

	@Override
	public void updateNString(String columnLabel, String arg1) throws SQLException {
		rs.updateNString(columnLabel, arg1);
	}

	@Override
	public void updateRowId(int arg0, RowId arg1) throws SQLException {
		rs.updateRowId(arg0, arg1);
	}

	@Override
	public void updateRowId(String columnLabel, RowId arg1) throws SQLException {
		rs.updateRowId(columnLabel, arg1);
	}

	@Override
	public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
		rs.updateSQLXML(arg0, arg1);
	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML arg1) throws SQLException {
		rs.updateSQLXML(columnLabel, arg1);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		if(iface.isInstance(rs))
			return true;
		else 	
			return rs.isWrapperFor(iface);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> iface) throws SQLException {
		if(iface.isInstance(rs))
			return (T) rs;
		else 	
			return rs.unwrap(iface);
	}

	void logException(Exception e) throws SQLException {
		LOG.log(Level.SEVERE, CLASSNAME + "[" + objectNum + "]: ", e);
		setInactive();
	}
	
	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return rs.getObject(columnIndex, type);
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
		return rs.getObject(columnLabel, type);
	}
	
}
