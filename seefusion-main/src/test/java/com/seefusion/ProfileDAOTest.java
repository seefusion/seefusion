package com.seefusion;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

public class ProfileDAOTest {

	private static final Logger LOG = Logger.getLogger(ProfileDAOTest.class.getName());
	
	ProfileDao test;

	@Before
	public void before() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		DbLoggerConnectionPool pool = new DbLoggerConnectionPool("jdbc:derby:memory:test;create=true", null, null);
		test = new ProfileDao(pool);
	}

	@Test
	public void testInsert() throws Exception {
		final Profile profile = new Profile("testInstance", "testName", 100L, 1000L);
		profile.notifyStarted();
		profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		assertEquals(5, profile.getSnapshots().size());
		profile.notifyStopped();
		LOG.fine("Insert DDL: " + test.getInsertStatement());
		test.insert(profile);
		JSONArray ret = test.list();
		assertEquals(1, ret.length());
		assertEquals("testName", ((JSONObject)ret.get(0)).get("name"));
	}

	@Test
	public void testGetById() {
	}

	@Test
	public void testEnsureTableExists() throws Exception {
		test.dropTable();
		test.executeUpdate(
				"CREATE TABLE profiles(ProfileId VARCHAR(255),InstanceName VARCHAR(255),Name VARCHAR(255),BeginTime TIMESTAMP,ScheduledDurationMs BIGINT,ActualDurationMs BIGINT,IntervalMs BIGINT,ThreadStacks LONG VARCHAR)");
		test.ensureTableExists();
		test.ensureTableExists();
	}

	@Test
	public void testDeleteById() throws SQLException {
		Profile p = new Profile("foo", "bar", 100, 10000);
		String id = p.getId();
		test.ensureTableExists();
		test.insertToDb(p);
		test.deleteById(id);
		assertNull(test.getById(id));
	}

}
