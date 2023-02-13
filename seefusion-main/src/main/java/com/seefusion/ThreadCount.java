package com.seefusion;

import java.util.HashMap;
import java.util.TreeMap;

import com.seestack.SeeThread;

class ThreadCount {

	String description;
	int count = 0;
	HashMap<Integer, SeeThread> threads = new HashMap<Integer, SeeThread>();

	JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("count", count);
		ret.put("description", description);
		JSONArray arr = new JSONArray();
		// sort by threadcount
		TreeMap<Integer, SeeThread> tm = new TreeMap<Integer, SeeThread>();
		for(SeeThread t : threads.values()) {
			tm.put(100000000 - t.getCount(), t);
		}
		for(SeeThread t :tm.values()) {
			arr.add(t.toJson());
		}
		ret.put("threads", arr);
		return ret;
	}

	@Override
	public String toString() {
		return toJson().toString();
	}
	
	public void add(SeeThread thread) {
		count+=thread.getCount();
		description = thread.getFingerprint();
		int hashCode = thread.hashCode();
		SeeThread st = threads.get(hashCode);
		if(st==null) {
			st = thread;
			threads.put(hashCode, thread);
		}
		st.incrementCount();
	}

}
