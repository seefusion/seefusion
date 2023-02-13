package com.seefusion;

class JsonDoAuth extends JsonRequestHandler {

	private static Perm lastPerm;

	@Override
	public JSONObject doJson(HttpTalker talker)  {
		return _doJson(talker);
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.NONE);
	}

	public static JSONObject _doJson(HttpTalker talker) {
		
		SeeFusion sf = talker.getSeeFusion();
		Perm userPerm;
		if(talker.hasPostData()) {
			String password = Password.safe(talker.getPostData().get("password").toString());
			userPerm = sf.getPerm(password);
		}
		else {
			userPerm = talker.getHttpRequest().getPerm();
		}
		// Logger.debug(userPerm.toString());
		// used by unit test
		lastPerm = userPerm;

		JSONObject ret = new JSONObject();
		ret.put("copyright", SeeFusionMain.COPYRIGHT);
		ret.put("version", "SeeFusion " + SeeFusion.getVersion());
		ret.put("buildNumber", "");
		ret.put("canKill", userPerm.has(Perm.KILL));
		ret.put("canRead", userPerm.has(Perm.LOGGEDIN));
		ret.put("canConfig", userPerm.has(Perm.CONFIG));
		JSONObject json = new JSONObject();
		json.put("seefusion",ret);
		if(userPerm.has(Perm.LOGGEDIN)) {
			String authToken = talker.getAuthTokenCache().createToken(userPerm).getToken();
			json.put("auth", authToken);
		}
		
		return json;
	}
	
	// used by unit test
	static Perm getLastPerm() {
		return lastPerm;
	}

}
