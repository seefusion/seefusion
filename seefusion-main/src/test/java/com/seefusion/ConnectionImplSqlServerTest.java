/**
 * 
 */
package com.seefusion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import java.sql.ResultSet;
import static org.junit.Assert.*;


/**
 * @author Daryl
 *
 */
@SuppressWarnings("PMD.CheckResultSet")
public class ConnectionImplSqlServerTest extends ConnectionImplTest {

	@Override
	String getJdbcURL() {
		return "jdbc:seefusion:{jdbc:jtds:sqlserver://mssql/seefusion;prepareSQL=2};driver=net.sourceforge.jtds.jdbc.Driver;dialect=sqlserver";
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
	String getExpectedProperty() {
		return "DB";
	}
	
	@Override
	String getExpectedValue() {
		return "seefusion";
	}
	
	@Override
	String getSql() {
		return "SELECT 1";
	}

	@Override
	void testRunStatement() throws Exception {
		Connection c = DriverManager.getConnection(getJdbcURL(), getJdbcUser(), getJdbcPassword());
		assertTrue(c instanceof ConnectionImpl);
		try {
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("Select 1");
			assertTrue(rs.next());
		}
		finally {
			c.close();
		}
	}
	
}
