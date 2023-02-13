/**
 * 
 */
package com.seefusion;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Daryl
 * 
 */
class QueryResult implements Iterable<Map<String, Object>> {

	int numCols;

	int numRows;

	String[] aColNames;

	int[] aColTypes;

	ArrayList<ArrayList<Object>> aColData;

	HashMap<String, Integer> mColNameLookup;

	int currentRow;

	private QueryResult() {
	}

	static QueryResult getQueryResult(ResultSet rs) throws SQLException {
		return parseResultSet(rs, 0);
	}
	static QueryResult getQueryResult(ResultSet rs, int maxRows) throws SQLException {
		return parseResultSet(rs, maxRows);
	}

	static QueryResult parseResultSet(ResultSet rs, int maxRows) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int numRows = 0;
		int numCols = rsmd.getColumnCount();
		String[] aColNames = new String[numCols + 1];
		int[] aColTypes = new int[numCols + 1];
		ArrayList<ArrayList<Object>> aColData = new ArrayList<ArrayList<Object>>();
		HashMap<String, Integer> mColNameLookup = new HashMap<String, Integer>();

		// result sets are 1-based; add dummy arrayList in position 0
		aColData.add(new ArrayList<Object>());
		for (int i = 1; i <= numCols; i++) {
			aColNames[i] = rsmd.getColumnName(i);
			mColNameLookup.put(aColNames[i].toLowerCase(), i);
			aColTypes[i] = rsmd.getColumnType(i);
			aColData.add(new ArrayList<Object>());
		}
		while (rs.next() && (maxRows==0 || numRows < maxRows)) {
			numRows++;
			for (int i = 1; i <= numCols; i++) {
				aColData.get(i).add(rs.getObject(i));
			}
		}
		rs.close();

