/**
 * 
 */
package com.seefusion;

import static org.junit.Assert.*;
import static com.seefusion.SimpleXmlParser.*;
import org.junit.Test;

/**
 * @author Daryl
 *
 */
public class SimpleXmlParserTest {

	/**
	 * Test method for {@link com.seefusion.SimpleXmlParser#isDecimal(java.lang.String)}.
	 */
	@Test
	public void testIsDecimalString() {
		assertTrue(isDecimal("1"));
		assertTrue(isDecimal("10"));
		assertTrue(isDecimal("01"));
		assertTrue(isDecimal("1234567890"));
		assertFalse(isDecimal(" 1"));
		assertFalse(isDecimal("1a"));
		assertFalse(isDecimal("o1"));
		assertFalse(isDecimal("0x1"));
		assertFalse(isDecimal(""));
		assertFalse(isDecimal(null));
	}

	/**
	 * Test method for {@link com.seefusion.SimpleXmlParser#isDecimal(char)}.
	 */
	@Test
	public void testIsDecimalChar() {
		assertTrue(isDecimal('1'));
		assertTrue(isDecimal('0'));
		assertTrue(isDecimal('9'));
		assertFalse(isDecimal(' '));
		assertFalse(isDecimal('a'));
		assertFalse(isDecimal('x'));
		assertFalse(isDecimal('&'));
	}

	/**
	 * Test method for {@link com.seefusion.SimpleXmlParser#isHexChar(char)}.
	 */
	@Test
	public void testIsHexChar() {
		assertTrue(isHexChar('1'));
		assertTrue(isHexChar('a'));
		assertTrue(isHexChar('A'));
		assertTrue(isHexChar('f'));
		assertTrue(isHexChar('F'));
		assertTrue(isHexChar('0'));
		assertTrue(isHexChar('9'));
		assertFalse(isHexChar('x'));
		assertFalse(isHexChar(' '));
		assertFalse(isHexChar('o'));
		assertFalse(isHexChar('q'));
		assertFalse(isHexChar('&'));
	}

	/**
	 * Test method for {@link com.seefusion.SimpleXmlParser#hex2int(char)}.
	 */
	@Test
	public void testHex2int() {
		assertEquals(15, hex2int('f'));
		assertEquals(10, hex2int('A'));
		assertEquals(9, hex2int('9'));
		assertEquals(0, hex2int('0'));
		assertEquals(5, hex2int('5'));
	}

	/**
	 * Test method for {@link com.seefusion.SimpleXmlParser#xmlStringDecode(java.lang.String)}.
	 */
	@Test
	public void testXmlStringDecode() {
		assertEquals("&", xmlStringDecode("&amp;"));
		assertEquals("me & my dawg", xmlStringDecode("me &amp; my d&#97;wg"));
	}

	/**
	 * Test method for {@link com.seefusion.SimpleXmlParser#decodeXMLattributes(com.seefusion.SimpleXml, java.lang.String)}.
	 */
	@Test
	public void testDecodeXMLattributes() {
		SimpleXml xml = new SimpleXml("zzz");
		decodeXMLattributes(xml, "foo='bar' foo2=\"bar2\"");
		assertEquals("bar", xml.getString("foo"));
		assertEquals("bar2", xml.getString("foo2"));
		xml = new SimpleXml("zzz");
		decodeXMLattributes(xml, "\tfoo = 'bar'\tfoo2\t=\t\"bar2 \"   ");
		assertEquals("bar", xml.getString("foo"));
		assertEquals("bar2 ", xml.getString("foo2"));
		decodeXMLattributes(xml, "\tfoo = 'bar'\tfoo2\t=\t\"bar2&quot; \"   ");
		assertEquals("bar", xml.getString("foo"));
		assertEquals("bar2\" ", xml.getString("foo2"));
		
	}

	/**
	 * Test method for {@link com.seefusion.SimpleXmlParser#parseXml(java.lang.String)}.
	 */
	@Test
	public void testParseXml() {
		SimpleXml xml = parseXml("<tag><subtag>foo</subtag></tag>");
		assertEquals("tag", xml.getTagName());
		SimpleXml subtag = xml.get("subtag");
		assertEquals("subtag", subtag.getTagName());
		assertEquals("foo", subtag.getSimpleValue());
	}
	
	/**
	 * Test method for {@link com.seefusion.SimpleXmlParser#parseXml(java.lang.String)}.
	 */
	@Test
	public void testParseSeeStackInfo() throws Exception {
		SimpleXml xml = parseXml(Util.readFile("./src/test/resources/SeeStackInfo.xml"));
		assertEquals("seestackinfo", xml.getTagName());
		SimpleXml methodInfos = xml.get("methodinfos");
		SimpleXml subtag = methodInfos.get("methodinfo", 5);
		assertNotNull(subtag);
		assertEquals("methodinfo", subtag.getTagName());
		assertEquals("coldfusion.compiler.NeoTranslator.translateJava", subtag.getProperty("nameprefix"));
		String longdesc = subtag.getProperty("longdescription");
		assertTrue(longdesc.length() > 50);
		assertEquals("high", subtag.getProperty("importance"));
		xml = parseXml(Util.readFile("./src/test/resources/server.xml"));
	}
	
	/**
	 * Test method for {@link com.seefusion.SimpleXmlParser#parseXml(java.lang.String)}.
	 */
	@Test
	public void testParseCDATA() {
		SimpleXml xml = parseXml("<tag><subtag><![CDATA[foo]]></subtag></tag>");
		assertEquals("tag", xml.getTagName());
		SimpleXml subtag = xml.get("subtag");
		assertEquals("subtag", subtag.getTagName());
		assertEquals("foo", subtag.getSimpleValue());
	}

}
