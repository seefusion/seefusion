package com.seefusion;

import static org.junit.Assert.*;

import org.junit.Test;

public class JsonDoSaveConfigTest {

	@Test
	public void testSaveConfigPassword() throws Exception  {
		SeeFusion sf = SeeFusion.getInstance();

		Password p1 = new Password("test");
		sf.httpConfigPassword = p1;
		assertTrue(sf.httpConfigPassword.equals("test"));
		assertFalse(sf.httpConfigPassword.equals("test2"));
		
		JSONObject postData = new JSONObject("{\"configitem\":\"httpConfigPassword\",\"value\":\"test2\",\"type\":\"password\",\"busy\":true,\"saved\":false,\"error\":false}");
		
		MockSocketTalker talker = new MockSocketTalker(sf, "post /something http/1.0");
		JsonDoSaveConfig test = new JsonDoSaveConfig();
		
		talker.postData = postData;
		
		test.doGet(talker);
		
		assertTrue(sf.httpConfigPassword.equals("test2"));
		assertFalse(sf.httpPassword.equals("test2"));
		
		sf.httpPassword = null;
		sf.httpKillPassword = null;
		sf.httpConfigPassword = null;
	}

}
