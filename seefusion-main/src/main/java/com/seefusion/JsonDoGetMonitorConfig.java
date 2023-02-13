package com.seefusion;

public class JsonDoGetMonitorConfig extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker)  {
		SeeFusion sf = talker.getSeeFusion();
		return getResponse(sf);
	}

	static JSONObject getResponse(SeeFusion sf) {
		ActiveMonitoringRules rules = sf.getActiveMonitoringRules();
		JSONObject ret = new JSONObject();
		ret.put("rules", rules.toJson());
		ret.put("rulecount", rules.size());
		ret.put("ruletypes", StabilityRuleType.toJson());
		return ret;
	}
	
	@Override
	Perm getPerm() {
		return new Perm(Perm.CONFIG);
	}

}
