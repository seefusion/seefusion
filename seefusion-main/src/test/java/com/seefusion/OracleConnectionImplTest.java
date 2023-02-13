/**
 * 
 */
package com.seefusion;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Daryl
 *
 */
@SuppressWarnings("PMD.CheckResultSet")
public class OracleConnectionImplTest extends ConnectionImplTest {
    
	@Override
	String getJdbcURL() {
		return "jdbc:seefusion:{jdbc:oracle:thin:@" + Util.getEnv("ORACLE", "localhost") + ":1521:XE};driver=oracle.jdbc.driver.OracleDriver;dialect=oracle";
	}

	@Override
	String getJdbcUser() {
		return "seefusion";
	}
	
	@Override
	String getJdbcPassword() {
		return "Seefus1on";
	}	
	
	@Override
	String getSql() {
		return "SELECT 1 FROM DUAL";
	}
	
	@Override
	String getExpectedProperty() {
		return "DB";
	}
	
	@Override
	String getExpectedValue() {
		return "XE";
	}

	@Override
	void testRunStatement() throws Exception {
		Connection c = DriverManager.getConnection(getJdbcURL());
		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("Select 1 from dual");
			assertTrue(rs.next());
		}
		finally {
			c.close();
		}
	}
}
