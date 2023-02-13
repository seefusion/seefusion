package com.seefusion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class ConfigTest {

	@Test @Ignore
	public void testRules() {
		Config config = new Config("./");
		assertTrue(config.isConfigBackedByFile());
		// SimpleXml xml = config.getXml();
		// System.out.println(xml);
		// System.out.println(xml.get("rules"));
		ActiveMonitoringRules rules = new ActiveMonitoringRules();
		rules.loadFromXML(config);
		assertFalse(rules.size()==0);
	}
	
}
