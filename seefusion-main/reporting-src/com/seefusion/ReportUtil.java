/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 * 
 */
public class ReportUtil {

	// like ColdFusion's getToken() function, but zero-based
	static String getToken(String s, int pos) {
		String[] foo = ltrim(s).split("[\\s]+");
		if(pos > foo.length) {
			return "";
		}
		else {
			return foo[pos];
		}
	}

	static String ltrim(String s) {
		int pos = 0;
		int len = s.length();
		while (pos < len) {
			switch (s.charAt(pos)) {
			case ' ':
			case '\t':
			case '\r':
			case '\n':
				pos++;
				break;
			default:
				return s.substring(pos);
			}
		}
		return s;
	}

	static String getQuerySummary(Query q) {
		String querySummary = "";
		String queryType = "";
		int pos = 0;
		// attempt to determine query root - the part of the query that doesn't
		// change
		if (q.queryText == null || "".equals(q.queryText)) {
			querySummary = "(no query)";
		}
		else if (q.queryParams == null || "".equals(q.queryParams)) {
			queryType = getToken(ltrim(q.queryText), 0);
			if (queryType.equalsIgnoreCase("SELECT") || queryType.equalsIgnoreCase("DELETE")) {
				pos = q.queryText.toUpperCase().indexOf("WHERE");
				if (pos != -1) {
					querySummary = q.queryText.substring(0, pos + 5);
				}
				else {
					querySummary = q.queryText;
				}
			}
			else if (queryType.equalsIgnoreCase("UPDATE")) {
				pos = q.queryText.toUpperCase().indexOf("SET");
				if (pos != -1) {
					querySummary = q.queryText.substring(0, pos + 3);
				}
				else {
					querySummary = q.queryText;
				}
			}
			else if (queryType.equalsIgnoreCase("INSERT")) {
				if (getToken(q.queryText, 1).equalsIgnoreCase("INTO")) {
					querySummary = "INSERT INTO " + getToken(q.queryText, 2);
				}
				else {
					querySummary = "INSERT " + getToken(q.queryText, 1);
				}
			}
			else if (queryType.equalsIgnoreCase("EXEC")) {
				querySummary = getToken(q.queryText, 0) + " " + getToken(q.queryText, 1);
			}
			else if (queryType.equalsIgnoreCase("{CALL")) {
				querySummary = q.queryText.substring(0, q.queryText.indexOf('}') + 1);
			}
			else if (queryType.equalsIgnoreCase("{?")) {
				querySummary = q.queryText.substring(0, q.queryText.indexOf('}') + 1);
			}
			else {
				querySummary = q.queryText;
			}
		}
		else { // parameterized query
			querySummary = q.queryText;
		}
		return querySummary;
	}
}
