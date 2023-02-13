/*
 * OutputFormatter.java
 *
 * Created on November 21, 2004, 4:10 PM
 */

package com.seefusion;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class OutputFormatter {

	SeeFusion sf;

	public static final int FORMAT_HTML = 1;

	public static final int FORMAT_XML = 2;

	/** Creates a new instance of RequestListFormatter */
	private OutputFormatter() {
	}

	static StringBuilder appendHtmlTo(StringBuilder output, SeeFusion sf, RequestList requestList) {
		MessageFormatFactory messageFormats = sf.getMessageFormatFactory();
		
		CountersHistory counters = sf.getHistoryMinutes();
		Counters[] stats = counters.getCounterIntervals();
		long freeMemory = sf.getFreeMemory();
		long curMemory = sf.getTotalMemory();
		long availMemory = curMemory - freeMemory;

		Object[] params = new Object[20];

		// css & header
		output.append(messageFormats.getMessage("pageheader"));
		
		// server info row
		params[0] = sf.getInstanceName();
		params[1] = Util.dateFormat(System.currentTimeMillis());
		params[2] = new Long(availMemory / 1048576);
		params[3] = new Long(curMemory / 1048576);
		params[4] = Util.msFormat(SeeFusion.getUptime());
		messageFormats.format("contentheaderpre", params, output);
		
		// requests row
		params[0] = "Request";
		params[1] = new Long(stats[0].getAvgPageTime());
		params[2] = new Float(stats[1].getPagesPerSecond());
		params[3] = new Long(stats[1].getAvgPageTime());
		params[4] = new Float(stats[2].getPagesPerSecond());
		params[5] = new Long(stats[2].getAvgPageTime());
		params[6] = new Long(counters.getTotalPageCount());
		params[7] = new Integer(stats[0].getPageCount());
		messageFormats.format("contentheaderavg", params, output);
		
		// queries row
		params[0] = "Query";
		params[1] = new Long(stats[0].getAvgQueryTime());
		params[2] = new Float(stats[1].getQueriesPerSecond());
		params[3] = new Long(stats[1].getAvgQueryTime());
		params[4] = new Float(stats[2].getQueriesPerSecond());
		params[5] = new Long(stats[2].getAvgQueryTime());
		params[6] = new Long(counters.getTotalQueryCount());
		params[7] = new Integer(stats[0].getQueryCount());
		messageFormats.format("contentheaderavg", params, output);

		// end of that table
		output.append(messageFormats.getMessage("contentheaderpost"));
		
		params[0]="Active";
		messageFormats.format("sectionheader", params, output);
		if(requestList.currentRequests.isEmpty()) {
			messageFormats.format("sectionempty", params, output); // "No Active Requests"
		}
		else {
			output.append(messageFormats.getMessage("activereqheader"));
			boolean altRow = true;
			for(Map.Entry<String, RequestInfo> ri : requestList.getCurrentRequests()) {
				ri.getValue().formatEmailHtmlRow(sf, messageFormats, "activereqdetail", true, (altRow = !altRow), output);
			}
			output.append(messageFormats.getMessage("reqfooter"));
		}
		output.append(messageFormats.getMessage("sectionfooter"));
		
		List<RequestInfo> requests = requestList.getRecentRequests();
		params[0]="Recent";
		messageFormats.format("sectionheader", params, output);
		if(requests.isEmpty()) {
			messageFormats.format("sectionempty", params, output); // "No Active Requests"
		}
		else {
			output.append(messageFormats.getMessage("reqheader"));
			boolean altRow = true;
			for(RequestInfo ri : requests) {
				ri.formatEmailHtmlRow(sf, messageFormats, "reqdetail", false, (altRow = !altRow), output);
			}
			output.append(messageFormats.getMessage("reqfooter"));
		}
		output.append(messageFormats.getMessage("sectionfooter"));
		
		return output;
	}
	
	static String dashboardDetailHtml(SeeFusion sf, RequestList requestList) {
		MessageFormatFactory messageFormats = sf.getMessageFormatFactory();
		StringBuilder output = new StringBuilder();
		Object[] params = new Object[20];
		LinkedList<Map.Entry<String, RequestInfo>> currentRequests = requestList.getCurrentRequests();
		RequestInfo requestInfo = null;
		long pageElapsedTime = 0;
		if (!currentRequests.isEmpty()) {
			Map.Entry<String, RequestInfo> entry = currentRequests.getFirst();
			requestInfo = entry.getValue();
			pageElapsedTime = requestInfo.getElapsedTime();
		}
		Counters[] stats = sf.getHistoryMinutes().getCounterIntervals();
		params[0] = "#rowstyle#";

		// Up/Slow/Down rules:
		int currentRequestsSize = currentRequests.size();
		float prev10SecPPS = stats[1].getPagesPerSecond();
		float prev10SecAvgPageTime = stats[1].getAvgPageTime();
		float prev60SecPPS = stats[2].getPagesPerSecond();
		long freeMemory = sf.getFreeMemory();
		long availMemory = sf.getTotalMemory();

		params[1] = ""; // status color and name
		params[2] = messageFormats.getMessage("ok");
		long slowPageThreshold = sf.getMasterRequestList().getSlowPageThreshold();
		
		if (freeMemory < (availMemory / 10)) {
			params[1] = "bgcolor=yellow";
			params[2] = messageFormats.getMessage("mem");
		}
		else if (freeMemory < (availMemory / 100)) {
			params[1] = "bgcolor=red";
			params[2] = messageFormats.getMessage("mem");
		}
		else if (currentRequestsSize < sf.getDashboardMinSlowRequests()) {
			// nop
		}
		else if (prev10SecPPS < 0.2
				&& prev60SecPPS < 0.2
				&& pageElapsedTime > 60000
				&& pageElapsedTime > slowPageThreshold) {
			// down?
			params[1] = "bgcolor=red";
			params[2] = messageFormats.getMessage("down");
		}
		else if (prev10SecPPS < 0.2 && pageElapsedTime > 60000 && pageElapsedTime > slowPageThreshold) {
			// one page very slow
			params[1] = "bgcolor=red";
			params[2] = messageFormats.getMessage("slow");
		}
		else if (prev10SecPPS < 0.2 && pageElapsedTime > 10000 && pageElapsedTime > slowPageThreshold) {
			// one page rather slow
			params[1] = "bgcolor=yellow";
			params[2] = messageFormats.getMessage("slow");
		}
		else if (prev10SecAvgPageTime > (slowPageThreshold * 2) && pageElapsedTime > (slowPageThreshold * 2)) {
			params[1] = "bgcolor=red";
			params[2] = messageFormats.getMessage("slow");
		}
		else if (prev10SecAvgPageTime > slowPageThreshold && pageElapsedTime > slowPageThreshold) {
			params[1] = "bgcolor=yellow";
			params[2] = messageFormats.getMessage("slow");
		}
		String browserURL = sf.getBrowserURL();
		String instanceName = sf.getInstanceName();
		if (browserURL != null) {
			if(browserURL.endsWith("/")) {
				params[3] = "<a href=\"" + browserURL + "html\" target=\"sf" + instanceName + "\">" + instanceName + "</a>";
			} else {
				params[3] = "<a href=\"" + browserURL + "/html\" target=\"sf" + instanceName + "\">" + instanceName + "</a>";
			}
		}
		else if (!instanceName.equalsIgnoreCase("SeeFusion")) {
			params[3] = "<a href=\"http://"
					+ instanceName
					+ ":8999/\" target=\"sf"
					+ instanceName
					+ "\">"
					+ instanceName
					+ "</a>";
		}
		else {
			params[3] = instanceName;
		}
		params[4] = new Integer(currentRequestsSize);
		params[13] = new Integer(sf.getMaxRequests());
		params[5] = new Float(stats[1].getPagesPerSecond());
		params[6] = new Long(stats[1].getAvgPageTime());
		params[7] = new Float(stats[2].getPagesPerSecond());
		params[8] = new Long(stats[2].getAvgPageTime());
		params[9] = new Long((availMemory - freeMemory) / 1048576);
		params[10] = new Long(availMemory / 1048576);
		params[11] = Util.msFormat(SeeFusion.getUptime());
		params[12] = sf.getInstanceName();
		messageFormats.format("dashboarddetailserver", params, output);
		if (requestInfo == null) {
			messageFormats.format("dashboarddetailnopage", null, output);
		}
		else {
			output.append(requestInfo.getInfoDashboard(messageFormats, "dashboarddetailpage", true));
		}

		return output.toString();
	}

	static SimpleXml dashboardDetailXml(SeeFusion sf, RequestList requestList) {
		SimpleXml ret = new SimpleXml("dashboardinfo");
		MessageFormatFactory messageFormats = sf.getMessageFormatFactory();
		LinkedList<Map.Entry<String, RequestInfo>> currentRequests = requestList.getCurrentRequests();
		RequestInfo requestInfo = null;
		long pageElapsedTime = 0;
		if (!currentRequests.isEmpty()) {
			Map.Entry<String, RequestInfo> entry = currentRequests.getFirst();
			requestInfo = entry.getValue();
			pageElapsedTime = requestInfo.getElapsedTime();
		}
		Counters[] stats = sf.getHistoryMinutes().getCounterIntervals();
		
		// Up/Slow/Down rules:
		int currentRequestsSize = currentRequests.size();
		float prev10SecPPS = stats[1].getPagesPerSecond();
		float prev10SecAvgPageTime = stats[1].getAvgPageTime();
		float prev60SecPPS = stats[2].getPagesPerSecond();
		long freeMemory = sf.getFreeMemory();
		long availMemory = sf.getTotalMemory();
		
		String color = ""; // status color and name
		String status = messageFormats.getMessage("ok");
		long slowPageThreshold = sf.getMasterRequestList().getSlowPageThreshold();
		
		if (freeMemory < (availMemory / 10)) {
			color = "bgcolor=yellow";
			status = messageFormats.getMessage("mem");
		}
		else if (freeMemory < (availMemory / 100)) {
			color = "bgcolor=red";
			status = messageFormats.getMessage("mem");
		}
		else if (currentRequestsSize < sf.getDashboardMinSlowRequests()) {
			// nop
		}
		else if (prev10SecPPS < 0.2
				&& prev60SecPPS < 0.2
				&& pageElapsedTime > 60000
				&& pageElapsedTime > slowPageThreshold) {
			// down?
			color = "bgcolor=red";
			status = messageFormats.getMessage("down");
		}
		else if (prev10SecPPS < 0.2 && pageElapsedTime > 60000 && pageElapsedTime > slowPageThreshold) {
			// one page very slow
			color = "bgcolor=red";
			status = messageFormats.getMessage("slow");
		}
		else if (prev10SecPPS < 0.2 && pageElapsedTime > 10000 && pageElapsedTime > slowPageThreshold) {
			// one page rather slow
			color = "bgcolor=yellow";
			status = messageFormats.getMessage("slow");
		}
		else if (prev10SecAvgPageTime > (slowPageThreshold * 2) && pageElapsedTime > (slowPageThreshold * 2)) {
			color = "bgcolor=red";
			status = messageFormats.getMessage("slow");
		}
		else if (prev10SecAvgPageTime > slowPageThreshold && pageElapsedTime > slowPageThreshold) {
			color = "bgcolor=yellow";
			status = messageFormats.getMessage("slow");
		}
		String browserURL = sf.getBrowserURL();
		String instanceName = sf.getInstanceName();
		if (browserURL != null) {
			browserURL = "<a href=\"" + browserURL + "\" target=\"sf" + instanceName + "\">" + instanceName + "</a>";
		}
		else if (!instanceName.equalsIgnoreCase("SeeFusion")) {
			browserURL = "<a href=\"http://"
				+ instanceName
				+ ":8999/\" target=\"sf"
				+ instanceName
				+ "\">"
				+ instanceName
				+ "</a>";
		}
		else {
			browserURL = instanceName;
		}
		ret.addTag("name", sf.getInstanceName());
		ret.addTag("status", status);
		ret.addTag("statuscolor", color);
		ret.addTag("serverurl", browserURL);
		ret.addTag("numcurrentrequests", currentRequestsSize);
		ret.addTag("maxcurrentrequests", sf.getMaxRequests());
		ret.addTag("pps10s", stats[1].getPagesPerSecond());
		ret.addTag("pagetime10s", stats[1].getAvgPageTime());
		ret.addTag("pps60s", stats[2].getPagesPerSecond());
		ret.addTag("pagetime60s",stats[2].getAvgPageTime());
		ret.addTag("curmemory", (availMemory - freeMemory) / 1048576);
		ret.addTag("maxmemory", availMemory / 1048576);
		ret.addTag("uptime", Util.msFormat(SeeFusion.getUptime()));
		ret.addTag("slowpagethreshold", slowPageThreshold);
		if (requestInfo == null) {
			RequestInfo.addNullInfoDashboardXml(ret);
		}
		else {
			requestInfo.addInfoDashboardXml(ret, true);
		}
		return ret;
	}

}
