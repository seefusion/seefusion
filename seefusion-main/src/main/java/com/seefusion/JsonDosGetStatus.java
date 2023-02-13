package com.seefusion;


public class JsonDosGetStatus extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker) {
		return _doJson(talker.getSeeFusion());
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

	public static JSONObject _doJson(SeeFusion sf) {
		return sf.getDosManager().getStatusJson();
	}


}
