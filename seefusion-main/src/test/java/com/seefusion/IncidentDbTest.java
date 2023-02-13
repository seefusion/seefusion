package com.seefusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class IncidentDbTest {

	SeeFusion sf;
	DbLogger dbLogger;

	@Before
	public void setUp() throws Exception {
		sf = SeeFusion.getInstance();
		dbLogger = sf.getDbLogger();
		DbLoggerConnectionPool pool = new DbLoggerConnectionPool(getJdbcUrl(), getJdbcUser(), getJdbcPassword());
		dbLogger.setConnectionPool(pool);
	}

	@After
	public void tearDown() throws Exception {
		dbLogger.shutdown();
	}

	@Test
	public void testSaveLoad() throws Exception {
		RequestInfo pi = new RequestInfo("foo", "bar");
		sf.getMasterRequestList().createRequest(pi);
		Incident inc = new Incident();
		inc.setBeginTime(new Date().getTime());
		inc.setIncidentType("test");
		inc.setThresholdType("testthreshold");
		inc.setThresholdValue("exceeded!");
		inc.setActionTaken("tested.");
		dbLogger.log(inc);

		String id = inc.getId();

		// give it a moment to actually do the inserts...
		dbLogger.flush();

		IncidentDao dao = (IncidentDao) dbLogger.getDao(Incident.class);
		dao.clearCache();
		Incident inc2 = dao.getById(id);
		assertNotNull(inc2);

		// some small loss of precision may occur during timestamp save/load
		long delta = Math.abs(inc.getBeginTime() - inc2.getBeginTime());
		assertTrue("delta " + delta, delta < 1500);
		assertEquals(inc.getThresholdType(), inc2.getThresholdType());
		assertEquals(inc.getThresholdValue(), inc2.getThresholdValue());
		assertEquals(inc.getActionTaken(), inc2.getActionTaken());
		assertTrue(inc2.getEndTime() == 0);
		assertEquals(1, inc.getRequests().length());
	}

	abstract String getJdbcPassword();

	abstract String getJdbcUser();

	abstract String getJdbcUrl() throws ClassNotFoundException;
}
