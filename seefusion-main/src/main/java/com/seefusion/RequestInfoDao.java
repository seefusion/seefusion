package com.seefusion;

import static com.seefusion.DbLoggerColumn.ColumnType.DATETIME;
import static com.seefusion.DbLoggerColumn.ColumnType.INT;
import static com.seefusion.DbLoggerColumn.ColumnType.LONG;
import static com.seefusion.DbLoggerColumn.ColumnType.VARCHAR;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class RequestInfoDao extends SeeDAO<RequestInfo> {

	/*
        +"BeginTime datetime NOT NULL"
        +",EndTime datetime NOT NULL"
        +",ServerName varchar (50) NULL"
        +",RequestURI varchar (200) NULL"
        +",UrlQueryString varchar (200) NULL"
        +",RemoteIP varchar (16) NULL"
        +",TotalTimeMs int NOT NULL"
        +",TimeToFirstByteMs int NULL"
        +",NumQueries int NOT NULL"
        +",TotalQueryTime int NOT NULL"
        +",LongestQueryTime int NULL"
        +",LongestQueryNumRows int NULL"
        +",LongestQueryParams text NULL"
        +",LongestQueryText text NULL"
        +",IncidentID int NULL"
	 */
	static final String ID = "PageID";
	static final String BEGIN_TIME = "BeginTime";
	static final String END_TIME = "EndTime";
	static final String SERVER_NAME = "ServerName";
	static final String REQUEST_URI = "RequestURI";
	static final String URL_QUERY_STRING = "UrlQueryString";
	static final String REMOTE_IP = "RemoteIP";
	static final String TOTAL_TIME_MS = "TotalTimeMs";
	static final String TIME_TO_FIRST_BYTE_MS = "TimeToFirstByteMs";
	static final String NUM_QUERIES = "NumQueries";
	static final String TOTAL_QUERY_TIME = "TotalQueryTime";
	static final String LONGEST_QUERY_TIME = "LongestQueryTime";
	static final String LONGEST_QUERY_NUM_ROWS = "LongestQueryNumRows";
	static final String LONGEST_QUERY_PARAMS = "LongestQueryParams";
	static final String LONGEST_QUERY_TEXT = "LongestQueryText";
	static final String INCIDENT_ID = "IncidentID";

	private static final List<DbLoggerColumn> COLS = new LinkedList<DbLoggerColumn>();
	static {
		COLS.add(new DbLoggerColumn(ID, VARCHAR));
		COLS.add(new DbLoggerColumn(SERVER_NAME, VARCHAR));
		COLS.add(new DbLoggerColumn(BEGIN_TIME, DATETIME));
		COLS.add(new DbLoggerColumn(END_TIME, DATETIME));
		COLS.add(new DbLoggerColumn(REQUEST_URI, VARCHAR));
		COLS.add(new DbLoggerColumn(URL_QUERY_STRING, VARCHAR));
		COLS.add(new DbLoggerColumn(REMOTE_IP, VARCHAR));
		COLS.add(new DbLoggerColumn(TOTAL_TIME_MS, LONG));
		COLS.add(new DbLoggerColumn(TIME_TO_FIRST_BYTE_MS, LONG));
		COLS.add(new DbLoggerColumn(NUM_QUERIES, INT));
		COLS.add(new DbLoggerColumn(TOTAL_QUERY_TIME, LONG));
		COLS.add(new DbLoggerColumn(LONGEST_QUERY_TIME, LONG));
		COLS.add(new DbLoggerColumn(LONGEST_QUERY_NUM_ROWS, INT));
		COLS.add(new DbLoggerColumn(LONGEST_QUERY_PARAMS, VARCHAR));
		COLS.add(new DbLoggerColumn(LONGEST_QUERY_TEXT, VARCHAR));
		COLS.add(new DbLoggerColumn(INCIDENT_ID, VARCHAR));
	}

	@Override
	protected RequestInfo newInstance(ResultSet rs) throws SQLException {
		int i=1;
		RequestInfo ret = new RequestInfo(rs.getString(i++), rs.getString(i++));
		long beginTime = rs.getTimestamp(i++).getTime();
		ret.setBeginTime(beginTime);
		ret.setTimeCompleted(rs.getTimestamp(i++).getTime());
		ret.setRequestURI(rs.getString(i++));
		ret.setQueryString(rs.getString(i++));
		ret.setRemoteIP(rs.getString(i++));
		// ignore computed column (TotalTimeMs)
		rs.getLong(i++);
		ret.setOutputStartedTime(rs.getLong(i++) + beginTime);
		ret.setQueryCount(rs.getInt(i++));
		ret.setQueryTimeMs(rs.getLong(i++));
		long longestQueryTime = rs.getLong(i++);
		int longestQueryNumRows = rs.getInt(i++);
		String longestQueryParams = rs.getString(i++);
		String longestQueryText = rs.getString(i++);
		if(longestQueryText != null) {
			QueryInfo longestQueryInfo = new QueryInfo();
			longestQueryInfo.setQueryText(longestQueryText);
			longestQueryInfo.setQueryParams(longestQueryParams);
			longestQueryInfo.endTick = longestQueryTime;
			longestQueryInfo.setResultCount(longestQueryNumRows);
			ret.setLongestQueryInfo(longestQueryInfo);
		}
		ret.setIncidentID(rs.getString(i++));
		
		return ret;
	}
	
	public RequestInfoDao(DbLoggerConnectionPool pool, int cacheSize) {
		super(pool, cacheSize);
	}

	@Override
	protected List<DbLoggerColumn> getColumns() {
		return COLS;
	}

	@Override
	String getTableName() {
		return "pages";
	}
	
	/*
		COLS.add(new DbLoggerColumn(ID, VARCHAR));
		COLS.add(new DbLoggerColumn(BEGIN_TIME, DATETIME));
		COLS.add(new DbLoggerColumn(END_TIME, DATETIME));
		COLS.add(new DbLoggerColumn(SERVER_NAME, VARCHAR));
		COLS.add(new DbLoggerColumn(REQUEST_URI, VARCHAR));
		COLS.add(new DbLoggerColumn(URL_QUERY_STRING, VARCHAR));
		COLS.add(new DbLoggerColumn(REMOTE_IP, VARCHAR));
		COLS.add(new DbLoggerColumn(TOTAL_TIME_MS, LONG));
		COLS.add(new DbLoggerColumn(TIME_TO_FIRST_BYTE_MS, LONG));
		COLS.add(new DbLoggerColumn(NUM_QUERIES, INT));
		COLS.add(new DbLoggerColumn(TOTAL_QUERY_TIME, LONG));
		COLS.add(new DbLoggerColumn(LONGEST_QUERY_TIME, LONG));
		COLS.add(new DbLoggerColumn(LONGEST_QUERY_NUM_ROWS, INT));
		COLS.add(new DbLoggerColumn(LONGEST_QUERY_PARAMS, VARCHAR));
		COLS.add(new DbLoggerColumn(INCIDENT_ID, VARCHAR));
	 */
	
	@Override
	long getLongValue(RequestInfo obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name==TOTAL_TIME_MS) return obj.getElapsedTime();
		if(name==TIME_TO_FIRST_BYTE_MS) return obj.getTimeToOutput();
		if(name==TOTAL_QUERY_TIME) return obj.getQueryTime();
		QueryInfo qi = obj.getLongestQueryInfo();
		if(name==LONGEST_QUERY_TIME) return qi == null ? 0 : qi.getElapsedTime();
		throw new IllegalArgumentException("Unknown column: " + name);
	}

	@Override
	int getIntValue(RequestInfo obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name==NUM_QUERIES) return obj.getQueryCount();
		QueryInfo qi = obj.getLongestQueryInfo();
		if(name==LONGEST_QUERY_NUM_ROWS) return qi==null ? 0 : qi.getResultCount();
		throw new IllegalArgumentException("Unknown column: " + name);
	}

	@Override
	String getStringValue(RequestInfo obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name==ID) return obj.getId();
		if(name==SERVER_NAME) return obj.getServerName();
		if(name==REQUEST_URI) return obj.getRequestURI();
		if(name==URL_QUERY_STRING) return obj.getQueryString();
		if(name==REMOTE_IP) return obj.getRemoteIP();
		if(name==INCIDENT_ID) return obj.getIncidentID();
		QueryInfo qi = obj.getLongestQueryInfo();
		if(name==LONGEST_QUERY_TEXT) return qi==null ? null : qi.getQueryTextOnly();
		if(name==LONGEST_QUERY_PARAMS) return qi==null ? null : qi.getQueryParamsOnly();
		throw new IllegalArgumentException("Unknown column: " + name);
	}

	@Override
	Date getDateValue(RequestInfo obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name==BEGIN_TIME) return new Date(obj.getBeginTime());
		if(name==END_TIME) return new Date(obj.getEndTime());
		throw new IllegalArgumentException("Unknown column: " + name);
	}

}
