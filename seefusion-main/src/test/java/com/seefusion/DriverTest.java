/*
 * DriverTest.java
 * JUnit based test
 *
 */

package com.seefusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Properties;

import org.junit.Test;

public class DriverTest extends TestCase {
    
    /**
     * Test of acceptsURL method, of class com.seefusion.Driver.
     */
	@Test
    public void testAcceptsURL() throws Exception {
        Driver instance = new Driver();
        
        boolean result = instance.acceptsURL("jdbc:seefusion:fooooooo");
        assertTrue(result);
    }
    
    /**
     * Test of jdbcCompliant method, of class com.seefusion.Driver.
     */
	@Test
    public void testJdbcCompliant() {
        Driver instance = new Driver();
        
        boolean expResult = true;
        boolean result = instance.jdbcCompliant();
        assertEquals(expResult, result);
        
    }
    
	@Test
    public void testParseUrlProperties() {
        Properties p = null;
        try {
            p = Driver.parseUrlProperties("jdbc:seefusion:{jdbc:mysql://localhost:3306/realestate?}");
            assertEquals("jdbc:mysql://localhost:3306/realestate?", p.getProperty("url"));
            p = Driver.parseUrlProperties("jdbc:seefusion:{jdbc:mysql://localhost:3306/realestate?};foo=bar");
            assertEquals("jdbc:mysql://localhost:3306/realestate?", p.getProperty("url"));
            assertEquals("bar", p.getProperty("foo"));
            p = Driver.parseUrlProperties("jdbc:seefusion:{jdbc:mysql://localhost:3306/realestate?}foo=bar");
            assertEquals("jdbc:mysql://localhost:3306/realestate?", p.getProperty("url"));
            assertEquals("bar", p.getProperty("foo"));
            p = Driver.parseUrlProperties(" jdbc:seefusion:jdbcwrapper:{jdbc:mysql://192.168.1.67:3306/probilling_production_dbo?allowMultiQueries=true}");
            assertEquals("jdbc:mysql://192.168.1.67:3306/probilling_production_dbo?allowMultiQueries=true", p.getProperty("url"));
            try {
                p = Driver.parseUrlProperties("jdbc:seefusion:{ugh");
                fail("Exception expected for jdbc:seefusion:{ugh");
            } catch(SQLException e) {
                //ignore
            }
            try {
                p = Driver.parseUrlProperties("jdbc:seefusion:ugh");
                assertEquals(null, p.getProperty("url"));
            } catch(SQLException e) {
                //ignore
            }
        } catch(Exception e) {
            fail(e.toString());
        }
        
    }
    
}
