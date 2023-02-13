/**
 * 
 */
package com.seefusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Daryl
 * 
 */
public class IncidentNoDBTest extends TestCase {

	private static final Logger LOG = Logger.getLogger(IncidentNoDBTest.class.getName());
	static {
		LOG.getParent().setLevel(Level.FINER);
	}
	
	DbLogger dbLogger;
	
	SeeFusion sf;
	
	@Before
	public void setUp() throws Exception {
		sf = SeeFusion.getInstance();
		dbLogger = sf.getDbLogger();
		dbLogger.setConnectionPool(null);
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
		assertEquals(1, inc.getRequests().length());
		
		String id = inc.getId();
		
		IncidentDao dao = (IncidentDao) dbLogger.getDao(Incident.class);
		Incident inc2 = dao.getById(id);
		assertNotNull(inc2);
		
		assertSame(inc2, inc);
		assertEquals(1, inc2.getRequests().length());
	}
	
}
