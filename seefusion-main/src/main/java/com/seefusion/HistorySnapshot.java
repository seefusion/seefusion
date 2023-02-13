package com.seefusion;

import java.util.LinkedList;
import java.util.Map.Entry;

class HistorySnapshot {

	private long timestamp;
	private LinkedList<Entry<String, RequestInfo>> requests;
	private JSONObject json = new JSONObject();
	private ThreadStacks stacks;

	HistorySnapshot(long snapshotTimestamp) {
		this.timestamp = snapshotTimestamp;
	}

	public HistorySnapshot(long now, SeeFusion sf) {
		this.timestamp = now;
		this.requests = sf.getMasterRequestList().getCurrentRequests();
		StackHelper stackHelper = StackHelper.getInstance();
		stacks = stackHelper.traceAll();
		json.put("timestamp", timestamp);
		JSONArray reqs = new JSONArray();
		for(Entry<String, RequestInfo> request : requests) {
			reqs.add(request.getValue().toJson());
		}
		json.put("requests", reqs);
		StringBuilder ret = new StringBuilder();
		for (ThreadStack stack : stacks.values()) {
			ret.append(stack.toString());
		}
		json.put("stacktrace", ret.toString());

	}

	long getSnapshotTimestamp() {
		return timestamp;
	}
	
	JSONObject toJson() {
		return json;
	}

	ThreadStacks getStacks() {
		return stacks;
	}


}
