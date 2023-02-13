/**
 * 
 */
package com.seefusion;

import java.util.LinkedList;
import java.util.Map;

/**
 * @author Daryl
 * 
 */
class JsonDoDashboard extends JsonRequestHandler implements Cacheable {

	static String ret;

	static long currentsec = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	JSONObject doJson(HttpTalker talker) {
		SeeFusion sf = talker.getSeeFusion();
		RequestList requestList = sf.getMasterRequestList();
		JSONObject ret = new JSONObject();
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
		String status = "ok";
		long slowPageThreshold = sf.getMasterRequestList().getSlowPageThreshold();
		long slowQryThreshold = sf.getMasterRequestList().getSlowQueryThreshold();
		
		if (freeMemory < (availMemory / 10)) {
			color = "bgcolor=yellow";
			status = "MEM";
		}
		else if (freeMemory < (availMemory / 100)) {
			color = "bgcolor=red";
			status = "MEM";
		}
		else if (currentRequestsSize < sf.getDashboardMinSlowRequests()) {
			// nop
		}
		else if (prev60SecPPS < 0.2
				&& pageElapsedTime > 60000
				&& pageElapsedTime > slowPageThreshold) {
			// down?
			color = "bgcolor=red";
			status = "DOWN";
		}
		else if (prev10SecPPS < 0.2 && pageElapsedTime > 60000 && pageElapsedTime > slowPageThreshold) {
			// one page very slow
			color = "bgcolor=red";
			status = "SLOW";
		}
		else if (prev10SecPPS < 0.2 && pageElapsedTime > 10000 && pageElapsedTime > slowPageThreshold) {
			// one page rather slow
			color = "bgcolor=yellow";
			status = "SLOW";
		}
		else if (prev10SecAvgPageTime > (slowPageThreshold * 2) && pageElapsedTime > (slowPageThreshold * 2)) {
			color = "bgcolor=red";
			status = "SLOW";
		}
		else if (prev10SecAvgPageTime > slowPageThreshold && pageElapsedTime > slowPageThreshold) {
			color = "bgcolor=yellow";
			status = "SLOW";
		}
		ret.put("name", sf.getInstanceName());
		ret.put("browserurl", sf.getBrowserURL());
		ret.put("status", status);
		ret.put("statuscolor", color);
		ret.put("numcurrentrequests", currentRequestsSize);
		ret.put("maxcurrentrequests", sf.getMaxRequests());
		ret.put("pps", stats[2].getPagesPerSecond());
		ret.put("qps", stats[2].getQueriesPerSecond());
		ret.put("avgpagetime",stats[2].getAvgPageTime());
		ret.put("avgqrytime",stats[2].getAvgQueryTime());
		JSONObject memory = new JSONObject();
		memory.put("used", (availMemory - freeMemory) / 1048576); 
		memory.put("total", availMemory / 1048576);
		ret.put("memory", memory);
		ret.put("uptime", Util.msFormat(SeeFusion.getUptime()));
		ret.put("slowpagethreshold", slowPageThreshold);
		ret.put("slowqrythreshold", slowQryThreshold);
		if (requestInfo != null) {
			requestInfo.addInfoDashboardJson(ret, true);
		}
		return ret;
	}

	@Override
	Perm getPerm() {
		return new Perm();
	}

	@Override
	public long getCacheableDurationMs() {
		return 5000;
	}

}
