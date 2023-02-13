package com.seefusion;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HttpRequestMapTest {

	HttpRequestMap test;
	
	// need to make this test portable
	//@Before
	public void setup() throws IOException {
		test = HttpRequestMap.getInstance("../../seefusion-5.ui/dist");
	}

	//@Test
	public void testIndex() throws IOException {
		SeeFusion sf = new SeeFusion();
		HttpRequestHandler ret = test.getHttpRequest("/index.html");
		assertNotNull(ret);
		assertTrue(ret.getClass().getName() + " should implement HttpDoFileServer", ret instanceof HttpDoFileServer);
		HttpDoFileServer f = (HttpDoFileServer)ret;
		MockSocketTalker t = new MockSocketTalker(sf, "get /index.html http/1.0");
		f.doGet(t);
		ByteArrayOutputStream out = t.mockSocket.out;
		// System.out.println(out.size());
		assertTrue(out.size() > 512);
	}

	//@Test
	public void testMixedCase() throws IOException {
		SeeFusion sf = new SeeFusion();
		HttpRequestHandler ret = test.getHttpRequest("/seefusion.js");
		assertTrue(ret.getClass().getName() + " should implement HttpDoFileServer", ret instanceof HttpDoFileServer);
		HttpDoFileServer f = (HttpDoFileServer)ret;
		MockSocketTalker t = new MockSocketTalker(sf);
		f.doGet(t);
		ByteArrayOutputStream out = t.mockSocket.out;
		// System.out.println(out.size());
		assertTrue(out.size() > 512);
	}

}
