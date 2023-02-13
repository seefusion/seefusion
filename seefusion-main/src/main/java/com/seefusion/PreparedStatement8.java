package com.seefusion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;

public class PreparedStatement8 extends PreparedStatement7 {

	PreparedStatement8(ConnectionImpl c, PreparedStatement ps, String sql, JdbcConfig jdbcConfig, boolean isTracingAPI)
			throws SQLException {
		super(c, ps, sql, jdbcConfig, isTracingAPI);
	}
	
	@Override
	public long executeLargeUpdate() throws SQLException {
		return ps.executeLargeUpdate();
	}

	@Override
	public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
		ps.setObject(parameterIndex, x, targetSqlType);
	}

	@Override
	public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
		ps.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

}
