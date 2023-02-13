package com.seefusion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Incidents {
	
	/**
	 * 
	 * Gets a list of unique days where incidents occur from the database.
	 * 
	 * @param d Date within the month to search
	 * @param c Connection to use for query
	 * @return List<java.sql.Date>
	 * @throws SQLException
	 */
	public static List<java.sql.Date> getIncidentsForMonth(Date d, Connection c) throws SQLException {
		List<java.sql.Date> ret = new LinkedList<java.sql.Date>();

		Calendar startDate = Calendar.getInstance(), endDate = Calendar.getInstance();
		Calendar thisDate = Calendar.getInstance();
		thisDate.setTime(d);
		startDate.set(thisDate.get(Calendar.YEAR), thisDate.get(Calendar.MONTH), 1, 0, 0, 0);
		endDate.set(thisDate.get(Calendar.YEAR), thisDate.get(Calendar.MONTH), 1, 0, 0, 0);
		endDate.add(Calendar.MONTH, 1);
		
		java.sql.PreparedStatement s = c.prepareStatement("SELECT BeginTime FROM Incidents WHERE BeginTime BETWEEN ? AND ? ORDER BY BeginTime");
		s.setDate(1, new java.sql.Date(startDate.getTime().getTime()));
		s.setDate(2, new java.sql.Date(endDate.getTime().getTime()));
		ResultSet rs = s.executeQuery();
		
		java.sql.Date lastDate=null, rsDate;
		while(rs.next()) {
			rsDate = rs.getDate(1);
			if(!rsDate.equals(lastDate)) {
				ret.add(rsDate);
				lastDate = rsDate;
			}
		}
		
		return ret;
	}
	
}
