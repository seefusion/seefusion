package com.seefusion;

import static org.junit.Assert.*;

import java.util.Properties;

import org.junit.Test;

public class RequestLineTest {

	@Test
	public void testRequestLine() {
		RequestLine test = new RequestLine("get /abc/def?foo=bar http/1.0");
		assertEquals("get", test.getRequestMethod());
		assertEquals("/abc/def", test.getPath());
		assertEquals("foo=bar", test.getQueryString());
		assertEquals("http/1.0", test.getProtocol());
		assertEquals("bar", test.getQueryProperties().get("foo"));
	}

    /**
     * Test of parseProperties method
     */
	@Test
    public void testparseProperties() {
        Properties m;
        m = RequestLine.parseProperties("");
        m = RequestLine.parseProperties("?");
        m = RequestLine.parseProperties("&");
        m = RequestLine.parseProperties("foo");
        m = RequestLine.parseProperties("foo=");
        m = RequestLine.parseProperties("=");
        m = RequestLine.parseProperties("=bar");
        m = RequestLine.parseProperties("?foo=bar");
        assertEquals("bar", (String)m.get("foo"));
        m = RequestLine.parseProperties("foo=bar&foo2=bar2&foo3=bar3");
        assertEquals("bar", (String)m.get("foo"));
        assertEquals("bar2", (String)m.get("foo2"));
        assertEquals("bar3", (String)m.get("foo3"));
    }
	
    public void testGetPageName() {
        System.out.println("testGetPageName");
        assertEquals("", new RequestLine("").getPath());
        assertEquals("", new RequestLine("get ").getPath());
    	assertEquals("/", new RequestLine("get /").getPath());
    	assertEquals("/foo", new RequestLine("get /foo").getPath());
    	assertEquals("/foo", new RequestLine("get /foo http/1.1").getPath());
    	assertEquals("/foo/bar", new RequestLine("get /foo/bar?").getPath());
    	assertEquals("/foo/bar", new RequestLine("get /foo/bar?bleh=whatever").getPath());
    }

}
