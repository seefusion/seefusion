package com.seefusion;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

public class OracleStatementImplTest {

	@Test
	public void enumerateConstructors() throws Exception {
		Constructor<?>[] constructors = OracleConnectionImpl.class.getConstructors();
		System.out.println(constructors.length + " constructors found");
		for(Constructor<?> constructor : constructors) {
			System.out.println(constructor);
		}
	}
	
	@Test
	public void testStatement1() throws Exception {
		// requestinfo adds itself to its internal  ThreadLocal so StatementImpl can find it
		new RequestInfo(SeeFusion.getInstance(), "serverName", "requestURI", "queryString", "remoteIP", "method", "pathInfo", false);
		Class.forName("com.seefusion.Driver");
		Connection c = getConnection();
		assertEquals("OracleConnectionImpl", c.getClass().getSimpleName());
		Statement s = c.createStatement();
		assertEquals("OracleStatementImpl", s.getClass().getSimpleName());
		try {
			s.executeUpdate("create table test(id int)");
			ResultSet rs = s.executeQuery("select 1 from test");
			rs.close();
			assertEquals("OracleResultSetImpl", rs.getClass().getSimpleName());
		}
		finally {
			s.executeUpdate("drop table test");			
		}
		s.close();
		c.close();
	}
	
	@Test
	public void testStatement2() throws Exception {
		// requestinfo adds itself to its internal ThreadLocal so StatementImpl can find it
		new RequestInfo(SeeFusion.getInstance(), "serverName", "requestURI", "queryString", "remoteIP", "method", "pathInfo", false);
		Class.forName("com.seefusion.Driver");
		Connection c = getConnection();
		assertEquals("OracleConnectionImpl", c.getClass().getSimpleName());
		Statement s = c.createStatement();
		assertEquals("OracleStatementImpl", s.getClass().getSimpleName());
		try {
			s.executeUpdate("create table test2(id int)");
			ResultSet rs = s.executeQuery("select 1 from test2");
			assertEquals("OracleResultSetImpl", rs.getClass().getSimpleName());
			rs.close();
		}
		finally {
			s.executeUpdate("drop table test2");			
		}
		s.close();
		c.close();
	}
	
	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:seefusion:{jdbc:oracle:thin:@" + Util.getEnv("ORACLE", "localhost") + ":1521/XE};driver=oracle.jdbc.OracleDriver", "seefusion", "Seefus1on");
	}
	
}
