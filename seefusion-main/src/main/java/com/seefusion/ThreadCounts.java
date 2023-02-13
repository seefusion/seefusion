package com.seefusion;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import com.seestack.SeeThread;

/**
 * Counts threads based on key
 * @author Daryl
 *
 */

public class ThreadCounts extends HashMap<Integer, ThreadCount> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3949527557609545536L;
	private int count = 0;

	public ThreadCount add(SeeThread thread) {
		count+=thread.getCount();
		Integer key = thread.hashCode();
		ThreadCount tc = get(key);
		if(tc==null) {
			// not in map yet; create
			tc = new ThreadCount();
			put(key, tc);
		}
		// count the thread
		tc.add(thread);
		return tc;
	}
	
	// you may again in the future try to make this extend ThreadCounts to reuse the toJson() method. Generic type hell!  Stay Back!
	public JSONArray toJson() {
		// sort by occurrence
		JSONArray arr = new JSONArray();
		TreeMap<Integer, LinkedList<ThreadCount>> tm = new TreeMap<Integer, LinkedList<ThreadCount>>();
		for(ThreadCount tc : values()) {
			Integer key = Integer.MAX_VALUE - tc.count;
			LinkedList<ThreadCount> list = tm.get(key);
			if(list == null) {
				list = new LinkedList<ThreadCount>();
				tm.put(key, list);
			}
			list.add(tc);
		}
		for(LinkedList<ThreadCount> list : tm.values()) {
			for(ThreadCount tc : list) {
				arr.add(tc.toJson());
			}
		}
		return arr;
	}

	public int getCount() {
		return count;
	}
	
}
