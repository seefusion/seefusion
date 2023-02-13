package com.seefusion;

class DbLoggerColumn {

	String name;
	ColumnType type;
	
	DbLoggerColumn(String name, ColumnType type) {
		this.name = name;
		this.type = type;
	}

	static enum ColumnType {
		VARCHAR,CLOB,INT,LONG,DATETIME,FLOAT;
	}

	String getName() {
		return name;
	}
	
	ColumnType getType() {
		return type;
	}

}
