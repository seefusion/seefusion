package com.seefusion;

import static com.seefusion.DbLoggerColumn.ColumnType.CLOB;
import static com.seefusion.DbLoggerColumn.ColumnType.DATETIME;
import static com.seefusion.DbLoggerColumn.ColumnType.FLOAT;
import static com.seefusion.DbLoggerColumn.ColumnType.LONG;
import static com.seefusion.DbLoggerColumn.ColumnType.VARCHAR;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class IncidentServerDao extends SeeDAO<IncidentServer> {

//	String incidentServerID = UUID.randomUUID().toString();
//	String incidentID;
//	String filterName;
//	Timestamp logTime;
//	int activeRequests;
//	long uptimeMs;
//	float memoryPct;
//	float pps10;
//	float qps10;
//	float pps60;
//	float qps60;
	private static final String ID = "IncidentServerID";
	private static final String INCIDENT_ID = "IncidentID";
	private static final String FILTER_NAME = "FilterName";
	private static final String LOG_TIME = "LogTime";
	private static final String ACTIVE_REQUESTS = "ActiveRequests";
	private static final String UPTIME_MS = "UptimeMs";
	private static final String MEMORY_PCT = "MemoryPct"; 
	private static final String PPS10 = "PagesPerSec10";
	private static final String QPS10 = "QueriesPerSec10";
	private static final String PPS60 = "PagesPerSec60";
	private static final String QPS60 = "QueriesPerSec60";
	
	private static final String TABLE_NAME = "incidentservers";
	
	private static final List<DbLoggerColumn> COLS = new LinkedList<DbLoggerColumn>();
	static {
		COLS.add(new DbLoggerColumn(ID, VARCHAR));
		COLS.add(new DbLoggerColumn(INCIDENT_ID, VARCHAR));
		COLS.add(new DbLoggerColumn(FILTER_NAME, VARCHAR));
		COLS.add(new DbLoggerColumn(LOG_TIME, DATETIME));
		COLS.add(new DbLoggerColumn(ACTIVE_REQUESTS, CLOB));
		COLS.add(new DbLoggerColumn(UPTIME_MS, LONG));
		COLS.add(new DbLoggerColumn(MEMORY_PCT, FLOAT));
		COLS.add(new DbLoggerColumn(PPS10, FLOAT));
		COLS.add(new DbLoggerColumn(QPS10, FLOAT));
		COLS.add(new DbLoggerColumn(PPS60, FLOAT));
		COLS.add(new DbLoggerColumn(QPS60, FLOAT));
	}
	
	IncidentServerDao(DbLoggerConnectionPool pool) {
		super(pool, 100);
	}

	@Override
	protected List<DbLoggerColumn> getColumns() {
		return COLS;
	}

	@Override
	String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected IncidentServer newInstance(ResultSet rs) throws SQLException {
		int i = 1;
		IncidentServer inc = new IncidentServer();
		inc.incidentServerID = rs.getString(i++);
		inc.incidentID = rs.getString(i++);
		inc.filterName = rs.getString(i++);
		return inc;
	}

	@Override
	long getLongValue(IncidentServer obj, DbLoggerColumn col) {
		throw new IllegalArgumentException("No column " + col.getName());
	}

	@Override
	int getIntValue(IncidentServer obj, DbLoggerColumn col) {
		throw new IllegalArgumentException("No column " + col.getName());
	}

	@Override
	String getStringValue(IncidentServer obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name == ID) {
			return obj.getIncidentServerID();
		}
		if(name == INCIDENT_ID) {
			return obj.getIncidentID();
		}
		if(name == FILTER_NAME) {
			return obj.getFilterName();
		}
		throw new IllegalArgumentException("No column " + col.getName());
	}

	@Override
	Date getDateValue(IncidentServer obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name == LOG_TIME) {
			return new Date(obj.logTime);
		}
		throw new IllegalArgumentException("No column " + col.getName());
	}
	



}
