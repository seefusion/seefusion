/**
 * 
 */
package com.seefusion;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daryl
 * 
 */
class ActiveMonitoringRules extends HashMap<String, ActiveMonitoringRule> implements Observer<RequestList> {

	private static final Logger LOG = Logger.getLogger(ActiveMonitoringRules.class.getName());
	
	private static final long serialVersionUID = 746141958175920650L;
	SeeFusion sf;

	ActiveMonitoringRules(SeeFusion sf) {
		super();
		this.sf = sf;
	}

	public ActiveMonitoringRules() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.Observer#update(com.seefusion.Subject)
	 */
	@Override
	public void update(RequestList o) {
		if (o instanceof RequestList) {
			RequestList requestList = o;
			if (!super.isEmpty()) {
				for (ActiveMonitoringRule rule : super.values()) {
					try {
						rule.process(sf, requestList);
					}
					catch (Throwable t) {
						logException("Exception processing rule " + rule.getName(), t);
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#remove(java.lang.Object)
	 */
	public ActiveMonitoringRule remove(String key) {
		ActiveMonitoringRule ret = super.remove(key);
		if(ret != null) {
			save();
		}
		return ret;
	}

	public void loadFromXML(Config config) {
		// get all rule names into temporary hashmap
		SimpleXml rules = config.getXml().get("rules");
		synchronized (this) {
			if (rules != null) {
				this.clear();
				// iterate over "new" rules and add/modify existing rules
				for (SimpleXml ruleXml : rules) {
					String thisRuleID = ruleXml.getProperty("id");
					try {
						ActiveMonitoringRule thisRule = get(thisRuleID);
						if (thisRule == null) {
							thisRule = ActiveMonitoringRule.getInstance(ruleXml.getProperty("triggerType"));
							this.put(thisRuleID, thisRule);
						}
						thisRule.loadFrom(ruleXml);
					}
					catch (InstantiationException e) {
						logException("ActiveMonitoringRules.loadFromXML: Unable to load rule " + thisRuleID, e);
					}
				}
			}
		}
	}

	void logException(String s, Throwable t) {
		LOG.log(Level.SEVERE, s, t);
	}

	public SimpleXml toXml() {
		SimpleXml ret = new SimpleXml("rules");
		for (ActiveMonitoringRule rule : this.values()) {
			ret.add(rule.toXml());
		}
		return ret;
	}

	public JSONArray toJson() {
		JSONArray ret = new JSONArray();
		synchronized(this) {
			for (ActiveMonitoringRule rule : this.values()) {
				ret.add(rule.toJson());
			}
		}
		return ret;
	}

	public void setFromJson(JSONObject json) {
		String thisRuleID;
		if(json.has("id")) {
			thisRuleID = json.getString("id");
		}
		else {
			thisRuleID = UUID.randomUUID().toString();
			json.put("id", thisRuleID);
		}
		try {
			ActiveMonitoringRule thisRule = this.get(thisRuleID);
			if (thisRule == null) {
				thisRule = ActiveMonitoringRule.getInstance(json.getString("triggerType"));
				thisRule.loadFrom(json);
				put(thisRuleID, thisRule);
			}
			else {
				thisRule.loadFrom(json);
			}
			save();
		}
		catch (InstantiationException e) {
			logException("Unable to load rule " + thisRuleID, e);
		}
	}
	
	public void save() {
		Config config = sf.getConfig();
		config.setXML(this.toXml());
		config.write();
	}

}
