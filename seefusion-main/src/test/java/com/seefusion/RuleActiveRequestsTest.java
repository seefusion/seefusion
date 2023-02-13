package com.seefusion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.Test;

@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class RuleActiveRequestsTest {

	@Test
	public void testSaveLoadXml() throws InstantiationException {
		RuleActiveRequests test = (RuleActiveRequests)ActiveMonitoringRule.getInstance("sactivereq");
		LinkedList<String>excludes = new LinkedList<String>();
		excludes.add("127.0.0.1");
		excludes.add("8.8.8.8");
		excludes.add("8.8.4.4");
		test.setExcludes(excludes);
		SimpleXml xml = test.toXml();
		// System.out.println(xml.toString());
		excludes = new LinkedList<String>();
		test.setExcludes(excludes);
		test.loadFrom(xml);
		excludes = test.getExcludes();
		assertTrue(excludes.contains("8.8.8.8"));
		assertTrue(excludes.contains("8.8.4.4"));
		assertFalse(excludes.contains("8.8.6.6"));
	}

	@Test
	public void testSaveLoadJson() throws InstantiationException {
		RuleActiveRequests test = (RuleActiveRequests)ActiveMonitoringRule.getInstance("sactivereq");
		test.setName("test");
		LinkedList<String>excludes = new LinkedList<String>();
		excludes.add("127.0.0.1");
		excludes.add("8.8.8.8");
		excludes.add("8.8.4.4");
		test.setExcludes(excludes);
		JSONObject xml = test.toJson();
		// System.out.println(xml.toString());
		excludes = new LinkedList<String>();
		test.setExcludes(excludes);
		test.loadFrom(xml);
		excludes = test.getExcludes();
		assertTrue(excludes.contains("8.8.8.8"));
		assertTrue(excludes.contains("8.8.4.4"));
		assertFalse(excludes.contains("8.8.6.6"));
	}

}
