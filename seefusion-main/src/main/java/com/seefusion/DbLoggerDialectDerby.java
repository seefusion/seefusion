package com.seefusion;

import java.sql.Connection;

import com.seefusion.DbLoggerColumn.ColumnType;

public class DbLoggerDialectDerby implements DbLoggerDialect {

	@Override
	public String getDatatype(ColumnType type) {
		switch (type) {
		case CLOB:
			return "LONG VARCHAR";
		case DATETIME:
			return "TIMESTAMP";
		case INT:
			return "INTEGER";
		case LONG:
			return "BIGINT";
		case VARCHAR:
			return "VARCHAR(255)";
		case FLOAT:
			return "FLOAT";
		default:
			break;
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String getName() {
		return "Apache Derby";
	}

	@Override
	public boolean validate(Connection c) {
		return true;
	}

	@Override
	public int getVarcharMaxLength() {
		return 255;
	}

	@Override
	public int getClobMaxLength() {
		return 32700;
	}

	@Override
	public String limit(int i) {
		return "";
	}

	@Override
	public String top(int i) {
		return "";
	}

	@Override
	public String rownumLte(int i) {
		return "";
	}

}
