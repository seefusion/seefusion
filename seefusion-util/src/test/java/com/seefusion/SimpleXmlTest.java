package com.seefusion;

import static org.junit.Assert.*;

import org.junit.Test;

public class SimpleXmlTest {


	@Test
	public void testToJsonSimple() {
		SimpleXml xml = new SimpleXml("test");
		xml.addTag("foo", "bar1");
		System.out.println(xml.toString());
		JSONObject ret = xml.toJson();
		System.out.println(ret.toString(2));
	}

	@Test
	public void testToString() {
		SimpleXml xml = new SimpleXml("test");
		xml.addTag("foo", "bar1>>>");
		System.out.println(xml.toString());
	}

	@Test
	public void testNestedXml() {
		SimpleXml xml = new SimpleXml("test");
		xml.addTag("foo", "bar1");
		SimpleXml xml2 = new SimpleXml("test2");
		xml2.addTag("foo", "bar2");
		assertTrue(xml2.remove("foo"));
		xml2.addTag("foo", "bar3");
		xml2.addTag("foo", "bar4");
		xml.add(xml2);
		String s = xml.toString();
		System.out.println(s);
		assertTrue(s.contains("<test2>"));
		assertTrue(s.contains("<foo>bar3</foo>"));
		assertTrue(s.contains("<foo>bar4</foo>"));
		assertFalse(s.contains("&lt;"));
		JSONObject ret = xml.toJson();
		System.out.println(ret.toString(2));
	}

	@Test
	public void testToJson() {
		SimpleXml xml = new SimpleXml("test");
		xml.addTag("foo", "bar1");
		xml.addTag("foo", "bar2");
		xml.addTag("foo", "bar3");
		xml.addTag("bar", "baz");
		xml.setAttribute("fooattribute", "bar");
		System.out.println(xml.toString());
		JSONObject ret = xml.toJson();
		System.out.println(ret.toString(2));
		assertTrue(ret.length() != 0);
	}
	
	@Test
	public void testToJson_ArrayOfComplex() {
		SimpleXml xml = new SimpleXml("test");
		xml.addTag("foo", new SimpleXml("who", "what"));
		xml.addTag("foo", new SimpleXml("where", "why"));
		xml.addTag("foo", new SimpleXml("how", "when"));
		xml.addTag("foo", "bar2");
		xml.addTag("foo", "bar3");
		xml.addTag("bar", "baz");
		xml.setAttribute("fooattribute", "bar");
		System.out.println(xml.toString());
		JSONObject ret = xml.toJson();
		System.out.println(ret.toString(2));
		assertTrue(ret.length() != 0);
	}

}
