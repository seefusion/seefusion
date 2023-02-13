package com.seefusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class DosManagerTest {

	private static final String IP1 = "1.1.1.1";
	private static final String IP2 = "2.2.2.2";
	DosManager test;
	
	@Before
	public void before() {
		SeeFusion sf = SeeFusion.getInstance();
		test = new DosManager(sf);		
	}
	
	@Test
	public void testExcludes() {
		assertFalse(test.excludes(IP1));
		test.addExcludedIP(IP1);
		assertTrue(test.excludes(IP1));
		assertFalse(test.excludes(IP2));
		// no need to check 'enabled' for excludes
	}

	@Test
	public void testBlocked() {
		assertFalse(test.blocks(IP1));
		test.addBlockedIP(IP1);
		assertFalse(test.blocks(IP2));
		assertFalse(test.blocks(IP1));
		test.setEnabled(true);
		assertFalse(test.blocks(IP2));
		assertTrue(test.blocks(IP1));
		test.removeBlock(IP2);
		assertFalse(test.blocks(IP2));
		assertTrue(test.blocks(IP1));
		test.removeBlock(IP1);
		assertFalse(test.blocks(IP2));
		assertFalse(test.blocks(IP1));
		test.setEnabled(false);
		assertFalse(test.blocks(IP2));
		assertFalse(test.blocks(IP1));
	}

	@Test
	public void testAction() {
		assertSame(DosManager.Action.PERMBAN, DosManager.Action.valueOf("PERMBAN"));
	}
	
	@Test
	public void testSetConfigJson() {
		JSONObject o = new JSONObject();
		o.put("enabled", true);
		o.put("action", "permban");
		o.put("requestlimit", 5);
		o.put("bandurationmin", 10);
		test.setConfigJson(o);
		assertTrue(test.isEnabled());
		assertSame(DosManager.Action.PERMBAN, test.getAction());
		assertEquals(5, test.getRequestLimit());
		assertEquals(10L, test.getBanDurationMin());
	}

}
