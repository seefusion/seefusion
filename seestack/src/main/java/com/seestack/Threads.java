/**
 * 
 */
package com.seestack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.seefusion.JSONArray;
import com.seefusion.JSONObject;

/**
 * @author Daryl
 *
 */
public class Threads {

	private LinkedHashMap<Integer, LinkedList<SeeThread>> sortedThreads;
	private int threadCount;
	
	public Threads() {
		threadCount = 0;
		sortedThreads = new LinkedHashMap<Integer, LinkedList<SeeThread>>();
		for(int i=4; i >=-1; i--) {
			sortedThreads.put(i, new LinkedList<SeeThread>());
		}
	}
	
	/**
	 * @param curThread
	 */
	public void add(SeeThread curThread) {
		sortedThreads.get(curThread.getImportance().value()).add(curThread);
		threadCount++;
	}

	public LinkedList<SeeThread> getThreads() {
		LinkedList<SeeThread> ret = new LinkedList<SeeThread>();
		for(LinkedList<SeeThread> threads : sortedThreads.values()) {
			for(SeeThread st : threads) {
				ret.add(st);
			}
		}
		return ret;
	}

	public JSONObject toJson()  {
		JSONObject ret = new JSONObject();
		HashMap<String, Info> infos = new HashMap<String, Info>();
		JSONArray threadJson = new JSONArray();
		for(SeeThread thread : getThreads()) {
			thread.addInfoFrom(infos);
			threadJson.put(thread.toJson());
		}
		ret.put("threads", threadJson);
		JSONObject infosJson = new JSONObject();
		for(Iterator<Info> iter = infos.values().iterator(); iter.hasNext(); ) {
			Info info = iter.next();
			infosJson.put(info.getRef(), info.toJson());
		}
		ret.put("infos", infosJson);
		return ret;
	}

	int size() {
		return threadCount;
	}
	
}
