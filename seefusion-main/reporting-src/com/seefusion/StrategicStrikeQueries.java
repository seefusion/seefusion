/**
 * 
 */
package com.seefusion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Daryl
 * 
 */
public class StrategicStrikeQueries {

	public static Object[] getReport(String jdbcDriverName, String jdbcURL, String jdbcUser, String jdbcPassword,
			Date startDT, Date endDT, int startClampHour, int startClampMins, int endClampHour, int endClampMins)
			throws SQLException {
		try {
			Class.forName(jdbcDriverName);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection c = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
		return getReport(c, startDT, endDT, startClampHour, startClampMins, endClampHour, endClampMins);
	}

	@SuppressWarnings("deprecation")
	public static Object[] getReport(Connection c, Date startDT, Date endDT, int startClampHour, int startClampMins,
			int endClampHour, int endClampMins) throws SQLException {

		ResultSet rs = Query.findQueriesByBeginTime(c, new java.sql.Date(startDT.getTime()), new java.sql.Date(endDT
				.getTime()));

		HashMap<String, QueryTracker> stQueries = new HashMap<String, QueryTracker>();

		while (rs.next()) {
			Query q = new Query(rs);
			int hh = q.beginTime.getHours();
			int mm = q.beginTime.getMinutes();
			if (((hh > startClampHour) || (hh == startClampHour && mm >= startClampMins))
					&& ((hh < endClampHour) || (hh == endClampHour && mm <= endClampMins))) {
				// determine query root
				String querySummary = ReportUtil.getQuerySummary(q);

				// record this page in main struct
				if (!stQueries.containsKey(querySummary)) {
					stQueries.put(querySummary, new QueryTracker(querySummary, q));
				}
				else {
					QueryTracker qt = stQueries.get(querySummary);
					qt.add(q);
				}
			}

		}

		Object[] qts = stQueries.values().toArray();
		Arrays.sort(qts);
		return qts;
	}

}
