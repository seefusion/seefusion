package com.seefusion;

class JsonDoLogout extends JsonRequestHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.JsonRequest#doGet(com.seefusion.SocketTalker)
	 */
	public JSONObject doJson(HttpTalker talker)  {
		talker.getHttpRequest().invalidateToken();
		talker.addHTTPHeader("Set-Cookie", "sfpassword=; Path=/; expires=now");
		return JsonDoAuth._doJson(talker);
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.NONE);
	}

}
