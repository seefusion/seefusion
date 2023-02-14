package com.seefusion.installer;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class InstallTomcatValveTest {

	String xml;
	
	// @Before
	public void setUp() throws Exception {
		xml = Util.readFile("./src/test/resources/tomcat7/server.xml");
	}
	
	// @Test
	public void testInstallUninstall() throws Exception {
		InstallTomcatValve test = new InstallTomcatValve(xml);
		assertFalse(test.isInstalled());
		test.install();
		String _xml = test.getXml();
		System.out.println(_xml);
		assertTrue(test.isInstalled());
		int filterClassPos = _xml.indexOf(InstallTomcatValve.CLASS_NAME);
		assertNotSame(-1, filterClassPos);
		int nextCommentStartPos = _xml.indexOf("<!--", filterClassPos);
		int nextCommentEndPos = _xml.indexOf("-->", filterClassPos);
		assertTrue("Valve added inside a comment!", nextCommentEndPos >= nextCommentStartPos);
		test.uninstall();
		_xml = test.getXml();
		System.out.println(_xml);
		filterClassPos = _xml.indexOf(InstallTomcatValve.CLASS_NAME);
		assertEquals(-1, filterClassPos);
		assertFalse(test.isInstalled());
	}

}
