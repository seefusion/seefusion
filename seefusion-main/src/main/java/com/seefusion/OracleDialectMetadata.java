package com.seefusion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class OracleDialectMetadata implements SqlDialectMetadata {

	public Properties getMetadata(Connection c) throws SQLException {
		Properties ret = new Properties();
		Statement s = null;
		ResultSet rs = null;
		try {
			s = c.createStatement();
			rs = s.executeQuery("SELECT SYS_CONTEXT(\'USERENV\', \'SID\'), SYS_CONTEXT(\'USERENV\', \'INSTANCE_NAME\'), SYS_CONTEXT(\'USERENV\', \'DB_NAME\'), SYS_CONTEXT(\'USERENV\', \'CURRENT_SCHEMA\') FROM DUAL");
			if(rs.next()) {
				ret.put("SID", rs.getString(1));
				ret.put("Instance", rs.getString(2));
				ret.put("DB", rs.getString(3));
				ret.put("Schema", rs.getString(4));
			}
		}
		catch (SQLException e) {
			// uh.... ignore i guess
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
