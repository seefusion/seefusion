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
public class Query {

	public Date beginTime;
	public Date endTime;
	public String filterName;
	public long queryTime;
	public long queryNumRows;
	public String queryParams;
	public String queryText;
	public String stack;
	
	static final String SQL = "SELECT BeginTime, EndTime, FilterName, QueryTime, QueryNumRows, QueryParams, QueryText, Stack FROM Queries ";
	
	Query() {
		
	}
	
	Query(ResultSet rs) throws SQLException {
		beginTime = rs.getTimestamp(1);
		endTime = rs.getTimestamp(2);
		filterName = rs.getString(3);
		queryTime = rs.getLong(4);
		queryNumRows = rs.getLong(5);
		queryParams = rs.getString(6);
		queryText = rs.getString(7);
		stack = rs.getString(8);
	}
	
	static ResultSet findQueriesByBeginTime(Connection c, Date startDT, Date endDT) throws SQLException {
		PreparedStatement s = c.prepareStatement(SQL + "WHERE BeginTime BETWEEN ? and ?");
		s.setTimestamp(1, new Timestamp(startDT.getTime()));
		s.setTimestamp(2, new Timestamp(endDT.getTime()));
		return s.executeQuery();
	}
	
	
	
}
