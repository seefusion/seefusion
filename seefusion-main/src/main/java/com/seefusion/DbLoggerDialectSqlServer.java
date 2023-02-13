package com.seefusion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.seefusion.DbLoggerColumn.ColumnType;

public class DbLoggerDialectSqlServer implements DbLoggerDialect {

	private static final Logger LOG = Logger.getLogger(DbLoggerDialectSqlServer.class.getName());
	
	@Override
	public String getDatatype(ColumnType type) {
		switch (type) {
		case CLOB:
			return "NVARCHAR(MAX)";
		case DATETIME:
			return "DATETIME";
		case INT:
			return "INTEGER";
		case LONG:
			return "BIGINT";
		case VARCHAR:
			return "NVARCHAR(255)";
		case FLOAT:
			return "FLOAT";
		default:
			break;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String getName() {
		return "SqlServer";
	}

	@Override
	public boolean validate(Connection c) {
		Statement s = null;
		ResultSet rs = null;
		try {
			s = c.createStatement();
			rs = s.executeQuery("select 1");
			if(rs.next()) {
				return true;
			}
		}
		catch (SQLException e) {
			LOG.warning("Connection validation failed: " + e.getMessage());
		}
		finally {
			if(rs != null)
				try {
					rs.close();
				}
				catch (SQLException e) {
				}
			if(s != null)
				try {
					s.close();
				}
				catch (SQLException e) {
				}
		}
		try {
			c.close();
		}
		catch (SQLException e) {
		}
		return false;
	}
	
	@Override
	public int getVarcharMaxLength() {
		return 255;
	}

	@Override
	public int getClobMaxLength() {
		return Integer.MAX_VALUE;
	}

	@Override
	public String limit(int i) {
		return "";
	}

	@Override
	public String top(int i) {
		return "TOP " + Integer.toString(i);
	}

	@Override
	public String rownumLte(int i) {
		return "";
	}

}
