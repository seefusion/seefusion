/**
 * 
 */
package com.seefusion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author Daryl
 * 
 */
class MonitorPluginDB implements MonitorPlugin {

	String driver;

	String url;

	String query;

	long warnTimeout;

	long failTimeout;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.MonitorPlugin#init(java.lang.String)
	 */
	public void init(String args) {
		Properties mArgs = RequestLine.parseProperties(args);
		driver = mArgs.getProperty("driver");
		Driver.loadDriver(driver);
		url = mArgs.getProperty("url");
		if (mArgs.containsKey("query")) {
			query = mArgs.getProperty("query");
		}
		else {
			query = "";
		}
		warnTimeout = Long.parseLong(mArgs.getProperty("warntimeout"));
		failTimeout = Long.parseLong(mArgs.getProperty("failtimeout"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.MonitorPlugin#doMonitor()
	 */
	public MonitorResult doMonitor() {
		int retCode = MonitorResult.STATUS_OK;
		String retString = "";
		Connection c = null;
		long startTick = System.currentTimeMillis();
		try {
			DriverManager.setLoginTimeout((int)failTimeout / 1000);
			c = DriverManager.getConnection(url);
			if (query.length() != 0) {
				Statement s = c.createStatement();
				try {
					ResultSet rs = s.executeQuery(query);
					rs.next();
					rs.close();
				}
				finally {
					s.close();
				}
			}
		}
		catch (SQLException e) {
			retCode = MonitorResult.STATUS_FAIL;
			retString = e.toString();
		}
		finally {
			if(c != null) {
				try {
					if(!c.isClosed()) {
						c.close();
					}
				}
				catch (SQLException e) {
					//ignore
				}
			}
			long elapsed = System.currentTimeMillis() - startTick;
			if(elapsed > failTimeout) {
				retCode = MonitorResult.STATUS_FAIL;
				retString = "Elapsed time of " + elapsed + "ms > fail timeout of " + failTimeout + "ms";
			}
			else if(elapsed > warnTimeout) {
				retCode = MonitorResult.STATUS_WARN;
				retString = "Elapsed time of " + elapsed + "ms > warn timeout of " + warnTimeout + "ms";
			}
		}

		return new MonitorResult(retCode, retString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.MonitorPlugin#destroy()
	 */
	public void destroy() {
		// nop
	}

}
