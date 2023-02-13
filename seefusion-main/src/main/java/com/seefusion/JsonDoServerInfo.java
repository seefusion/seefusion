package com.seefusion;

import java.util.Map.Entry;

class JsonDoServerInfo extends JsonRequestHandler implements Cacheable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.JsonRequest#doGet(com.seefusion.SocketTalker)
	 */
	@Override
	public JSONObject doJson(HttpTalker talker)  {
		JSONObject json = new JSONObject();
		
		// add some basic data
		json.put("timestamp", System.currentTimeMillis());
		String remoteIP = talker.getRemoteAddr();
		json.put("remoteIP", remoteIP);
		
		// add some seefusion config/license/etc info
		JSONObject sfJson = new JSONObject();
		SeeFusion sf = talker.getSeeFusion();
		RequestList requestList = sf.getMasterRequestList();
		long slowPageThreshold=requestList.getSlowPageThreshold();
		sfJson.put("slowPageThreshold", slowPageThreshold);
		long slowQueryThreshold=requestList.getSlowQueryThreshold();
		sfJson.put("slowQueryThreshold", slowQueryThreshold);
		String clickableIPURL = sf.clickableIPURL;
		sfJson.put("clickableIPURL", clickableIPURL==null ? "" : clickableIPURL);
		sfJson.put("clickableURLs", sf.clickableURLs);
		sfJson.put("totalPages", sf.getHistoryMinutes().totalPageCount);
		sfJson.put("totalQueries", sf.getHistoryMinutes().totalQueryCount);
		sfJson.put("displayThreadNames", sf.isDisplayThreadNames());
		sfJson.put("dosEnabled", sf.getDosManager().isEnabled());
		sfJson.put("displayRelativeTimes", sf.isDisplayRelativeTimes());
		
		json.put("seefusion",sfJson);
		
		// add some server/memory info
		long freeMemory = sf.getFreeMemory();
		long totalMemory = sf.getTotalMemory();
		long usedMemory = totalMemory - freeMemory;
		
		// add some server/memory info
		JSONObject serverJson = new JSONObject();
		serverJson.put("name", sf.getInstanceName());
		serverJson.put("maxRequests", sf.maxRequests);
		serverJson.put("currentRequests", sf.getMasterRequestList().getCurrentRequests().size());
		int count = 0;
		for(Entry<String, RequestInfo> foo : sf.getMasterRequestList().getCurrentRequests()) {
			QueryInfo qi = foo.getValue().getQueryInfo();
			if(qi != null && qi.isActive) {
				count++;
			}
		}
		serverJson.put("currentQueries", count);
		serverJson.put("uptime", Util.msFormat(SeeFusion.getUptime()));
		
		JSONObject memoryJson = new JSONObject();
		memoryJson.put("free", freeMemory);
		memoryJson.put("total", totalMemory);
		memoryJson.put("used", usedMemory);
		serverJson.put("memory", memoryJson);
		
		json.put("server", serverJson);
		
		CountersHistory c = sf.getHistoryMinutes();
		json.put("counters1", c.getCounters(1).toJson());
		json.put("counters10", c.getCounters(10).toJson());
		json.put("counters60", c.getCounters(60).toJson());
		
		return json;
	}

	@Override
    public long getCacheableDurationMs() {
	    // Cachable for up to 2.5sec
	    return 2500;
    }

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