		QueryResult qr = new QueryResult();
		qr.setNumCols(numCols);
		qr.setNumRows(numRows);
		qr.setAColNames(aColNames);
		qr.setAColTypes(aColTypes);
		qr.setAColData(aColData);
		qr.setMColNameLookup(mColNameLookup);
		qr.currentRow = 1;
		return qr;

	}
	
	public Iterator<Map<String, Object>> iterator() {
		return new QRIterator(this);
	}

	class QRIterator implements Iterator<Map<String, Object>> {
		QueryResult qr;

		int currentRow;

		int numRows;

		QRIterator(QueryResult qr) {
			this.qr = qr;
			this.numRows = qr.recordCount();
			this.currentRow = 0;
		}

		public boolean hasNext() {
			return currentRow < numRows;
		}

		/*
		 * This returns a Map<String, Object> of the current row (name/value pairs)
		 * 
		 * @see java.util.Iterator#next() @returns Boolean.TRUE or Boolean.FALSE
		 *      indicating whether there was another row
		 */
		public Map<String, Object> next() {
			if (currentRow > numRows) {
				return null;
			}
			currentRow++;
			if (currentRow <= numRows) {
				Map<String, Object> ret = new HashMap<String, Object>();
				for(int i=0; i < aColNames.length; i++) {
					ret.put(aColNames[i], aColData.get(i).get(currentRow));
				}
				return ret;
			}
			else {
				return null;
			}
		}

		public void remove() throws UnsupportedOperationException {
			throw new UnsupportedOperationException();
		}

		Object get(int col) {
			if (numRows == 0) {
				return null;
			}
			else {
				return qr.get(col, currentRow);
			}
		}

		Object get(String col) {
			if (numRows == 0) {
				return null;
			}
			else {
				return qr.get(col, currentRow);
			}
		}

		String getString(String col) {
			return get(col).toString();
		}

		String getString(int col) {
			return get(col).toString();
		}

		int getInt(String col) {
			return Util.objToInt(get(col));
		}

		int getInt(int col) {
			return Util.objToInt(get(col));
		}

		long getLong(String col) {
			return Util.objToLong(get(col));
		}

		long getLong(int col) {
			return Util.objToLong(get(col));
		}

		Timestamp getTimestamp(String col) {
			return Util.objToTimestamp(get(col));
		}

		Timestamp getTimestamp(int col) {
			return Util.objToTimestamp(get(col));
		}
	}

	Object get(int col, int row) {
		return aColData.get(col).get(row-1);
	}

	Object get(int col) {
		if (numRows == 0) {
			return null;
		}
		else {
			return get(col, 1);
		}
	}

	Object get(String col, int row) {
		return get(mColNameLookup.get(col.toLowerCase()).intValue(), row);
	}

	Object get(String col) {
		if (numRows == 0) {
			return null;
		}
		else {
			return get(col, 1);
		}
	}

	void put(int col, int row, Object data) {
		aColData.get(col).set(row-1, data);
	}

	void put(String col, int row, Object data) {
		put(mColNameLookup.get(col.toLowerCase()).intValue(), row, data);
	}

	String getString(String col) {
		return get(col).toString();
	}

	String getString(int col) {
		return get(col).toString();
	}

	String getString(String col, int row) {
		return get(col, row).toString();
	}

	String getString(int col, int row) {
		return get(col, row).toString();
	}

	int getInt(String col) {
		return Util.objToInt(get(col));
	}

	int getInt(int col) {
		return Util.objToInt(get(col));
	}

	int getInt(String col, int row) {
		return Util.objToInt(get(col, row));
	}

	int getInt(int col, int row) {
		return Util.objToInt(get(col, row));
	}

	long getLong(String col) {
		return Util.objToLong(get(col));
	}

	long getLong(int col) {
		return Util.objToLong(get(col));
	}

	long getLong(String col, int row) {
		return Util.objToLong(get(col, row));
	}

	long getLong(int col, int row) {
		return Util.objToLong(get(col, row));
	}

	Timestamp getTimestamp(String col) {
		return Util.objToTimestamp(get(col));
	}

	Timestamp getTimestamp(int col) {
		return Util.objToTimestamp(get(col));
	}

	Timestamp getTimestamp(String col, int row) {
		return Util.objToTimestamp(get(col, row));
	}

	Timestamp getTimestamp(int col, int row) {
		return Util.objToTimestamp(get(col, row));
	}

	/**
	 * @return Returns the aColData.
	 */
	ArrayList<ArrayList<Object>> getAColData() {
		return this.aColData;
	}

	/**
	 * @param colData
	 *            The aColData to set.
	 */
	void setAColData(ArrayList<ArrayList<Object>> colData) {
		this.aColData = colData;
	}

	/**
	 * @return Returns the aColNames.
	 */
	String[] getAColNames() {
		return this.aColNames;
	}

	/**
	 * @param colNames
	 *            The aColNames to set.
	 */
	void setAColNames(String[] colNames) {
		this.aColNames = colNames;
	}

	/**
	 * @return Returns the aColTypes.
	 */
	int[] getAColTypes() {
		return this.aColTypes;
	}

	/**
	 * @param colTypes
	 *            The aColTypes to set.
	 */
	void setAColTypes(int[] colTypes) {
		this.aColTypes = colTypes;
	}

	/**
	 * @return Returns the mColNameLookup.
	 */
	HashMap<String, Integer> getMColNameLookup() {
		return this.mColNameLookup;
	}

	/**
	 * @param colNameLookup
	 *            The mColNameLookup to set.
	 */
	void setMColNameLookup(HashMap<String, Integer> colNameLookup) {
		this.mColNameLookup = colNameLookup;
	}

	/**
	 * @return Returns the numCols.
	 */
	int getNumCols() {
		return this.numCols;
	}

	/**
	 * @param numCols
	 *            The numCols to set.
	 */
	void setNumCols(int numCols) {
		this.numCols = numCols;
	}

	/**
	 * @return Returns the numRows.
	 */
	int recordCount() {
		return this.numRows;
	}

	/**
	 * @param numRows
	 *            The numRows to set.
	 */
	void setNumRows(int numRows) {
		this.numRows = numRows;
	}

}
