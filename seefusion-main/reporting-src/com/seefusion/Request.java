/**
 * 
 */
package com.seefusion;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Daryl
 *
 */
public class Request {

	public Date beginTime;
	public Date endTime;
	public String filterName;
	public String serverName;
	public String requestURI;
	public String queryString;
	public String remoteIP;
	public long totalTimeMs;
	public long timeToFirstByteMs;
	public int numQueries;
	public long totalQueryTime;
	public long longestQueryTime;
	public long longestQueryNumRows;
	public String longestQueryParams;
	public String longestQueryText;
	
	static final String SQL =
		"SELECT beginTime" +
		", endTime" +
		", filterName" +
		", serverName" +
		", requestURI" +
		", urlQueryString" +
		", remoteIP" +
		", totalTimeMs" +
		", timeToFirstByteMs" +
		", numQueries" +
		", totalQueryTime" +
		", longestQueryTime" +
		", longestQueryNumRows" +
		", longestQueryParams" +
		", longestQueryText" +
		" FROM Pages ";
	
	Request() {
		
	}
	
	Request(ResultSet rs) throws SQLException {
		int n=1;
		beginTime = rs.getTimestamp(n++);
		endTime = rs.getTimestamp(n++);
		filterName = rs.getString(n++);
		serverName = rs.getString(n++);
		requestURI = rs.getString(n++);
		queryString = rs.getString(n++);
		remoteIP = rs.getString(n++);
		totalTimeMs = rs.getLong(n++);
		timeToFirstByteMs = rs.getLong(n++);
		numQueries = rs.getInt(n++);
		totalQueryTime = rs.getLong(n++);
		longestQueryTime = rs.getLong(n++);
		longestQueryNumRows = rs.getLong(n++);
		longestQueryParams = rs.getString(n++);
		longestQueryText = rs.getString(n++);
	}
		
	static ResultSet findByBeginTime(Connection c, Date startDT, Date endDT) throws SQLException {
		PreparedStatement s = c.prepareStatement(SQL + "WHERE beginTime BETWEEN ? and ?");
		s.setTimestamp(1, new Timestamp(startDT.getTime()));
		s.setTimestamp(2, new Timestamp(endDT.getTime()));
		return s.executeQuery();
	}

	/**
	 * @return the sQL
	 */
	public static String getSQL() {
		return SQL;
	}

	/**
	 * @return the beginTime
	 */
	public Date getBeginTime() {
		return this.beginTime;
	}

	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return this.endTime;
	}

	/**
	 * @return the filterName
	 */
	public String getFilterName() {
		return this.filterName;
	}

	/**
	 * @return the longestQueryNumRows
	 */
	public long getLongestQueryNumRows() {
		return this.longestQueryNumRows;
	}

	/**
	 * @return the longestQueryParams
	 */
	public String getLongestQueryParams() {
		return this.longestQueryParams;
	}

	/**
	 * @return the longestQueryText
	 */
	public String getLongestQueryText() {
		return this.longestQueryText;
	}

	/**
	 * @return the longestQueryTime
	 */
	public long getLongestQueryTime() {
		return this.longestQueryTime;
	}

	/**
	 * @return the numQueries
	 */
	public int getNumQueries() {
		return this.numQueries;
	}

	/**
	 * @return the queryString
	 */
	public String getQueryString() {
		return this.queryString;
	}

	/**
	 * @return the remoteIP
	 */
	public String getRemoteIP() {
		return this.remoteIP;
	}

	/**
	 * @return the requestURI
	 */
	public String getRequestURI() {
		return this.requestURI;
	}

	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return this.serverName;
	}

	/**
	 * @return the timeToFirstByteMs
	 */
	public long getTimeToFirstByteMs() {
		return this.timeToFirstByteMs;
	}

	/**
	 * @return the totalQueryTime
	 */
	public long getTotalQueryTime() {
		return this.totalQueryTime;
	}

	/**
	 * @return the totalTimeMs
	 */
	public long getTotalTimeMs() {
		return this.totalTimeMs;
	}
	
}
