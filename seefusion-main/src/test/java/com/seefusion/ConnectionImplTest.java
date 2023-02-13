/**
 * 
 */
package com.seefusion;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Daryl
 * 
 */
@SuppressWarnings("PMD.CheckResultSet")
public abstract class ConnectionImplTest extends TestCase {
	Connection c;

	Statement s;

	DbLogger dbLogger;

	SeeFusion sf;

	DbLogger getTestDbLogger() throws SQLException {
		DbLogger ret = new DbLogger(sf);
		ret.setConnectionPool(new DbLoggerConnectionPool(getJdbcURL(), getJdbcUser(), getJdbcPassword()));
		return ret;
	}
	
	@Before
	public void setUp() throws Exception {
		Class.forName("com.seefusion.Driver");
		c = DriverManager.getConnection(getJdbcURL(), getJdbcUser(), getJdbcPassword());
		s = c.createStatement();

		sf = SeeFusion.getInstance("./WEB-INF/");
	}

	@After
	public void tearDown() throws Exception {
		if(s != null) {
			s.close();
		}
		if(c != null) {
			c.close();
		}
	}
	
	@Test
	public void testConnection() throws Exception {
		ResultSet rs = s.executeQuery(getSql());
		rs.next();
		ConnectionImpl ci = (ConnectionImpl)c;
		String expectedValue = getExpectedValue();
		// see SqlDialectMetadata.getMetadata()
		String prop = ci.connectionMetadata.getProperty(getExpectedProperty());
		assertEquals("Property", expectedValue, prop);
		rs.close();
	}
	
	/**
	 * @return Returns the jdbcURL.
	 */
	abstract String getJdbcURL();

	/**
	 * @return Returns the jdbcUser.
	 */
	abstract String getJdbcUser();

	/**
	 * @return Returns the jdbcPassword.
	 */
	abstract String getJdbcPassword();

	/**
	 * @return Returns the sql to test.
	 */
	abstract String getSql();
	
	/**
	 * @return Returns the expectedValue.
	 */
	abstract String getExpectedValue();

	/**
	 * @return Returns the prop.
	 */
	abstract String getExpectedProperty();

	abstract void testRunStatement() throws Exception;

}
