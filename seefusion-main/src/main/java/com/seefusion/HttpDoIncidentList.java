/**
 * 
 */
package com.seefusion;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.Properties;

/**
 * @author Daryl
 *
 */
class HttpDoIncidentList extends HttpRequestHandler {

	/* (non-Javadoc)
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	public String doGet(HttpTalker talker) {
		SeeFusion sf = talker.getSeeFusion();
		DbLogger dbl = sf.getDbLogger();
		
		
		Properties urlParams = talker.getUrlParams();
		String sStartTimestamp = urlParams.getProperty("startTimestamp");
		String sEndTimestamp = urlParams.getProperty("endTimestamp");
		DateFormat df = DateFormat.getDateTimeInstance();
		Timestamp startTimestamp;
		Timestamp endTimestamp;
		
		try {
			startTimestamp = new Timestamp(df.parse(sStartTimestamp).getTime());
			endTimestamp = new Timestamp(df.parse(sEndTimestamp).getTime());
		}
		catch (ParseException e1) {
			startTimestamp = null;
			endTimestamp = null;
		}
		
		try {
			QueryResult qr;
			if(startTimestamp==null) {
				qr = dbl.doQuery("SELECT IncidentID, FilterName, BeginTime, EndTime, IncidentType, ThresholdType, ThresholdValue FROM Incidents ORDER BY IncidentID DESC", 30);
			} else {
				Object[] aParams = new Object[2];
				aParams[0] = startTimestamp;
				aParams[1] = endTimestamp;
				qr = dbl.doPreparedQuery("SELECT IncidentID, FilterName, BeginTime, EndTime, IncidentType, ThresholdType, ThresholdValue FROM Incidents WHERE BeginTime BETWEEEN ? AND ? ORDER BY IncidentID DESC", aParams, 50);
			}
			
			StringBuilder result = new StringBuilder(2000);
			result.append("<table border=1><tr><th>IncidentID</th><th>Server</th><th>Start</th><th>End</th><th>Type</th><th>Threshold</th><th>Value</th></tr>");
			for(Map<String, Object> row : qr) {
				result.append("<tr>");
				String incidentID = (String)row.get("incidentid");
				result.append("<td align=right><a href=\"incidentView?incidentID=").append(incidentID).append("\">").append(incidentID).append("</a></td>");
				result.append("<td align=right>").append(row.get("filtername")).append("</td>");
				result.append("<td align=right>").append(row.get("begintime")).append("</td>");
				result.append("<td align=right>").append(row.get("endtime")).append("</td>");
				result.append("<td align=right>").append(row.get("incidenttype")).append("</td>");
				result.append("<td align=right>").append(row.get("thresholdtype")).append("</td>");
				result.append("<td align=right>").append(row.get("thresholdvalue")).append("</td>");
				result.append("</tr>");
			}
			result.append("</table>");
			
			return result.toString();
			
		}
		catch (SQLException e) {
			talker.doMessage("Error excuting query: " + e.toString());
			return null;
		}
		
	}

	static Perm perm = new Perm(Perm.LOGGEDIN);
	@Override
	Perm getPerm() {
		return perm;
	}

}
