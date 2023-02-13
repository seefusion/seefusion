package com.seefusion;

public class JsonDoDeleteRule extends JsonRequestHandler {

	public JSONObject doJson(HttpTalker talker)  {
		JSONObject req = talker.getJsonData();
		SeeFusion sf = talker.getSeeFusion();
		sf.getActiveMonitoringRules().remove(req.getString("id"));
		return JsonDoGetMonitorConfig.getResponse(sf);
	}
	
	@Override
	Perm getPerm() {
		return new Perm(Perm.CONFIG);
	}


}
