/**
 * 
 */
package com.seefusion.installer;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * @author Daryl
 *
 */
public class InstallWebXmlTest {
	
	String xml;
	
	@Before
	public void setUp() throws Exception {
		xml = Util.readFile("src/test/resources/jrun4/web.xml");
	}
	
	@Test
	public void testInstallUninstall() throws Exception {
		InstallWebXml.debugMode = true;
		String _xml = InstallWebXml.installFilter(xml);
		//System.out.println(_xml);
		int filterClassPos = _xml.indexOf(InstallWebXml.FILTER_CLASS);
		assertNotSame(-1, filterClassPos);
		_xml = InstallWebXml.uninstallFilter(_xml);
		//System.out.println(_xml);
		filterClassPos = _xml.indexOf(InstallWebXml.FILTER_CLASS);
		assertEquals(-1, filterClassPos);
	}
	
	@Test
	public void testAdjustForComment() {
		String s;
		s = "";
		assertEquals(InstallWebXml.adjustForComment(s, 0), 0);
		assertEquals(InstallWebXml.adjustForComment("blah blah blah",5), 5);
		s = "blah <!-- blah --> blah foo blah";
		assertEquals(s.indexOf("foo"), InstallWebXml.adjustForComment(s, s.indexOf("foo")));
		s = "blah <!-- blah --> blah foo blah";
		assertEquals(s.indexOf("-->")+3, InstallWebXml.adjustForComment(s, s.indexOf("blah", 4)));
	}
	
	@Test
	public void testInstall2() throws Exception {
		System.out.println("testInstall2");
		xml = "	<!--\r\n" + 
				"	<context-param id=\"macromedia_context_88\">\r\n" + 
				"		<param-name>cf.class.path</param-name>\r\n" + 
				"		<param-value>./../../lib</param-value>\r\n" + 
				"	</context-param>\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"	-->";
		int oldXmlLen = xml.indexOf("-->")+3;
		xml = InstallWebXml.install(xml);
		System.out.println(xml);
		assertTrue("filter should be placed after comment." + xml.indexOf("<filter") + "..." + oldXmlLen, xml.indexOf("<filter") > oldXmlLen);
		System.out.println("/testInstall2");
	}
	
}
