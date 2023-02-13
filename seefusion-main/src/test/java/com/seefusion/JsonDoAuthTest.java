package com.seefusion;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JsonDoAuthTest {

	static SeeFusion sf = SeeFusion.getInstance();
	
	@Before
	public void before() {
		sf.setHttpPassword("");
		sf.setHttpKillPassword("");
		sf.setHttpConfigPassword("");
	}

	@After
	public void after() {
		sf.setHttpPassword("");
		sf.setHttpKillPassword("");
		sf.setHttpConfigPassword("");
	}

	@Test
	public void testNoPasswordWithNoneRequired() throws Exception {

		MockSocketTalker talker = new MockSocketTalker(sf);
		JsonDoAuth test = new JsonDoAuth();
		test.doGet(talker);

		Perm perm = JsonDoAuth.getLastPerm();
		assertTrue(perm.has(Perm.LOGGEDIN));
		assertTrue(perm.has(Perm.CONFIG));
	}

	@Test
	public void testBlankPassword() throws Exception {
		MockSocketTalker talker = new MockSocketTalker(sf);
		JSONObject postData = new JSONObject("{\"password\":\"\"}");
		talker.postData = postData;

		JsonDoAuth test = new JsonDoAuth();
		test.doGet(talker);

		Perm perm = JsonDoAuth.getLastPerm();
		assertTrue(perm.has(Perm.LOGGEDIN));
		assertTrue(perm.has(Perm.CONFIG));
	}

	@Test
	public void testWrongPassword() throws Exception {
		// wrong password should have same result as no password
		MockSocketTalker talker = new MockSocketTalker(sf);
		JSONObject postData = new JSONObject("{\"password\":\"asdf\"}");
		talker.postData = postData;

		JsonDoAuth test = new JsonDoAuth();
		test.doGet(talker);

		Perm perm = JsonDoAuth.getLastPerm();
		assertFalse(sf.hasPassword());
		assertTrue("If SF has no password, any password should work", perm.has(Perm.LOGGEDIN));
		assertTrue(perm.has(Perm.CONFIG));

		sf.httpPassword = new Password("test");
		assertTrue(sf.hasPassword());
		test.doGet(talker);

		perm = JsonDoAuth.getLastPerm();
		assertFalse(perm.has(Perm.LOGGEDIN));
		assertFalse(perm.has(Perm.CONFIG));
	}

	@Test
	public void testNonConfigPassword() throws Exception {
		Password p1 = new Password("test");
		Password p2 = new Password("test2");
		sf.httpPassword = p1;
		sf.httpConfigPassword = p2;
		assertFalse(sf.httpConfigPassword.equals("test"));
		assertTrue(sf.httpConfigPassword.equals("test2"));

		MockSocketTalker talker = new MockSocketTalker(sf);
		JSONObject postData = new JSONObject("{\"password\":\"test\"}");
		talker.postData = postData;
		JsonDoAuth test = new JsonDoAuth();
		test.doGet(talker);

		Perm perm = JsonDoAuth.getLastPerm();
		assertTrue(perm.has(Perm.LOGGEDIN));
		assertFalse(perm.has(Perm.KILL));
		assertFalse(perm.has(Perm.CONFIG));
	}

	@Test
	public void testConfigPassword() throws Exception {
		Password p1 = new Password("test");
		Password p2 = new Password("test2");
		sf.httpPassword = p1;
		sf.httpConfigPassword = p2;
		assertFalse(sf.httpConfigPassword.equals("test"));
		assertTrue(sf.httpConfigPassword.equals("test2"));

		MockSocketTalker talker = new MockSocketTalker(sf);
		JSONObject postData = new JSONObject("{\"password\":\"test2\"}");
		talker.postData = postData;
		JsonDoAuth test = new JsonDoAuth();
		test.doGet(talker);

		Perm perm = JsonDoAuth.getLastPerm();
		assertTrue(perm.has(Perm.LOGGEDIN));
		assertTrue(perm.has(Perm.KILL));
		assertTrue(perm.has(Perm.CONFIG));

		postData = new JSONObject("{\"password\":\"\"}");
		talker.postData = postData;
		test.doGet(talker);

		perm = JsonDoAuth.getLastPerm();
		assertTrue(perm.has(Perm.LOGGEDIN));
		assertTrue(perm.has(Perm.KILL));
		assertFalse(perm.has(Perm.CONFIG));
	}

}
