package com.seefusion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.seestack.Info;
import com.seestack.SeeStack;
import com.seestack.SeeThread;
import com.seestack.Threads;

class ProfileAnalysis {

	private HashMap<String, Info> infos = new HashMap<String, Info>();
	private ThreadFingerprintCounts threadCounts = new ThreadFingerprintCounts();
	private JSONObject json;
	
	ProfileAnalysis(SeeFusion sf, Profile profile) throws IOException {
		this(sf.getConfig().getConfigDir(), sf.getConfig().getProperty("localPackages"), profile);
	}
	
	ProfileAnalysis(String configDir, String localPackages, Profile profile) throws IOException {
		SeeStack ss = new SeeStack(configDir, localPackages);
		Threads threads = ss.process(new StackParserSeeFusion(profile.threadStacks));
		for(SeeThread thread : threads.getThreads()) {
			thread.addInfoFrom(infos);
			threadCounts.add(thread);
		}
	}
	
	/**
	 * for instantiating from DB-stored json string
	 * @param json
	 */
	ProfileAnalysis(JSONObject json) {
		this.json = json;
	}
	
	ThreadFingerprintCounts getThreadCounts() {
		return threadCounts;
	}
	
	JSONObject toJson() {
		if(json != null) {
			return json;
		}
		JSONObject ret = new JSONObject();
		ret.put("analysis", threadCounts.toJson());
		ret.put("count", threadCounts.getCount());
		JSONObject infoJson = new JSONObject();
		for(Map.Entry<String, Info> info : infos.entrySet()) {
			infoJson.put(info.getKey(), info.getValue().toJson());
		}
		ret.put("infos", infoJson);
		return ret;
	}

}
