package com.seefusion;

public class JsonDoGetActiveProfileStatus extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker) {
		SeeFusion sf = talker.getSeeFusion();
		JSONObject ret = new JSONObject();
		ret.put("active", sf.getProfiler().toJson());
		return ret;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
