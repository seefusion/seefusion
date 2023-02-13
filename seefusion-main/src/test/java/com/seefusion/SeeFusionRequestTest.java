/*
 * SeeFusionRequestTest.java
 * JUnit based test
 *
 * Created on October 6, 2005, 4:05 PM
 */

package com.seefusion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author Daryl
 */
public class SeeFusionRequestTest extends TestCase {
    
    /**
     * Test of setDebugState method, of class com.seefusion.SeeFusionRequest.
     */
	@Test
    public void testSetDebugState() {
        SeeFusionConnector sfc = SeeFusionConnector.getInstance(".");
        assertNotNull(sfc);
        assertNotNull(sfc.sf);
        assertTrue(sfc.sf.isRunning());
        SeeFusionRequest req = sfc.createRequest("name", "requestURI", "queryString", "remoteIP", "GET", "", false);
        
        req.setDebugState(true);
        assertTrue(req.pi.isDebugPage());
        
        req.close();
        
        sfc.shutdown();
    }

    /**
     * Test of getDebugOutput method, of class com.seefusion.SeeFusionRequest.
     */
	@Test
    public void testGetDebugOutput() {
        SeeFusionConnector sfc = SeeFusionConnector.getInstance(".");
        assertNotNull(sfc);
        assertNotNull(sfc.sf);
        assertTrue(sfc.sf.isRunning());
        SeeFusionRequest req = sfc.createRequest("name", "requestURI", "queryString", "remoteIP", "GET", "", false);
        
        assertTrue(req.getDebugOutput().length()==0);

        assertTrue(req.trace("Hello, world!").indexOf("Hello, world!") != -1);
        assertTrue(req.getDebugOutput().indexOf("Hello, world!") == -1);
        assertTrue(req.getDebugOutput().indexOf("Hello, world!") == -1);
        
        req.setDebugState(true);
        req.trace("Hello, world!");
        assertTrue(req.getDebugOutput().indexOf("Hello, world!") != -1);
        
        req.close();
        
        sfc.shutdown();
    }

    /**
     * Test of getThisRequest method, of class com.seefusion.SeeFusionRequest.
     */
	@Test
    public void testGetThisRequest() {
        SeeFusionConnector sfc = SeeFusionConnector.getInstance(".");
        assertNotNull(sfc);
        assertNotNull(sfc.sf);
        assertTrue(sfc.sf.isRunning());
        
        //System.out.println(SeeFusionRequest.getThisRequest().pi.getInfoText());
        assertNull(SeeFusionRequest.getThisRequest());
        
        SeeFusionRequest req = sfc.createRequest("name", "requestURI", "queryString", "remoteIP", "GET", "", false);

        req.setDebugState(true);
        req.trace("Hello, world!");
        assertTrue(req.getDebugOutput().indexOf("Hello, world!") != -1);
        
        SeeFusionRequest req2 = SeeFusionRequest.getThisRequest();
        assertTrue(req2.getDebugOutput().indexOf("Hello, world!") != -1);
        
        req2.close();
        
        assertFalse(req.isActive());
        
        sfc.shutdown();
    }
    
}
