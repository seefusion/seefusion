package com.seefusion;

public class JsonDoGetDashboard extends JsonRequestHandler  {
	
	@Override
	public JSONObject doJson(HttpTalker talker) {
		// push all available counters to client
		SeeFusion sf = talker.getSeeFusion();
		Dashboard dash = new Dashboard(sf); 
		JSONObject ret = new JSONObject();
		try {
			String mode = talker.getUrlParams().getProperty("mode");
			boolean problemsOnly = "problems".equals(mode); 
			//Logger.log("mode: " + mode + " : " + problemsOnly);
			ret.put("servers", dash.doGetJson(sf.dashboardServersList, problemsOnly));
		} catch (JSONException e) {
			throw new ErrorMessage(e);
		}
		return ret;
	}

	//@Override
    public long getCacheableDurationMs() {
	    return 5000;
    }

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}
}
