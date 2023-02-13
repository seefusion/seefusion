package com.seefusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.junit.Test;

public class XmlTest {

	private static final Logger LOG = Logger.getLogger(XmlTest.class.getName());
	private static final String CRLF = "\r\n";

	@Test
	public void testXmlStringXml() {
		SimpleXml xml = new SimpleXml("foo", new SimpleXml("bar", "baz"));
		LOG.info(CRLF + xml.toString());
		assertTrue(xml.toString().indexOf("<bar>baz</bar>") > 0);
	}

	@Test
	public void testXmlStringLong() {
		SimpleXml xml = new SimpleXml("foo", new SimpleXml("bar", 12345L));
		LOG.info(CRLF + xml.toString());
		assertTrue(xml.toString().indexOf("<bar>12345</bar>") > 0);
	}

	@Test
	public void testXmlStringInt() {
		SimpleXml xml = new SimpleXml("foo", new SimpleXml("bar", 12345));
		LOG.info(CRLF + xml.toString());
		assertTrue(xml.toString().indexOf("<bar>12345</bar>") > 0);
	}

	@Test
	public void testXmlStringFloat() {
		SimpleXml xml = new SimpleXml("foo", new SimpleXml("bar", (float)12345.678));
		LOG.info(CRLF + xml.toString());
		assertTrue(xml.toString().indexOf("<bar>12345.678</bar>") > 0);
	}

	@Test
	public void testXmlStringBoolean() {
		SimpleXml xml = new SimpleXml("foo", new SimpleXml("bar", false));
		LOG.info(CRLF + xml.toString());
		assertTrue(xml.toString().indexOf("<bar>false</bar>") > 0);
	}

	@Test
	public void testAddXml() {
		SimpleXml xml = new SimpleXml("foo");
		xml.add(new SimpleXml("bar", false));
		xml.add(new SimpleXml("baz", true));
		LOG.info(CRLF + xml.toString());
		assertTrue(xml.toString().indexOf("<bar>false</bar>") > 0);
		assertTrue(xml.toString().indexOf("<baz>true</baz>") > 0);
	}
	
	@Test
	public void testAddXmlNested() {
		SimpleXml xml = new SimpleXml("foo");
		xml.add(new SimpleXml("bar", new SimpleXml("baz", true)));
		LOG.info(CRLF + xml.toString());
		assertEquals("<foo>\r\n\t<bar>\r\n\t\t<baz>true</baz>\r\n\t</bar>\r\n</foo>\r\n", xml.toString());
	}
	
	@Test
	public void testAddEmptyXml() {
		SimpleXml xml = new SimpleXml("foo");
		xml.add(new SimpleXml("bar"));
		LOG.info(CRLF + xml.toString());
		assertEquals("<foo>\r\n\t<bar></bar>\r\n</foo>\r\n", xml.toString());
	}

	@Test
	public void testSetAttribute() {
		SimpleXml xml = new SimpleXml("foo");
		xml.setAttribute("bar", "baz");
		LOG.info(CRLF + xml.toString());
		assertTrue(xml.toString().indexOf("bar=\"baz\"") > 0);
	}

}
