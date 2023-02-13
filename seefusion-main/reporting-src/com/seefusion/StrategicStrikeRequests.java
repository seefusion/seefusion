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
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Daryl
 * 
 */
public class StrategicStrikeRequests {

	protected static final int MAX_SIZE = 2000;
	IRequestNameTranslator translator;

	public static Object[] getReport(String jdbcDriverName, String jdbcURL, String jdbcUser, String jdbcPassword,
			Date startDT, Date endDT, int startClampHour, int startClampMins, int endClampHour, int endClampMins, List<Pattern> patternList)
			throws SQLException
	{
		IRequestNameTranslator translator = new RequestNameTranslatorImpl(patternList);
		return getReport(jdbcDriverName, jdbcURL, jdbcUser, jdbcPassword, startDT, endDT, startClampHour, startClampMins, endClampHour, endClampMins, translator);
	}

	public static Object[] getReport(String jdbcDriverName, String jdbcURL, String jdbcUser, String jdbcPassword,
			Date startDT, Date endDT, int startClampHour, int startClampMins, int endClampHour, int endClampMins,
			IRequestNameTranslator translator)
			throws SQLException
	{
		try {
			Class.forName(jdbcDriverName);
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection c = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);
		return getReport(c, startDT, endDT, startClampHour, startClampMins, endClampHour, endClampMins, translator);
	}

	public static Object[] getReport(Connection c,
			Date startDT, Date endDT, int startClampHour, int startClampMins, int endClampHour, int endClampMins, List<Pattern> patternList)
			throws SQLException
	{
		IRequestNameTranslator translator = new RequestNameTranslatorImpl(patternList);
		return getReport(c, startDT, endDT, startClampHour, startClampMins, endClampHour, endClampMins, translator);
	}

	@SuppressWarnings("deprecation")
	public static Object[] getReport(Connection c, Date startDT, Date endDT, int startClampHour, int startClampMins,
			int endClampHour, int endClampMins, IRequestNameTranslator translator) throws SQLException {

		ResultSet rs = Request.findByBeginTime(c, new java.sql.Date(startDT.getTime()), new java.sql.Date(endDT.getTime()));

		LimitedLinkedHashMap<String, RequestTracker> stRequests = new LimitedLinkedHashMap<String, RequestTracker>(MAX_SIZE+1, MAX_SIZE);

		while (rs.next()) {
			Request req = new Request(rs);
			int hh = req.beginTime.getHours();
			int mm = req.beginTime.getMinutes();
			String requestKey = translator.translateRequestName(req.getRequestURI());
			if (((hh > startClampHour) || (hh == startClampHour && mm >= startClampMins))
					&& ((hh < endClampHour) || (hh == endClampHour && mm <= endClampMins))) {
				// record this page in main struct
				if (!stRequests.containsKey(requestKey)) {
					stRequests.put(requestKey, new RequestTracker(requestKey, req));
				}
				else {
					RequestTracker tracker = (RequestTracker) stRequests.get(requestKey);
					tracker.add(req);
				}
			}

		}

		Object[] qts = stRequests.values().toArray();
		Arrays.sort(qts);
		return qts;
	}

}
