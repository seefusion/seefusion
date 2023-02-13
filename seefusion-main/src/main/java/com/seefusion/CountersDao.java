package com.seefusion;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.seefusion.DbLoggerColumn.ColumnType;

public class CountersDao extends SeeDAO<Counters> {

	private static final String ACTIVE_REQUESTS = "ActiveRequests";
	private static final String UPTIME_MS = "UptimeMs";
	private static final String MEMORY_SIZE = "MemorySize";
	private static final String MEMORY_USED = "MemoryUsed";
	private static final String TOTAL_QUERY_TIME_MS = "TotalQueryTimeMs";
	private static final String NUM_QUERIES = "NumQueries";
	private static final String TOTAL_PAGE_TIME_MS = "TotalPageTimeMs";
	private static final String NUM_PAGES = "NumPages";
	private static final String DURATION_SECONDS = "DurationSeconds";
	private static final String FILTER_NAME = "FilterName";
	private static final String COUNTER_TIME = "CounterTime";
	private static final String ID = "id";
	static final List<DbLoggerColumn> COLS = new LinkedList<DbLoggerColumn>();
	
	static {
		COLS.add(new DbLoggerColumn(ID, ColumnType.VARCHAR));
		COLS.add(new DbLoggerColumn(COUNTER_TIME, ColumnType.DATETIME));
		COLS.add(new DbLoggerColumn(FILTER_NAME, ColumnType.VARCHAR));
		COLS.add(new DbLoggerColumn(DURATION_SECONDS, ColumnType.INT));
		COLS.add(new DbLoggerColumn(NUM_PAGES, ColumnType.INT));
		COLS.add(new DbLoggerColumn(TOTAL_PAGE_TIME_MS, ColumnType.LONG));
		COLS.add(new DbLoggerColumn(NUM_QUERIES, ColumnType.INT));
		COLS.add(new DbLoggerColumn(TOTAL_QUERY_TIME_MS, ColumnType.LONG));
		COLS.add(new DbLoggerColumn(MEMORY_USED, ColumnType.LONG));
		COLS.add(new DbLoggerColumn(MEMORY_SIZE, ColumnType.LONG));
		COLS.add(new DbLoggerColumn(UPTIME_MS, ColumnType.LONG));
		COLS.add(new DbLoggerColumn(ACTIVE_REQUESTS, ColumnType.INT));
	}
	
	CountersDao(DbLoggerConnectionPool pool) {
		super(pool, 100);
	}

	@Override
	protected List<DbLoggerColumn> getColumns() {
		return COLS;
	}

	@Override
	String getTableName() {
		return "counters";
	}

	@Override
	protected Counters newInstance(ResultSet rs) throws SQLException {
		int i = 1;
		String id = rs.getString(i++);
		Date timestamp = rs.getDate(i++);
		String filterName = rs.getString(i++);
		int durationSeconds = rs.getInt(i++);
		int numPages = rs.getInt(i++);
		long pageTime = rs.getLong(i++);
		int numQueries = rs.getInt(i++);
		long queryTime = rs.getLong(i++);
		long memoryUsed = rs.getLong(i++);
		long memorySize = rs.getLong(i++);
		long uptimeMs = rs.getLong(i++);
		int activeRequests = rs.getInt(i++);
		
		return new Counters(id, timestamp, filterName, durationSeconds, numPages, pageTime, numQueries, queryTime, memoryUsed, memorySize, uptimeMs, activeRequests);
	}

	@Override
	long getLongValue(Counters obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name == TOTAL_PAGE_TIME_MS) return obj.getPageTime();
		if(name == TOTAL_QUERY_TIME_MS) return obj.getQueryTime();
		if(name == MEMORY_USED) return obj.getMaxMemory() - obj.getFreeMemory();
		if(name == MEMORY_SIZE) return obj.getMaxMemory();
		if(name == UPTIME_MS) return obj.getUptimeMs();
		throw new IllegalArgumentException("Unknown column: " + name);
	}

	@Override
	int getIntValue(Counters obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name == NUM_PAGES) return obj.getPageCount();
		if(name == NUM_QUERIES) return obj.getQueryCount();
		if(name == DURATION_SECONDS) return obj.getCounterDuration();
		if(name == ACTIVE_REQUESTS) return obj.getActiveRequests();
		throw new IllegalArgumentException("Unknown column: " + name);
	}

	@Override
	String getStringValue(Counters obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name == ID) return obj.getId();
		if(name == FILTER_NAME) return obj.getInstanceName();
		throw new IllegalArgumentException("Unknown column: " + name);
	}

	@Override
	Date getDateValue(Counters obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name == COUNTER_TIME) return new Date(obj.getCounterTimestamp());
		throw new IllegalArgumentException("Unknown column: " + name);
	}

}
