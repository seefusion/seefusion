package com.seefusion;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatement7 extends PreparedStatementImpl implements PreparedStatement {

	PreparedStatement7(ConnectionImpl c, PreparedStatement ps, String sql, JdbcConfig jdbcConfig, boolean isTracingAPI)
			throws SQLException {
		super(c, ps, sql, jdbcConfig, isTracingAPI);
	}
	
	@Override
	public void closeOnCompletion() throws SQLException {
		ps.closeOnCompletion();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return ps.isCloseOnCompletion();
	}

}
