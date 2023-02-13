package com.seefusion;

import static com.seefusion.DbLoggerColumn.ColumnType.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

class ProfileDao extends SeeDAO<Profile> {

	private static final String ID = "ProfileId";
	private static final String INSTANCE_NAME = "InstanceName";
	private static final String NAME = "Name";
	private static final String BEGIN_TIME = "BeginTime";
	private static final String SCHEDULED_DURATION_MS = "ScheduledDurationMs";
	private static final String ACTUAL_DURATION_MS = "ActualDurationMs";
	private static final String INTERVAL_MS = "IntervalMs";
	private static final String STACK_COUNT = "StackCount";
	private static final String THREAD_STACKS = "ThreadStacks";
	
	private static final String TABLE_NAME = "profiles";

	private static final List<DbLoggerColumn> COLS = new LinkedList<DbLoggerColumn>();
	static {
		COLS.add(new DbLoggerColumn(ID, VARCHAR));
		COLS.add(new DbLoggerColumn(INSTANCE_NAME, VARCHAR));
		COLS.add(new DbLoggerColumn(NAME, VARCHAR));
		COLS.add(new DbLoggerColumn(BEGIN_TIME, DATETIME));
		COLS.add(new DbLoggerColumn(SCHEDULED_DURATION_MS, LONG));
		COLS.add(new DbLoggerColumn(ACTUAL_DURATION_MS, LONG));
		COLS.add(new DbLoggerColumn(INTERVAL_MS, LONG));
		COLS.add(new DbLoggerColumn(STACK_COUNT, INT));
		COLS.add(new DbLoggerColumn(THREAD_STACKS, CLOB));
	}

	@Override
	protected Profile newInstance(ResultSet rs) throws SQLException {
		int i = 1;
		String id = rs.getString(i++);
		String instanceName = rs.getString(i++);
		String name = rs.getString(i++);
		long startTick = rs.getTimestamp(i++).getTime();
		long scheduledDurationMs = rs.getLong(i++);
		long actualDurationMs = rs.getLong(i++);
		long intervalMs = rs.getLong(i++);
		int stackCount = rs.getInt(i++);
		String threadStacks = rs.getString(i++);
		return new Profile(id, instanceName, name, startTick, scheduledDurationMs, intervalMs, actualDurationMs, stackCount, threadStacks);
	}

	ProfileDao(DbLoggerConnectionPool pool) {
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
	protected String getOrderBy() {
		return "beginTime desc";
	}

	@Override
	long getLongValue(Profile obj, DbLoggerColumn col) {
		String name = col.getName();
		if (name == ACTUAL_DURATION_MS) return obj.getActualDurationMs();
		if (name == INTERVAL_MS) return obj.getIntervalMs();
		if (name == SCHEDULED_DURATION_MS) return obj.getScheduledDurationMs();
		throw new IllegalArgumentException("Unknown long column: " + name);
	}

	@Override
	int getIntValue(Profile obj, DbLoggerColumn col) {
		String name = col.getName();
		if(name == STACK_COUNT)
			return obj.getStackCount();
		throw new IllegalArgumentException("Unknown int column: " + name);
	}

	@Override
	String getStringValue(Profile obj, DbLoggerColumn col) {
		String name = col.getName();
		if (name == ID) return obj.getId();
		if (name == INSTANCE_NAME) return obj.getInstanceName();
		if (name == NAME) return obj.getName();
		if (name == THREAD_STACKS) return obj.getThreadStacksJson();
		throw new IllegalArgumentException("Unknown string column: " + name);
	}

	@Override
	Date getDateValue(Profile obj, DbLoggerColumn col) {
		String name = col.getName();
		if (name == BEGIN_TIME) return new Date(obj.getStartTick());
		throw new IllegalArgumentException("Unknown date column: " + name);
	}

}