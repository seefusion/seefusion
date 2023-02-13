package com.seefusion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ThreadStackElementTest {

	@Test
	public void testJsonRoundTripWithNullFileName() {
		ThreadStackElement e = new ThreadStackElement(new StackTraceElement("foo", "bar", null, -1));
		e = new ThreadStackElement(e.toJson());
		assertEquals("foo", e.getClassName());
		assertEquals("bar", e.getMethodName());
		assertEquals(null, e.getFileName());
		assertEquals(-1, e.getLineNumber());
	}

	@Test
	public void testJsonRoundTrip() {
		ThreadStackElement e = new ThreadStackElement(new StackTraceElement("foo", "bar", "baz", -1));
		e = new ThreadStackElement(e.toJson());
		assertEquals("foo", e.getClassName());
		assertEquals("bar", e.getMethodName());
		assertEquals("baz", e.getFileName());
		assertEquals(-1, e.getLineNumber());
	}

}
