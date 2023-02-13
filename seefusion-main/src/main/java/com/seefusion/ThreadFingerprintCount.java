package com.seefusion;

import com.seestack.SeeThread;

class ThreadFingerprintCount {

	private ThreadCounts threadCounts = new ThreadCounts();
	private final String description;
	private int count = 0;

	ThreadFingerprintCount(String description) {
		this.description = description;
	}
	
	JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("description", this.getDescription());
		ret.put("count", threadCounts.getCount());
		ret.put("analysis", threadCounts.toJson());
		return ret;
	}

	void add(SeeThread thread) {
		count+=thread.getCount();
		threadCounts.add(thread);
	}

	String getDescription() {
		return description;
	}

	int getCount() {
		return count;
	}
	
	public ThreadCounts getThreadCounts() {
		return threadCounts;
	}
	
	@Override
	public String toString() {
		return "ThreadFingerPrintCount {"+description+", count="+count+", threadCounts=" + threadCounts.toJson();
	}
	
}
