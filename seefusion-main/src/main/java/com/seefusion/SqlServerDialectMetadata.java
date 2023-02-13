/*
 * ConnectionImplSqlServer.java
 *
 * Created on November 2, 2005, 11:23 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.seefusion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author Daryl
 */
class SqlServerDialectMetadata implements SqlDialectMetadata {
    
    /**
     * Gathers database-server-specific metadata
     * @param c Connection to retrieve driver-specific metadata from
     */
    public Properties getMetadata(java.sql.Connection c) throws SQLException {
    	Properties ret = new Properties();
        Statement s = null;
        ResultSet rs = null;
        try {
            s = c.createStatement();
            rs = s.executeQuery("SELECT @@SPID, @@SERVERNAME, DB_NAME(DB_ID())");
            if(rs.next()) {
	            ret.put("SPID", rs.getString(1));
	            ret.put("Server", rs.getString(2));
	            ret.put("DB", rs.getString(3));
            }
        }
        finally {
            if(rs != null) { try{ rs.close(); } catch(SQLException e) { /* ignore */ } }
            if( s != null) { try{  s.close(); } catch(SQLException e) { /* ignore */ } }
        }
        return ret;
    }
    
}
