package com.seefusion;

public class JsonDoSaveRule extends JsonRequestHandler {

	public JSONObject doJson(HttpTalker talker)  {
		JSONObject req = talker.getJsonData();
		SeeFusion sf = talker.getSeeFusion();
		sf.getActiveMonitoringRules().setFromJson(req);
		return JsonDoGetMonitorConfig.getResponse(sf);
	}
	
	@Override
	Perm getPerm() {
		return new Perm(Perm.CONFIG);
	}


}
