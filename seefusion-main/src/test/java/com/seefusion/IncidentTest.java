/**
 * 
 */
package com.seefusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Daryl
 * 
 */
public class IncidentTest extends TestCase {

	String jdbcClass = "org.apache.derby.jdbc.EmbeddedDriver";

	String jdbcURL = "jdbc:derby:memory:test;create=true";

	String jdbcUser = null;

	String jdbcPassword = null;

	DbLogger dbLogger;
	
	SeeFusion sf;
	
	DbLoggerConnectionPool pool;

	@Before
	public void setUp() throws Exception {
		Class.forName(this.jdbcClass);

		sf = SeeFusion.getInstance();
		pool = new DbLoggerConnectionPool(jdbcURL, jdbcUser, jdbcPassword);
		dbLogger = new DbLogger(sf);
		dbLogger.setConnectionPool(pool);
		Thread.sleep(100);
	}

	@After
	public void tearDown() throws Exception {
		dbLogger.shutdown();
	}

	@Test
	public void testSaveLoad() throws Exception {
		Incident inc = new Incident();
		inc.setBeginTime(new Date().getTime());
		inc.setIncidentType("test");
		inc.setThresholdType("testthreshold");
		inc.setThresholdValue("exceeded!");
		inc.setActionTaken("tested.");
		dbLogger.log(inc);
		
		String id = inc.getId();
		
		IncidentDao dao = (IncidentDao) dbLogger.getDao(Incident.class);
		// give it a moment to actually do the insert...
		Thread.sleep(100);
		// clear the cache to make sure we're actually working with the db
		dao.clearCache();
		Incident inc2 = dao.getById(id);
		assertNotNull(inc2);
		
		// some small loss of precision may occur during timestamp save/load
		assertTrue(Math.abs(inc.getBeginTime() - inc2.getBeginTime()) < 10);
		assertEquals(inc.getThresholdType(), inc2.getThresholdType());
		assertEquals(inc.getThresholdValue(), inc2.getThresholdValue());
		assertEquals(inc.getActionTaken(), inc2.getActionTaken());
		assertTrue(inc2.getEndTime() == 0);
		
		inc2.setEndTime(new Date().getTime());
		dbLogger.log(inc2);
		
		Incident inc3 = dao.getById(id);
		assertTrue(Math.abs(inc2.getBeginTime() - inc3.getBeginTime()) < 10);
	}
	
}
