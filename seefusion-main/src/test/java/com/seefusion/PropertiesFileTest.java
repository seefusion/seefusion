/*
 * PropertiesFileTest.java
 * JUnit based test
 *
 * Created on September 14, 2005, 4:20 PM
 */

package com.seefusion;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

/**
 *
 * @author Daryl
 */
public class PropertiesFileTest extends TestCase {
    
    PropertiesFile pf;
    
    /**
     * Test of setFilePropertiesPath method, of class com.seefusion.Config.
     */
    @Test
    public void testFilePropertiesAll() throws Exception {
        File f=null;
        try {
            f = File.createTempFile("test", null);
            Properties p = new PropertiesFile(f);
            p.setProperty("foo", "barFile");
            pf = new PropertiesFile(f);
            assertEquals("barFile", pf.getProperty("foo"));

            // test setProperty()
            pf.setProperty("bar", "baz");
            pf = new PropertiesFile(f);
            assertEquals("baz", pf.getProperty("bar"));
            
            // test third constructor
            Properties defaults = new Properties();
            defaults.setProperty("foo2","bar2");
            defaults.setProperty("bar","bar2");
            pf = new PropertiesFile(f, defaults, null);
            assertEquals("bar2", pf.getProperty("foo2"));
            assertEquals("baz", pf.getProperty("bar"));

        } finally {
            if(f!=null) f.delete();
        }
    }
    
}
