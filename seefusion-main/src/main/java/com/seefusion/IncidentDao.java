package com.seefusion;

import static com.seefusion.DbLoggerColumn.ColumnType.DATETIME;
import static com.seefusion.DbLoggerColumn.ColumnType.VARCHAR;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class IncidentDao extends SeeDAO<Incident> {

//    static String incidentsDDL = "CREATE TABLE Incidents ("
//            +"IncidentID int NOT NULL"
//            +",BeginTime datetime NOT NULL"
//            +",EndTime datetime NULL"
//            +",FilterName varchar (50) NOT NULL"
//            +",IncidentType varchar (50) NULL"
//            +",ThresholdType varchar (50) NULL"
//            +",ThresholdValue varchar (50) NULL"
//            +",ActionTaken text NULL"
//            +")";
	private static final String ID = "IncidentID";
	private static final String BEGIN_TIME = "BeginTime";
	private static final String END_TIME = "EndTime";
	private static final String FILTER_NAME = "FilterName";
	private static final String INCIDENT_TYPE = "IncidentType";
	private static final String THRESHOLD_TYPE = "ThresholdType";
	private static final String THRESHOLD_VALUE = "ThresholdValue";
	private static final String ACTION_TAKEN = "ActionTaken";
	
	private static final String TABLE_NAME = "incidents";
	
	private static final List<DbLoggerColumn> COLS = new LinkedList<DbLoggerColumn>();
	static {
		COLS.add(new DbLoggerColumn(ID, VARCHAR));
		COLS.add(new DbLoggerColumn(FILTER_NAME, VARCHAR));
		COLS.add(new DbLoggerColumn(BEGIN_TIME, DATETIME));
		COLS.add(new DbLoggerColumn(END_TIME, DATETIME));
		COLS.add(new DbLoggerColumn(INCIDENT_TYPE, VARCHAR));
		COLS.add(new DbLoggerColumn(THRESHOLD_TYPE, VARCHAR));
		COLS.add(new DbLoggerColumn(THRESHOLD_VALUE, VARCHAR));
		COLS.add(new DbLoggerColumn(ACTION_TAKEN, VARCHAR));
	}
	
	IncidentDao(DbLoggerConnectionPool pool) {
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
	protected Incident newInstance(ResultSet rs) throws SQLException {
		int i = 1;
		Incident inc = new Incident();
		inc.incidentID = rs.getString(i++);
		inc.filterName = rs.getString(i++);
		inc.beginTime = rs.getTimestamp(i++).getTime();
		inc.endTime = rs.getTimestamp(i++).getTime();
		inc.incidentType = rs.getString(i++);
		inc.thresholdType = rs.getString(i++);
		inc.thresholdValue = rs.getString(i++);
		inc.actionTaken = rs.getString(i++);
		return inc;
	}

	@Override
	long getLongValue(Incident obj, DbLoggerColumn col) {
		throw new IllegalArgumentException("No column " + col.getName());
	}

	@Override
	int getIntValue(Incident obj, DbLoggerColumn col) {
		throw new IllegalArgumentException("No column " + col.getName());
	}

	@Override
	String getStringValue(Incident obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name == ID) return obj.getIncidentID();
		if(name == FILTER_NAME) return obj.getFilterName();
		if(name == INCIDENT_TYPE) return obj.getIncidentType();
		if(name == THRESHOLD_TYPE) return obj.getThresholdType();
		if(name == THRESHOLD_VALUE) return obj.getThresholdValue();
		if(name == ACTION_TAKEN) return obj.getActionTaken();
		throw new IllegalArgumentException("No column " + col.getName());
	}

	@Override
	Date getDateValue(Incident obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name == BEGIN_TIME) return new Date(obj.getBeginTime());
		if(name == END_TIME) return new Date(obj.getEndTime());
		throw new IllegalArgumentException("No column " + col.getName());
	}

}
