package com.seefusion;


public class JsonDosGetConfig extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker) {
		return _doJson(talker.getSeeFusion());
	}
	
	static JSONObject _doJson(SeeFusion sf) {
		return sf.getDosManager().getConfigJson();
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}


}
