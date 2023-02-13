/**
 * 
 */
package com.seefusion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Daryl
 * 
 */
@SuppressWarnings({"PMD.CheckResultSet","PMD.AvoidDuplicateLiterals"})

public abstract class DbLoggerImplTest extends TestCase {

	Connection c;

	Statement s;

	DbLogger dbLogger;

	SeeFusion sf;

	DbLogger getTestDbLogger() throws SQLException {
		DbLoggerConnectionPool pool = new DbLoggerConnectionPool(getJdbcURL(), getJdbcUser(), getJdbcPassword());
		DbLogger ret = new DbLogger(sf);
		ret.setConnectionPool(pool);
		return ret;
	}

	@Before
	public void setUp() throws Exception {
		Class.forName(getJdbcClass());
		c = DriverManager.getConnection(getJdbcURL(), getJdbcUser(), getJdbcPassword());
		s = c.createStatement();

		sf = SeeFusion.getInstance();
		dbLogger = sf.dbLogger;
		Thread.sleep(100);
	}

	@After
	public void tearDown() throws Exception {
		if(dbLogger != null) {
			dbLogger.shutdown();
		}
		if(s != null) {
			s.close();
		}
		if(c != null) {
			c.close();
		}
	}

	/**
	 * @return Returns the jdbcClass.
	 */
	abstract String getJdbcClass();

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
	 * Test of doCreateQueriesTable method, of class
	 * com.seefusion.DbLoggerSqlServer.
	 * @throws SQLException 
	 */
	@Test
	public void testCreateTables() throws SQLException {
		for(SeeDAO<? extends DaoObject> dao : dbLogger.getDaoMap().values()) {
			dao.createTable();
		}
	}

    void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// ignore
		}
	}
	
}
