package com.seefusion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class SeeFusionTest extends TestCase {
    
    /**
     * Test of isDebugIP method, of class com.seefusion.SeeFusion.
     */
	@Test
	public void testIsDebugIP() {
        SeeFusion sf = new SeeFusion(true);
        sf.setDebugIPs("127.0.0.1");
        assertTrue(sf.isDebugIP("127.0.0.1"));
        assertFalse(sf.isDebugIP(""));
        assertFalse(sf.isDebugIP(null));
        sf.setDebugIPs(null);
        assertFalse(sf.isDebugIP("127.0.0.1"));
        assertFalse(sf.isDebugIP(""));
        assertFalse(sf.isDebugIP(null));
        sf.setDebugIPs("");
        assertFalse(sf.isDebugIP("127.0.0.1"));
        assertFalse(sf.isDebugIP(""));
        assertFalse(sf.isDebugIP(null));
    }

}
