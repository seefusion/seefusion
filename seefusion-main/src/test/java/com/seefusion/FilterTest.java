package com.seefusion;

import static org.junit.Assert.*;

import java.util.LinkedList;

import javax.servlet.FilterChain;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mockit.Expectations;
import mockit.Mocked;

public class FilterTest extends TestCase {

	private Filter test;

	private static final String req1 = "GET / HTTP/1.0\r\n\r\n";
	private static final String response1 = "HTTP/1.0 200 OK\r\nServer: MockFilter\r\n\r\n<html><body>Hello, cruel world!</body></html>";
	
	@Before
	public void setUp() throws Exception {
		test = new Filter();
		test.init(new MockFilterConfig());
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
	public void testgetForwardedMultipleRemoteAddr(@Mocked final HttpServletRequest httpRequest) {
		new Expectations() {{
			httpRequest.getHeader("X-Forwarded-For"); result="8.8.8.8, 72.14.255.255";
		}};
		assertEquals("72.14.255.255", Filter.getForwardedRemoteAddr(httpRequest, "X-Forwarded-For", 1));
		assertEquals("8.8.8.8", Filter.getForwardedRemoteAddr(httpRequest, "X-Forwarded-For", 2));
	}
	
	@Test
	@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
	public void testgetForwardedSingleRemoteAddr(@Mocked final HttpServletRequest httpRequest) {
		new Expectations() {{
			httpRequest.getHeader("X-Forwarded-For"); result="8.8.8.8";
		}};
		assertEquals("8.8.8.8", Filter.getForwardedRemoteAddr(httpRequest, "X-Forwarded-For", 1));
	}

	@Test
	@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
	public void testgetForwardedForMissingHeader(@Mocked final HttpServletRequest httpRequest) {
		new Expectations() {{
			httpRequest.getHeader("X-Forwarded-For"); result=null;
			httpRequest.getRemoteAddr(); result = "8.8.8.8";
		}};
		assertEquals("8.8.8.8", Filter.getForwardedRemoteAddr(httpRequest, "X-Forwarded-For", 1));
	}

	@Test
	@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
	public void testgetForwardedForInvalidDepth(@Mocked final HttpServletRequest httpRequest) {
		new Expectations() {{
			httpRequest.getHeader("X-Forwarded-For"); result="1.1.1.1";
			httpRequest.getRemoteAddr(); result = "8.8.8.8";
		}};
		assertEquals("8.8.8.8", Filter.getForwardedRemoteAddr(httpRequest, "X-Forwarded-For", 3));
	}

}
