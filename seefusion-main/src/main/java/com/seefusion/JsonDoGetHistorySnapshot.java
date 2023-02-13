package com.seefusion;

import java.io.IOException;

import com.seestack.SeeStack;

public class JsonDoGetHistorySnapshot extends JsonRequestHandler implements Cacheable {

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

	@Override
	public long getCacheableDurationMs() {
		return 60 * 60 * 1000;
	}

	@Override
	JSONObject doJson(HttpTalker talker) throws ErrorMessage {
		long timestamp = Long.parseLong(talker.getUrlParams().getProperty("timestamp"));
		HistorySnapshot hs = talker.getSeeFusion().getHistoryMinutes().getHistorySnapshots().get(timestamp);
		if(hs==null) {
			throw new ErrorMessage("Snapshot not found.");
		}
		Config config = talker.getSeeFusion().getConfig();
		SeeStack ss;
		try {
			ss = new SeeStack(config.getConfigDir(), config.getProperty("localPackages"));
		} catch (IOException e1) {
			throw new ErrorMessage("Unable to load SeeStack data.");
		}
		JSONObject ret = new JSONObject();
		JSONObject analysis;
		analysis = ss.process(new StackParserSeeFusion(hs.getStacks())).toJson();
		ret.put("seestack", analysis);
		ret.put("snapshot", hs.toJson());
		return ret;
	}

}
