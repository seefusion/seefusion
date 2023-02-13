package com.seefusion;

import java.util.HashMap;
import java.util.TreeMap;

import com.seestack.SeeThread;

public class ThreadFingerprintCounts extends HashMap<String, ThreadFingerprintCount> {
	
	private static final long serialVersionUID = -5009266873467972402L;
	int count = 0;

	// you may again in the future try to make this extend ThreadCounts to reuse the add() method. Generic type hell!  Stay Back!
	void add(SeeThread thread) {
		count+=thread.getCount();
		String fingerprint = thread.getFingerprint();
		if(fingerprint!=null) { // ignore any traces that don't contain our code
			ThreadFingerprintCount tc = get(fingerprint);
			if(tc==null) {
				// not in map yet; create
				tc = new ThreadFingerprintCount(fingerprint);
				put(fingerprint, tc);
			}
			// count the thread
			tc.add(thread);
		}
	}
	
	// you may again in the future try to make this extend ThreadCounts to reuse the toJson() method. Generic type hell!  Stay Back!
	JSONArray toJson() {
		// sort by occurrence
		JSONArray arr = new JSONArray();
		TreeMap<Integer, ThreadFingerprintCount> tm = new TreeMap<Integer, ThreadFingerprintCount>();
		for(ThreadFingerprintCount tc : values()) {
			tm.put(100000000 - tc.getCount(), tc);
		}
		for(ThreadFingerprintCount tc : tm.values()) {
			arr.add(tc.toJson());
		}
		return arr;
	}

	int getCount() {
		return count;
	}

}
