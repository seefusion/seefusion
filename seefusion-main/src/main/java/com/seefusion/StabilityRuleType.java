package com.seefusion;

public enum StabilityRuleType {
	
	ACTIVEREQ('s', "Active Request Count")
	,MEMORYPCT('s', "Memory Utilization (%)")
	,PAGETIME1M('s', "Page Time (1min Avg)")
	,QUERYTIME1M('s', "Query Time (1min Avg)")
	,PAGETIME('r', "Page Time (for request)")
	,QUERYTIME('r', "Query Time (for request)");
	
	char ruleContext;
	String name;
	StabilityRuleType(char ruleContext, String name) {
		this.ruleContext = ruleContext;
		this.name = name;
	}
	
	static StabilityRuleType parseType(String ruleType) {
		ruleType = ruleType.trim().toLowerCase();
		if (ruleType.equals("sactivereq") || ruleType.equals("activereq")) {
			return ACTIVEREQ;
		}
		else if (ruleType.equals("smemorypct") || ruleType.equals("memorypct")) {
			return MEMORYPCT;
		}
		else if (ruleType.equals("s1mpagetime") || ruleType.equals("pagetime1m")) {
			return PAGETIME1M;
		}
		else if (ruleType.equals("s1mquerytime") || ruleType.equals("querytime1m")) {
			return QUERYTIME1M;
		}
		else if (ruleType.equals("rpagetime") || ruleType.equals("pagetime")) {
			return PAGETIME;
		}
		else if (ruleType.equals("rquerytime") || ruleType.equals("querytime")) {
			return QUERYTIME;
		}
		else {
			throw new RuntimeException("Unknown rule type: " + ruleType);
		}
	}
	
	ActiveMonitoringRule getInstance() {
		switch(this) {
		case ACTIVEREQ: return new RuleActiveRequests();
		case MEMORYPCT: return new RuleMemPct();
		case PAGETIME1M: return new RulePageTime1m();
		case QUERYTIME1M: return new RuleQueryTime1m();
		case PAGETIME: return new RulePageTime();
		case QUERYTIME: return new RuleQueryTime();
		}
		return null;
	}
	
	static JSONArray toJson() {
		JSONArray ret = new JSONArray();
		for(StabilityRuleType t : StabilityRuleType.values()) {
			JSONObject foo = new JSONObject();
			foo.put("label", t.name);
			foo.put("value", t.name());
			ret.add(foo);
		}
		return ret;
	}

	public String getName() {
		return name;
	}
}
