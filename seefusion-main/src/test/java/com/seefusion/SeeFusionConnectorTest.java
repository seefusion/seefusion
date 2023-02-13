/*
 * SeeFusionConnectorTest.java
 * JUnit based test
 *
 * Created on October 6, 2005, 2:27 PM
 */

package com.seefusion;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Daryl
 */
@Ignore
public class SeeFusionConnectorTest extends TestCase {
    
    /**
     * Test of getInstance method, of class com.seefusion.SeeFusionConnector.
     */
	@Test
    public void testGetInstance() {
        SeeFusionConnector sf = SeeFusionConnector.getInstance(".");
        assertNotNull(sf);
        sf.shutdown();
    }

    /**
     * Test of createRequest method, of class com.seefusion.SeeFusionConnector.
     */
	@Test
    public void testCreateRequest() {
        Properties props = new Properties();
        props.setProperty("name", "inst");
        props.setProperty("listeners", "all:8998");
        props.setProperty("historySize", "80");
        SeeFusionConnector sfc = SeeFusionConnector.getInstance(".");
        assertNotNull(sfc);
        assertNotNull(sfc.sf);
        assertTrue(sfc.sf.isRunning());
        SeeFusionRequest req = sfc.createRequest("name", "requestURI", "queryString", "remoteIP", "GET", "", false);
        assertTrue(sfc.sf.getMasterRequestList().getCurrentRequests().size() == 1);
        req.close();
        assertTrue(sfc.sf.getMasterRequestList().getCurrentRequests().size() == 0);
        sfc.shutdown();
    }
    
}
