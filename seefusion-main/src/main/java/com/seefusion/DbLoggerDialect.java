package com.seefusion;

import java.sql.Connection;

interface DbLoggerDialect {

	String getDatatype(DbLoggerColumn.ColumnType type);

	String getName();

	boolean validate(Connection c);

	int getVarcharMaxLength();

	int getClobMaxLength();

	String limit(int i);

	String top(int i);
	
	String rownumLte(int i);

}
