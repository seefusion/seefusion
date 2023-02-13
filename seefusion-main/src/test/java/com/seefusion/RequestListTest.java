package com.seefusion;

import static org.junit.Assert.assertNotSame;

import org.junit.Test;

public class RequestListTest {

	@Test
	public void testToJSON() {
		RequestList test = new RequestList();
		RequestInfo ri = new RequestInfo();
		ri.setPageURL("http://sometesturl");
		ri.setRequestURI("http://sometesturl");
		ri.setServerName("testServer");
		test.createRequest(ri);
		String ret = test.toJSON().toString();
		// System.out.println(ret);
		assertNotSame("URL not found in " + ret , -1, ret.indexOf("sometesturl"));
	}

}
