/*
 * PropertiesFilterWrapperTest.java
 * JUnit based test
 *
 * Created on November 3, 2005, 4:35 PM
 */

package com.seefusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Properties;

import org.junit.Test;

/**
 *
 * @author Daryl
 */
public class PropertiesFilterWrapperTest extends TestCase {
    
    /**
     * Test of getProperty method, of class com.seefusion.PropertiesFilterWrapper.
     */
	@Test
    public void testGetProperty() {
        Properties p = new Properties();
        p.setProperty("foo", "bar");
        p.setProperty("test.foo", "testbar");
        
        PropertiesFilterWrapper instance = new PropertiesFilterWrapper(p, "test.");
        
        String expResult = "testbar";
        String result = instance.getProperty("foo");
        assertEquals(expResult, result);
        
    }

    /**
     * Test of setProperty method, of class com.seefusion.PropertiesFilterWrapper.
     */
	@Test
    public void testSetProperty() {
        Properties p = new Properties();
        p.setProperty("foo", "bar");
        p.setProperty("test.foo", "testbar");
        
        PropertiesFilterWrapper instance = new PropertiesFilterWrapper(p, "test.");
        
        String expResult = "testbar2";
        instance.setProperty("foo", expResult);
        String result = instance.getProperty("foo");        
        assertEquals(expResult, result);
        
    }

    /**
     * Test of remove method, of class com.seefusion.PropertiesFilterWrapper.
     */
	@Test
    public void testRemove() {
        Properties p = new Properties();
        p.setProperty("foo", "bar");
        p.setProperty("test.foo", "testbar");
        
        PropertiesFilterWrapper instance = new PropertiesFilterWrapper(p, "test.");
        
        instance.remove("foo");
        assertFalse(p.containsKey("test.foo"));
        
    }


}
