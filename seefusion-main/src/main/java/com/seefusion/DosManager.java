package com.seefusion;

import java.util.logging.Logger;

public class DosManager {

	private static final Logger LOG = Logger.getLogger(DosManager.class.getName());
	
	private boolean enabled = false;
	private int requestLimit = 0;
	private SeeFusion sf;
	private IPList permBlockList = new IPList();
	private TimedIPList tempBlockList = new TimedIPList();
	private IPList excludeList = new IPList();
	private Action action;
	private long banDurationMin;
	
	public DosManager(SeeFusion seeFusion) {
		this.sf = seeFusion;
	}

	boolean blocks(String ip) {
		LOG.fine("enabled: " + enabled);
		if(!enabled) {
			return false;
		}
		// check existing blocks
		if(permBlockList.contains(ip)) {
			LOG.fine("ip in perm block list: " + ip);
			return true;
		}
		if(tempBlockList.contains(ip)) {
			LOG.fine("ip in temp block list: " + ip);
			return true;
		}
		// new block?
		LOG.fine("request limit: " + requestLimit);
		if (requestLimit > 0) {
			RequestList list = sf.getMasterRequestList();
			int count = list.getIPCount(ip);
			LOG.fine("current count: " + count);
			if(count >= requestLimit) {
				if(excludes(ip)) {
					LOG.fine("ip excluded" + ip);
				}
				else {
					if(action == Action.PERMBAN) {
						LOG.fine("added to perm ban list" + ip);
						addBlockedIP(ip);
					}
					else if(action == Action.TEMPBAN) {
						LOG.fine("added to temp ban list" + ip);
						tempBlockList.add(ip, new Long(System.currentTimeMillis() + (60000L * banDurationMin)));
					}
					return true;
				}
			}
		}
		return false;
	}
	
	boolean excludes(String ip) {
		return excludeList.contains(ip);
	}

	void loadFromXML(SimpleXml xmlConfig) {
		// get all rule names into temporary hashmap
		SimpleXml config = xmlConfig.get("dosmanager");
		if(config==null) {
			config = new SimpleXml("dosmanager");
		}
		String s = config.getProperty("enabled");
		this.enabled = s!=null && Boolean.parseBoolean(s);
		s = config.getProperty("action");
		this.action = s == null ? Action.REJECT : Action.valueOf(s.toUpperCase());
		s = config.getProperty("requestlimit");
		this.requestLimit = s==null ? 6 : Integer.parseInt(s);
		s = config.getProperty("bandurationmin");
		this.banDurationMin = s==null ? 5 : Integer.parseInt(s);
		permBlockList = new IPList();
		SimpleXml entries = config.get("block");
		if(entries != null) {
			for(SimpleXml entry : entries) {
				permBlockList.add(entry.getSimpleValue());
			}
		}
		excludeList = new IPList();
		entries = config.get("excludes");
		if(entries != null) {
			for(SimpleXml entry : entries) {
				excludeList.add(entry.getSimpleValue());
			}
		}
	}
	
	void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	SimpleXml toXml() {
		SimpleXml ret = new SimpleXml("dosmanager");
		ret.setProperty("enabled", this.enabled);
		ret.setProperty("action", this.action);
		ret.setProperty("requestlimit", this.requestLimit);
		ret.setProperty("bandurationmin", this.banDurationMin);
		SimpleXml list = new SimpleXml("excludes");
		for (String ip : this.excludeList) {
			list.addTag("ip", ip);
		}
		ret.add(list);
		list = new SimpleXml("block");
		LOG.fine(this.permBlockList.toString());
		for (String ip : this.permBlockList) {
			list.addTag("ip", ip);
		}
		ret.add(list);
		return ret;
	}

	void addBlockedIP(String ip) {
		boolean save = false;
		synchronized(permBlockList) {
			if(!permBlockList.contains(ip)) {
				save = permBlockList.add(ip);
			}
		}
		if(save) {
			save();
		}
	}
	
	void addExcludedIP(String ip) {
		synchronized(excludeList) {
			if(!excludeList.contains(ip)) {
				excludeList.add(ip);
			}
		}
		removeBlock(ip);
	}
	
	IPList getPermBlockList() {
		return permBlockList;
	}
	
	TimedIPList getTempBlockList() {
		return tempBlockList;
	}
	
	IPList getExcludedIPList() {
		return excludeList;
	}
	
	boolean isEnabled() {
		return enabled;
	}

	JSONObject getConfigJson() {
		JSONObject ret = new JSONObject();
		ret.put("enabled", this.enabled);
		ret.put("action", this.action.toString().toLowerCase());
		ret.put("requestlimit", this.requestLimit);
		ret.put("bandurationmin", this.banDurationMin);
		return ret;
	}

	JSONObject getStatusJson() {
		JSONObject ret = new JSONObject();
		ret.put("excludes", excludeList.getJsonArray());
		ret.put("tempblocklist", tempBlockList.getJsonArray());
		ret.put("permblocklist", permBlockList.getJsonArray());
		return ret;
	}

	public void setConfigJson(JSONObject jsonObject) {
		this.enabled = jsonObject.getBoolean("enabled");
		this.action = Action.valueOf(jsonObject.getString("action").toUpperCase());
		this.requestLimit = jsonObject.getInt("requestlimit");
		this.banDurationMin = jsonObject.getLong("bandurationmin");
		save();
	}

	void save() {
		Config config = sf.getConfig();
		config.setXML(this.toXml());
		config.write();
	}



	public void removeBlock(String ip) {
		boolean save;
		synchronized(permBlockList) {
			save = permBlockList.remove(ip);
		}
		synchronized(tempBlockList) {
			save = tempBlockList.remove(ip) || save;  // save is last to prevent short circuit
		}
		if(save) {
			save();
		}
	}

	public void removeExcludedIP(String ip) {
		synchronized (excludeList) {
			excludeList.remove(ip);
		}
	}

	enum Action {
		TEMPBAN,
		PERMBAN,
		REJECT;
	}

	Action getAction() {
		return action;
	}

	public int getRequestLimit() {
		return requestLimit;
	}
	
	long getBanDurationMin() {
		return banDurationMin;
	}
}
