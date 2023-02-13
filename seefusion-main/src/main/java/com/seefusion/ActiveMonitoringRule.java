/**
 * 
 */
package com.seefusion;

import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * @author Daryl
 * 
 */
abstract class ActiveMonitoringRule {
	
	private static final Logger LOG = Logger.getLogger(ActiveMonitoringRule.class.getName());

	// configuration vars
	protected String id;
	
	private String name;

	protected long triggerLimit;

	protected int delay;

	protected long delayMs;

	protected Integer notifyDisableMin;

	protected Long notifyDisableMs;

	protected int sleep;

	protected long sleepMs;

	private LinkedList<String> excludes;

	protected boolean isEnabled;

	protected boolean isKillResponse;

	protected boolean isNotifyResponse;

	protected String smtpFrom;

	protected String smtpTo;

	protected String smtpSubject;

	// rule tracking vars

	protected boolean isServerRule = true;

	protected boolean isTripped = false;
	
	protected long lastNotify = 0;

	protected long sleepUntil = 0;

	// server rules only
	protected long firstNoticed = 0;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + delay;
		result = prime * result + (int) (delayMs ^ (delayMs >>> 32));
		result = prime * result + ((excludes == null) ? 0 : excludes.hashCode());
		result = prime * result + (int) (firstNoticed ^ (firstNoticed >>> 32));
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isEnabled ? 1231 : 1237);
		result = prime * result + (isKillResponse ? 1231 : 1237);
		result = prime * result + (isNotifyResponse ? 1231 : 1237);
		result = prime * result + (isServerRule ? 1231 : 1237);
		result = prime * result + (isTripped ? 1231 : 1237);
		result = prime * result + (int) (lastNotify ^ (lastNotify >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((notifyDisableMin == null) ? 0 : notifyDisableMin.hashCode());
		result = prime * result + ((notifyDisableMs == null) ? 0 : notifyDisableMs.hashCode());
		result = prime * result + sleep;
		result = prime * result + (int) (sleepMs ^ (sleepMs >>> 32));
		result = prime * result + (int) (sleepUntil ^ (sleepUntil >>> 32));
		result = prime * result + ((smtpFrom == null) ? 0 : smtpFrom.hashCode());
		result = prime * result + ((smtpSubject == null) ? 0 : smtpSubject.hashCode());
		result = prime * result + ((smtpTo == null) ? 0 : smtpTo.hashCode());
		result = prime * result + (int) (triggerLimit ^ (triggerLimit >>> 32));
		result = prime * result + ((getTriggerType() == null) ? 0 : getTriggerType().hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActiveMonitoringRule other = (ActiveMonitoringRule) obj;
		if (delay != other.delay)
			return false;
		if (delayMs != other.delayMs)
			return false;
		if (excludes == null) {
			if (other.excludes != null)
				return false;
		} else if (!excludes.equals(other.excludes))
			return false;
		if (firstNoticed != other.firstNoticed)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isEnabled != other.isEnabled)
			return false;
		if (isKillResponse != other.isKillResponse)
			return false;
		if (isNotifyResponse != other.isNotifyResponse)
			return false;
		if (isServerRule != other.isServerRule)
			return false;
		if (isTripped != other.isTripped)
			return false;
		if (lastNotify != other.lastNotify)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (notifyDisableMin == null) {
			if (other.notifyDisableMin != null)
				return false;
		} else if (!notifyDisableMin.equals(other.notifyDisableMin))
			return false;
		if (notifyDisableMs == null) {
			if (other.notifyDisableMs != null)
				return false;
		} else if (!notifyDisableMs.equals(other.notifyDisableMs))
			return false;
		if (sleep != other.sleep)
			return false;
		if (sleepMs != other.sleepMs)
			return false;
		if (sleepUntil != other.sleepUntil)
			return false;
		if (smtpFrom == null) {
			if (other.smtpFrom != null)
				return false;
		} else if (!smtpFrom.equals(other.smtpFrom))
			return false;
		if (smtpSubject == null) {
			if (other.smtpSubject != null)
				return false;
		} else if (!smtpSubject.equals(other.smtpSubject))
			return false;
		if (smtpTo == null) {
			if (other.smtpTo != null)
				return false;
		} else if (!smtpTo.equals(other.smtpTo))
			return false;
		if (triggerLimit != other.triggerLimit)
			return false;
		if (getTriggerType() != other.getTriggerType())
			return false;
		return true;
	}
	
	
	
	// null-safe string compare
	boolean sEquals(String s1, String s2) {
		if(s1==null && s2==null) {
			return true;
		}
		if (s1==null) {
			return false;
		}
		return s1.equals(s2);
	}
	
	void loadFrom(SimpleXml xml) {
		this.id = xml.getProperty("id", "");
		if(id.equals("")) {
			id = UUID.randomUUID().toString();
		}
		this.setName(xml.getProperty("name"));
		this.triggerLimit = Util.parseLong(xml.getProperty("triggerLimit"), 0);
		this.setExcludes(new LinkedList<String>());
		String s;
		for(SimpleXml exclude : xml.get("excludeURLs")) {
			if((s = exclude.getSimpleValue()) != null) {
				this.getExcludes().add(s);
			}
		}
		this.isEnabled = Util.parseYesNoParam(xml.getProperty("isEnabled", "false"));
		this.isKillResponse = Util.parseYesNoParam(xml.getProperty("isKillResponse", "false"));
		this.isNotifyResponse = Util.parseYesNoParam(xml.getProperty("isNotifyResponse", "false"));
		this.smtpFrom = xml.getProperty("smtpFrom", "");
		this.smtpTo = xml.getProperty("smtpTo", "");
		this.smtpSubject = xml.getProperty("smtpSubject", "");

		this.delay = Util.parseInt(xml.getProperty("delay"), 0);
		this.delayMs = this.delay * 1000;
		this.sleep = Util.parseInt(xml.getProperty("sleep"), 0);
		this.sleepMs = this.sleep * 1000;
		this.notifyDisableMin = Math.max(1, Util.parseInt(xml.getProperty("notifyDisableMin"), 1));
		this.notifyDisableMs = notifyDisableMin * 60000L;

		if (this.getTriggerType().ruleContext == 'r') {
			isServerRule = false;
		}
	}
	
	void loadFrom(JSONObject json) {
		if(json.has("id")) {
			this.id = json.getString("id");
		}
		else {
			id = UUID.randomUUID().toString();
		}
		this.setName(json.getString("name"));
		this.triggerLimit = Util.parseLong(json.get("triggerLimit").toString(), 0);
		JSONArray excludeURLs = json.getJSONArray("excludeURLs");
		this.setExcludes(new LinkedList<String>());
		for(Object o : excludeURLs) {
			this.getExcludes().add(o.toString());
		}
		this.isEnabled = json.getBoolean("isEnabled");
		this.isKillResponse = json.getBoolean("isKillResponse");
		this.isNotifyResponse = json.getBoolean("isNotifyResponse");
		this.smtpFrom = json.optString("smtpFrom", "");
		this.smtpTo = json.optString("smtpTo", "");
		this.smtpSubject = json.optString("smtpSubject", "");

		this.delay = Util.parseInt(json.get("delay").toString(), 0);
		this.delayMs = this.delay * 1000;
		this.sleep = Util.parseInt(json.get("sleep").toString(), 0);
		this.sleepMs = this.sleep * 1000;
		this.notifyDisableMin = json.optInt("notifyDisableMin", 0);
		this.notifyDisableMs = notifyDisableMin * 60000L;
		this.isServerRule = (this.getTriggerType().ruleContext == 's');
	}

	/*
	 * {label:"Server:Active Requests", data:"sactivereq", units:"Requests"}
	 * ,{label:"Server:Memory Used %", data:"smemorypct", units:"%"}
	 * ,{label:"Server:1Min Avg Page Time", data:"s1mpagetime", units:"ms"}
	 * ,{label:"Server:1Min Avg Query Time", data:"s1mquerytime", units:"ms"}
	 * ,{label:"Request:Page Time", data:"rpagetime", units:"ms"}
	 * ,{label:"Request:Query Time", data:"rquerytime", units:"ms"}
	 */
	void process(SeeFusion sf, RequestList requestList) {
		if (isEnabled) {
			if (isServerRule) {
				processServerRule(sf, requestList);
			}
			else {
				processRequestsRule(sf, requestList);
			}
		}
	}

	void processServerRule(SeeFusion sf, RequestList requestList) {
		long now = System.currentTimeMillis();
		
		// check for a killed page that needs killstoppin'
		RequestInfo req = requestList.getOldestActiveRequest();
		if(req != null) {
			Object oState = req.getRuleState(this.getName());
			if (oState != null) {
				LOG.fine(this.getName() + " rule state: " + oState.toString());
				if (oState instanceof String) {
					String state = (String) oState;
					if (state.startsWith("doKill")) {
						LOG.fine("StabilityRule " + this.getName() + ": Killing request " + req.toString() + ": " + state.substring(6));
						req.kill(state.substring(6));
						req.setRuleState(this.getName(), "kill" + state.substring(6));
						return;
					}
					if (state.startsWith("kill")) {
						LOG.fine("StabilityRule " + this.getName() + ": KillStop request " + req.toString() + ": " + state.substring(4));
						req.killStop(state.substring(4));
						//req.setRuleState(this.name, "killstop");
						return;
					}
					if ("killstop".equals(state)) {
						// nop
						return;
					}
				}
			}
		}
		
		// check sleep
		if (sleepUntil != 0) {
			if (now < sleepUntil) {
				LOG.fine(getName() + " sleeping...");
				return;
			}
			else {
				sleepUntil = 0;
			}
		}
		
		boolean shouldKill = true;
		// check url filter
		if(req != null) {
			String reqString = req.getRequestString().toLowerCase(); 
			for (String exclude : getExcludes()) {
				if (reqString.indexOf(exclude) != -1) {
					shouldKill = true;
				}
			}
		}
		
		// check condition
		String conditionString = checkCondition(sf, null);
		if (conditionString == null) {
			firstNoticed = 0;
			isTripped = false;
			return;
		}

		// check delay
		if (this.delayMs != 0) {
			if (firstNoticed == 0) {
				firstNoticed = now;
				return;
			}
			if ((now - firstNoticed) < delayMs) {
				LOG.fine(getName() + " delaying...");
				// not yet
				return;
			}
		}
		
		// //////////////////////
		// do response action //
		// //////////////////////

		// set or obey isTripped
		if(isTripped) {
			// ignore
			return;
		} else {
			isTripped = true;
		}

		boolean doNotify = this.isNotifyResponse;
		// check rule notify interval
		if (doNotify && this.notifyDisableMs != 0) {
			doNotify = this.lastNotify == 0 || (now - this.lastNotify) > this.notifyDisableMs;
		}

		// sleep
		if (sleepMs != 0) {
			LOG.fine(getName() + " sleeping for " + sleep + " sec");
			this.sleepUntil = now + sleepMs;
		}
		// log incident
		StringBuilder buf = new StringBuilder(200);
		buf.append("Logged");
		if (this.isKillResponse) {
			if(shouldKill) {
				buf.append(", Killed");
			}
			else {
				buf.append(", NOT Killed (exclusion rule)");
			}
		}
		if (doNotify) buf.append(", Notified ").append(this.smtpTo);
		String actionTaken = buf.toString();

		LOG.info("Rule " + this.getName() + " activated because: " + conditionString + "; response is " + actionTaken);
		
		logIncident(sf, now, actionTaken);

		if (doNotify) {
			// do notify (before kill)
			this.lastNotify = now;
			Properties params = new Properties();
			params.put("\\{name\\}", this.getName());
			params.put("\\{reason\\}", conditionString);
			params.put("\\{action\\}", actionTaken);
			if(req==null) {
				params.put("\\{uri\\}", "No active request.");
				params.put("\\{query\\}", "No active query.");
			} else {
				if(req.getQueryString() != null && req.getQueryString().length() > 0) {
					params.put("\\{uri\\}", req.getRequestURI() + "?" + req.getQueryString() + " from " + req.getRemoteIP());
				}
				else {
					params.put("\\{uri\\}", req.getRequestURI() + " from " + req.getRemoteIP());
				}
				QueryInfo qi = req.getQueryInfo();
				if (qi != null && qi.isActive()) {
					params.put("\\{query\\}", qi.getQueryText()
							+ "\r\nTime: "
							+ qi.getElapsedTime()
							+ "ms,  Rows: "
							+ qi.getResultCount());
				}
				else {
					params.put("\\{query\\}", "No active query.");
				}
			}
			doNotify(sf, params);
		}

		// kill
		if (this.isKillResponse && shouldKill && req != null) {
			// delay kill by 1s
			req.setRuleState(this.getName(), "doKill"
					+ "SeeFusion terminated request: "
					+ conditionString
					+ " (rule \""
					+ this.getName()
					+ "\")");
		}

	}

	String logIncident(SeeFusion sf, long now, String actionTaken) {
		DbLogger logger = sf.getDbLogger();
		Incident incident = new Incident();
		incident.setBeginTime(firstNoticed == 0 ? now : firstNoticed);
		incident.setEndTime(now);
		incident.setIncidentType(this.getName());
		incident.setThresholdType(this.getTriggerType().toString());
		incident.setThresholdValue(Long.toString(this.triggerLimit));
		incident.setActionTaken(actionTaken);
		logger.log(incident);
		return incident.getId();
	}

	void processRequestsRule(SeeFusion sf, RequestList requestList) {
		LinkedList<Map.Entry<String, RequestInfo>> reqList = requestList.getCurrentRequests();
		RequestInfo req;
		for (Map.Entry<String, RequestInfo> entry : reqList) {
			req = entry.getValue();
			processRequestRule(sf, req);
		}
	}

	void processRequestRule(SeeFusion sf, RequestInfo req) {
		LOG.fine("Processing request rule "
				+ getName()
				+ " against "
				+ req.getURI());
		long now = System.currentTimeMillis();

		// check current rule state for this request
		Object oState = req.getRuleState(this.getName());
		if (oState != null) {
			LOG.fine(this.getName() + " rule state: " + oState.toString());
			if (oState instanceof String) {
				String state = (String) oState;
				// already processed
				if ("ignore".equals(state)) {
					return;
				}
				if (state.startsWith("doKill")) {
					LOG.fine("StabilityRule " + this.getName() + ": Killing request " + req.toString() + ": " + state.substring(6));
					req.kill(state.substring(6));
					req.setRuleState(this.getName(), "kill" + state.substring(6));
					return;
				}
				if (state.startsWith("kill")) {
					LOG.fine("StabilityRule " + this.getName() + ": KillStop request " + req.toString() + ": " + state.substring(4));
					req.killStop(state.substring(4));
					//req.setRuleState(this.name, "killstop");
					return;
				}
				if ("killstop".equals(state)) {
					// nop
					return;
				}
			}
			else if (oState instanceof Long) {
				firstNoticed = ((Long) oState).longValue();
			}
		}

		// check sleep
		if (sleepUntil != 0 && now < sleepUntil) {
			return;
		}

		// this may be loaded via getRuleState
		long firstNoticed = 0;

		// check url filter
		String reqString = req.getRequestString().toLowerCase(); 
		for (String exclude : getExcludes()) {
			if (reqString.indexOf(exclude) != -1) {
				req.setRuleState(this.getName(), "ignore");
				return;
			}
		}

		// check condition
		String conditionString = checkCondition(sf, req);
		if (conditionString == null) {
			req.removeRuleState(this.getName());
			return;
		}

		// sleep
		if (sleepMs != 0) {
			this.sleepUntil = now + sleepMs;
		}

		// check delay
		if (this.delay != 0) {
			if (firstNoticed == 0) {
				firstNoticed = now;
				req.setRuleState(this.getName(), new Long(firstNoticed));
				return;
			}
			if ((now - firstNoticed) < delay) {
				// not yet
				return;
			}
		}

		// do response action

		// log request
		boolean doNotify = this.isNotifyResponse;
		// check rule notify interval
		if (doNotify && this.notifyDisableMs != 0) {
			doNotify = this.lastNotify == 0 || (now - this.lastNotify) > this.notifyDisableMs;
		}
		StringBuilder buf = new StringBuilder(200);
		buf.append("Logged");
		if (this.isKillResponse) buf.append(", Killed");
		if (doNotify) buf.append(", Notified ").append(this.smtpTo);
		String actionTaken = buf.toString();

		LOG.info("Rule " + this.getName() + " activated because: " + conditionString + "; response is " + actionTaken);

		logIncident(sf, now, actionTaken);

		if (this.isKillResponse) {
			// delay kill by 1s
			req.setRuleState(this.getName(), "doKill"
					+ "SeeFusion terminated request: "
					+ conditionString
					+ " (rule \""
					+ this.getName()
					+ "\")");
		}
		else {
			req.setRuleState(this.getName(), "ignore");
		}

		if (doNotify) {
			this.lastNotify = now;
			/*
			 * You can include the following variables in either the Subject or
			 * Body of the message: {name} : The name of the rule. {reason} :
			 * The reason the rule was hit (for example, "Current Request Count
			 * 15 >= limit of 15") {action} : The action(s) performed. {uri} :
			 * The request that triggered this rule (or the oldest active
			 * request for Server rules) {query} : Information about the
			 * currently active query in the [oldest] active page.
			 */
			Properties params = new Properties();
			params.put("\\{name\\}", this.getName());
			params.put("\\{reason\\}", conditionString);
			params.put("\\{action\\}", actionTaken);
			params.put("\\{uri\\}", req.getRequestString() + " from " + req.getRemoteIP());
			QueryInfo qi = req.getQueryInfo();
			if (qi != null && qi.isActive()) {
				params.put("\\{query\\}", qi.getQueryText()
						+ "\r\nTime: "
						+ qi.getElapsedTime()
						+ "ms,  Rows: "
						+ qi.getResultCount());
			}
			else {
				params.put("\\{query\\}", "No active query.");
			}

			doNotify(sf, params);
		}
	}

	abstract String checkCondition(SeeFusion sf, RequestInfo req);

	abstract String getDescription();

	// Factory.
	static ActiveMonitoringRule getInstance(String type) throws InstantiationException {
		StabilityRuleType ruleType = StabilityRuleType.parseType(type);
		ActiveMonitoringRule newRule = ruleType.getInstance();
		return newRule;
	}

	void doNotify(SeeFusion sf, Properties params) {
		// replace params
		String subject = this.smtpSubject;
		for (Map.Entry<Object, Object> entry : params.entrySet()) {
			subject = subject.replaceAll((String) entry.getKey(), (String) entry.getValue());
		}
		// do notify
		SmtpMessage msg = new SmtpMessage();
		msg.setSmtpFrom(this.smtpFrom);
		msg.setSmtpTo(this.smtpTo);
		msg.setContentType("text/html; charset=\"UTF-8\"");
		msg.setSmtpSubject(subject);

		msg.setSmtpBody(buildMessageBody(sf));
		sf.getSmtpSender().send(msg);

	}

	String buildMessageBody(SeeFusion sf) {
		final RequestList requestList = sf.getMasterRequestList();
		final StringBuilder result = new StringBuilder(5000);
		OutputFormatter.appendHtmlTo(result, sf, requestList);
		result.append("<pre>");
		try {
			StackHelper stackHelper = StackHelper.getInstance();
			ThreadStacks threads = stackHelper.traceAllFilteredExcept(Thread.currentThread().getName(), "", sf.getActiveThreadNames());
			for (ThreadStack stack : threads.values()) {
				result.append(stack.toString());
			}
		}
		catch (Throwable t) {
			result.append("Stack trace not available: ").append(t.toString());
		}
		result.append("</pre>\r\n");
		result.append("</body>\r\n</html>");
		return result.toString();
	}


	JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("id", this.id);
		ret.put("name", this.getName());
		ret.put("triggerType", this.getTriggerType());
		ret.put("triggerTypeString", this.getTriggerType().getName());
		ret.put("triggerLimit", this.triggerLimit);
		ret.put("excludeURLs", new JSONArray(getExcludes()));
		ret.put("isEnabled", this.isEnabled);
		ret.put("isKillResponse", this.isKillResponse);
		ret.put("isNotifyResponse", this.isNotifyResponse);
		ret.put("smtpFrom", this.smtpFrom);
		ret.put("smtpTo", this.smtpTo);
		ret.put("smtpSubject", this.smtpSubject);
		ret.put("delay", this.delay);
		ret.put("sleep", this.sleep);
		ret.put("notifyDisableMin", this.notifyDisableMin);
		ret.put("smtpSubject", this.smtpSubject);
		return ret;
	}

	SimpleXml toXml() {
		SimpleXml ret = new SimpleXml("rule");
		ret.setProperty("id", this.id);
		ret.setProperty("name", this.getName());
		ret.setProperty("triggerType", this.getTriggerType());
		ret.setProperty("triggerLimit", this.triggerLimit);
		SimpleXml excludesX = new SimpleXml("excludeURLs");
		for(String exclude : getExcludes()) {
			excludesX.addTag("exclude", exclude);
		}
		ret.add(excludesX);
		ret.setProperty("isEnabled", this.isEnabled);
		ret.setProperty("isKillResponse", this.isKillResponse);
		ret.setProperty("isNotifyResponse", this.isNotifyResponse);
		ret.setProperty("smtpFrom", this.smtpFrom);
		ret.setProperty("smtpTo", this.smtpTo);
		ret.setProperty("smtpSubject", this.smtpSubject);
		ret.setProperty("delay", this.delay);
		ret.setProperty("sleep", this.sleep);
		ret.setProperty("notifyDisableMin", this.notifyDisableMin);
		ret.setProperty("smtpSubject", this.smtpSubject);
		return ret;
	}

	public LinkedList<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(LinkedList<String> excludes) {
		this.excludes = excludes;
	}

	abstract StabilityRuleType getTriggerType();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
