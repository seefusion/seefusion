package com.seefusion;

public class JsonDoLog extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker)  {
		JSONObject ret = new JSONObject();
		ret.put("log", SeeFusionHandler.getLog());
		return ret;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
