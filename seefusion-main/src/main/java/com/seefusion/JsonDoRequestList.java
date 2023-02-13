package com.seefusion;

import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

public class JsonDoRequestList extends JsonRequestHandler {
	
	@Override
	public JSONObject doJson(HttpTalker talker)  {
		Properties urlParams = talker.getUrlParams();
		String type = urlParams.getProperty("type");

		if(type == null){
			throw new ErrorMessage("You must pass a type [active, recent, slow]");
		}
		if (!talker.getSeeFusion().isGloballyEnabled()) {
			throw new ErrorMessage("SeeFusion is Not Enabled");
		}

		return doGet(talker.getSeeFusion().getMasterRequestList(), type);
	}

	public static JSONObject doGet(RequestList requestList, String type)  {
		
		JSONObject ret = new JSONObject();
		
		if(type.equalsIgnoreCase("active")){
			LinkedList<Map.Entry<String, RequestInfo>> currentRequests = requestList.getCurrentRequests();
			JSONArray currentRequestArray = new JSONArray();
			for(Map.Entry<String, RequestInfo> entry : currentRequests) {
				RequestInfo requestInfo = entry.getValue();
				currentRequestArray.put(requestInfo.toJson());
			}
			ret.put("pages", currentRequestArray);
		}
		
		else if(type.equalsIgnoreCase("recent")) {
			LinkedList<RequestInfo> recentRequests = requestList.getRecentRequests();
			JSONArray recentRequestArray = new JSONArray();
			for(RequestInfo requestInfo : recentRequests) {
				recentRequestArray.put(requestInfo.toJson());
			}
			ret.put("pages", recentRequestArray);
		}
		
		else if(type.equalsIgnoreCase("slow")) {
			LinkedList<RequestInfo> slowRequests = requestList.getSlowRequests();
			JSONArray slowRequestArray = new JSONArray();
			for(RequestInfo requestInfo : slowRequests) {
				slowRequestArray.put(requestInfo.toJson());
			}
			ret.put("pages", slowRequestArray);
		}
		
		return ret;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}