package com.seefusion;

public class DbLoggerDialectFactory {

	public static DbLoggerDialect getDialectFor(String url) {
		if(url.contains("mysql")) {
			return new DbLoggerDialectMySql();
		}
		if(url.contains("oracle")) {
			return new DbLoggerDialectOracle();
		}
		if(url.contains("derby")) {
			return new DbLoggerDialectDerby();
		}
		if(url.contains("sqlserver") || url.contains("jtds")) {
			return new DbLoggerDialectSqlServer();
		}
		return null;
	}

}
