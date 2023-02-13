package com.seefusion;

public class JsonDoSaveConfigs extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker) throws ErrorMessage {
		JSONArray arr = talker.getPostDataArray();
		for(Object o : arr) {
			if(o instanceof JSONObject) {
				JSONObject req = (JSONObject)o;
				JsonDoSaveConfig.setProperty(req.getString("configitem"), req.get("value").toString(), talker);
			}
		}
		return JsonDoGetConfig.doJsonImpl(talker);
	}
	
	@Override
	Perm getPerm() {
		return new Perm(Perm.CONFIG);
	}


}
