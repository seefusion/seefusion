package com.seefusion;

public class JsonDoGetHistoryMinutes extends JsonRequestHandler implements Cacheable {
	
	public JSONObject doJson(HttpTalker talker)  {
		SeeFusion sf = talker.getSeeFusion();
		// push all available counters to client
		JSONObject ret = new JSONObject();
		JSONArray history = new JSONArray();
		CountersHistory historyMinutes = sf.getHistoryMinutes();
		
		for (Counters c : historyMinutes.getCounters()) {
			history.put(c.toJson());
		}
		ret.put("history", history);
		return ret;
	}

	@Override
    public long getCacheableDurationMs() {
	    return 20000;
    }

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}
}
