/**
 * 
 */
package com.seefusion;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Daryl
 *
 */
@Ignore
public class PreparedStatementImplTest extends TestCase {
    
	static final String jdbcClass = "com.seefusion.Driver";
    static final String jdbcURL = "jdbc:seefusion:{jdbc:jtds:sqlserver://localhost/seefusion};driver=net.sourceforge.jtds.jdbc.Driver";
    static final String jdbcUser = "cfuser";
    static final String jdbcPassword = "cfuser";
    Connection c;
    Statement s;
    SeeFusion sf;
    RequestInfo pi;
    
    @Before
    public void setUp() throws Exception {
        Class.forName(jdbcClass);
        c = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
        
        Properties props = new Properties();
        props.setProperty("name", "inst");
        props.setProperty("listeners", "all:8998");
        props.setProperty("historySize", "80");
        sf = SeeFusion.getInstance();
        pi = sf.createRequest( "name", "RequestURI", "QueryString", "RemoteAddr", "GET", "", false);
    }
    
    @After
    public void tearDown() throws Exception {
    	if(c != null) {
    		c.close();
    	}
    	if(pi != null) {
    		pi.close();
    	}
    	if(sf != null) {
    		sf.shutdown();
    	}
    }
    
    @Test
    public void testExecuteUpdate() throws SQLException {
        PreparedStatement ps = c.prepareStatement("UPDATE Employees SET Lastname = ? WHERE EmployeeID = ?");
        ps.setString(1, "King");
        ps.setInt(2, 7);

        ps.executeUpdate();
        
        assertFalse(pi.getQueryInfo().isActive());
        
    }
    
    @Test
    public void testExecute() throws SQLException {
        PreparedStatement ps = c.prepareStatement("UPDATE Employees SET Lastname = ? WHERE EmployeeID = ?");
        ps.setString(1, "King");
        ps.setInt(2, 7);

        ps.execute();
        
        assertFalse(pi.getQueryInfo().isActive());
        
        ps.close();

        ps = c.prepareStatement("SELECT * FROM Employees WHERE EmployeeID = ?");
        ps.setInt(1, 7);

        ps.execute();
        
        assertTrue(pi.getQueryInfo().isActive());
        
        ps.close();
        
    }

}
