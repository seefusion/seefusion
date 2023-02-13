/**
 * 
 */
package com.seefusion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author Daryl
 *
 */
class MySQLDialectMetadata implements SqlDialectMetadata {

	@Override
	public Properties getMetadata(Connection c) throws SQLException {
		Properties ret = new Properties();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = c.createStatement();
			rs = s.executeQuery("SELECT CONNECTION_ID(), DATABASE()");
			if(rs.next()) {
				ret.put("PID", rs.getString(1));
				ret.put("DB", rs.getString(2));
			}
		}
		finally {
			if (rs != null) {
				try {
					rs.close();
				}
				catch (SQLException e) { /* ignore */
				}
			}
			if (s != null) {
				try {
					s.close();
				}
				catch (SQLException e) { /* ignore */
				}
			}
		}
		return ret;
	}

}
