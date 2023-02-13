package com.seefusion;

import static com.seefusion.DbLoggerColumn.ColumnType.CLOB;
import static com.seefusion.DbLoggerColumn.ColumnType.DATETIME;
import static com.seefusion.DbLoggerColumn.ColumnType.INT;
import static com.seefusion.DbLoggerColumn.ColumnType.LONG;
import static com.seefusion.DbLoggerColumn.ColumnType.VARCHAR;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class QueryInfoDao extends SeeDAO<QueryInfo> {

	QueryInfoDao(DbLoggerConnectionPool pool, int maxInMemoryCount) {
		super(pool, maxInMemoryCount);
	}

	static final String ID = "QueryId";
	static final String INSTANCE_NAME = "InstanceName";
	static final String BEGIN_TIME = "BeginTime";
	static final String END_TIME = "EndTime";
	static final String ELAPSED_TIME = "ElapsedTimeMs";
	static final String RESULT_COUNT = "ResultCount";
	static final String QUERY_TEXT = "QueryText";
	static final String QUERY_PARAMS = "QueryParams";
	static final String STACK = "StackTrace";

	@Override
	protected List<DbLoggerColumn> getColumns() {
		List<DbLoggerColumn> ret = new LinkedList<DbLoggerColumn>();
		ret.add(new DbLoggerColumn(ID, VARCHAR));
		ret.add(new DbLoggerColumn(INSTANCE_NAME, VARCHAR));
		ret.add(new DbLoggerColumn(BEGIN_TIME, DATETIME));
		ret.add(new DbLoggerColumn(END_TIME, DATETIME));
		ret.add(new DbLoggerColumn(ELAPSED_TIME, LONG));
		ret.add(new DbLoggerColumn(RESULT_COUNT, INT));
		ret.add(new DbLoggerColumn(QUERY_TEXT, CLOB));
		ret.add(new DbLoggerColumn(QUERY_PARAMS, CLOB));
		ret.add(new DbLoggerColumn(STACK, CLOB));
		return ret;
	}

	void doInsertQuery(PreparedStatement ps, QueryInfo qi, String instanceName) throws SQLException {
		int i = 1;
		ps.setString(i++, qi.id);
		ps.setString(i++, instanceName);
		ps.setTimestamp(i++, new java.sql.Timestamp(qi.getBeginTime()));
		ps.setTimestamp(i++, new java.sql.Timestamp(qi.getEndTime()));
		ps.setLong(i++, qi.getElapsedTime());
		ps.setInt(i++, qi.getResultCount());
		ps.setString(i++, qi.getQueryTextOnly());
		ps.setString(i++, qi.getQueryParamsOnly());
		ps.setString(i++, qi.getStack());
		ps.executeUpdate();
	}
	
	@Override
	String getTableName() {
		return "queries";
	}

	@Override
	protected QueryInfo newInstance(ResultSet rs) throws SQLException {
		int i = 1;
		String id = rs.getString(i++);
		QueryInfo ret = new QueryInfo(id);
		ret.setInstanceName(rs.getString(i++));
		ret.setStartTick(rs.getTimestamp(i++).getTime());
		ret.setEndTick(rs.getTimestamp(i++).getTime());
		// elapsed time is calculated, skip this col
		rs.getLong(i++);
		ret.setResultCount(rs.getInt(i++));
		ret.setQueryText(rs.getString(i++));
		ret.setQueryParams(rs.getString(i++));
		ret.setStack(rs.getString(i++));
		return ret;
	}

	/*
		ret.add(new DbLoggerColumn(ID, VARCHAR));
		ret.add(new DbLoggerColumn(INSTANCE_NAME, VARCHAR));
		ret.add(new DbLoggerColumn(BEGIN_TIME, DATETIME));
		ret.add(new DbLoggerColumn(END_TIME, DATETIME));
		ret.add(new DbLoggerColumn(ELAPSED_TIME, LONG));
		ret.add(new DbLoggerColumn(RESULT_COUNT, INT));
		ret.add(new DbLoggerColumn(QUERY_TEXT, CLOB));
		ret.add(new DbLoggerColumn(QUERY_PARAMS, CLOB));
		ret.add(new DbLoggerColumn(STACK, CLOB));(non-Javadoc)
	 */
	@Override
	long getLongValue(QueryInfo obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name==ELAPSED_TIME) return obj.getElapsedTime();
		throw new IllegalArgumentException("Unknown column: " + name);
	}

	@Override
	int getIntValue(QueryInfo obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name==RESULT_COUNT) return obj.getResultCount();
		throw new IllegalArgumentException("Unknown column: " + name);
	}

	@Override
	String getStringValue(QueryInfo obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name == ID) return obj.id;
		if(name == INSTANCE_NAME) return obj.getInstanceName();
		if(name == QUERY_TEXT) return obj.getQueryTextOnly();
		if(name == QUERY_PARAMS) return obj.getQueryParamsOnly();
		if(name == STACK) return obj.getStack();
		throw new IllegalArgumentException("Unknown column: " + name);
	}

	@Override
	Date getDateValue(QueryInfo obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name == BEGIN_TIME) return new Date(obj.getBeginTime());
		if(name == END_TIME) return new Date(obj.getEndTime());
		throw new IllegalArgumentException("Unknown column: " + name);
	}
	
}
