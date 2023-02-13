/**
 * 
 */
package com.seefusion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Daryl
 *
 */
public class SuffixListTest extends TestCase {
	
	/*
	 * Test method for 'com.seefusion.SuffixList.SuffixList(String)'
	 */
	@Test
	public void testSuffixList() {
		SuffixList sf = new SuffixList(".cfm");
		assertFalse(sf.hasMatch(""));
		assertFalse(sf.hasMatch("/"));
		assertFalse(sf.hasMatch("/foo/"));
		assertTrue(sf.hasMatch("/foo/bar.cfm"));
		assertFalse(sf.hasMatch("/foo/bar.gif"));
	}

	/*
	 * Test method for 'com.seefusion.SuffixList.hasMatch(String)'
	 */
	@Test
	public void testHasMatch() {
	    /**
	     * Test of hasDebugSuffix method, of class com.seefusion.SeeFusion.
	     */
		SuffixList sf = new SuffixList();
		sf.setSuffixes(null);
		assertTrue(!sf.hasMatch(""));
		sf.setSuffixes(".cfm");
		assertFalse(sf.hasMatch(""));
		assertFalse(sf.hasMatch("/"));
		assertFalse(sf.hasMatch("/foo/"));
		assertTrue(sf.hasMatch("/foo/bar.cfm"));
		assertFalse(sf.hasMatch("/foo/bar.gif"));
	}

	/*
	 * Test method for 'com.seefusion.SuffixList.setSuffixes(String)'
	 */
	@Test
	public void testSetSuffixes() {
		SuffixList sf = new SuffixList();
		sf.setSuffixes(null);
		assertTrue(!sf.hasMatch(""));
		sf.setSuffixes(". .cfm");
		assertTrue(sf.hasMatch(""));
		assertTrue(sf.hasMatch("/"));
		assertTrue(sf.hasMatch("/foo/"));
		assertTrue(sf.hasMatch("/foo/bar.cfm"));
		assertFalse(sf.hasMatch("/foo/bar.gif"));
		assertTrue(sf.hasMatch("/foo/bar/servlet"));
	}

}
