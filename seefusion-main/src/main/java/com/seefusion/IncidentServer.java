/**
 * 
 */
package com.seefusion;

import java.util.UUID;

/**
 * @author Daryl
 * 
 */
class IncidentServer extends DaoObjectImpl implements DaoObject {

	String incidentServerID = UUID.randomUUID().toString();
	String incidentID;
	long logTime;
	String filterName;
	int activeRequests;
	long uptimeMs;
	float memoryPct;
	float pps10;
	float qps10;
	float pps60;
	float qps60;
	
	IncidentServer() {}
	
	IncidentServer(String incidentID, SeeFusion sf) {
		this.incidentID = incidentID;
		this.logTime = System.currentTimeMillis();
		this.filterName = sf.getInstanceName();
		this.activeRequests = sf.getMasterRequestList().getCurrentRequestCount();
		this.uptimeMs = SeeFusion.getUptime();
		this.memoryPct = sf.getAvailableMemoryPct();
		Counters[] aCounters = sf.getHistoryMinutes().getCounterIntervals();
		Counters c10 = aCounters[1];
		Counters c60 = aCounters[2];
		this.pps10 = c10.getPagesPerSecond();
		this.qps10 = c10.getQueriesPerSecond();
		this.pps60 = c60.getPagesPerSecond();
		this.qps60 = c60.getQueriesPerSecond();
	}
	
	/**
	 * @return Returns the activeRequests.
	 */
	int getActiveRequests() {
		return this.activeRequests;
	}

	/**
	 * @param activeRequests
	 *            The activeRequests to set.
	 */
	void setActiveRequests(int activeRequests) {
		this.activeRequests = activeRequests;
	}

	/**
	 * @return Returns the filterName.
	 */
	String getFilterName() {
		return this.filterName;
	}

	/**
	 * @param filterName
	 *            The filterName to set.
	 */
	void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	/**
	 * @return Returns the incidentID.
	 */
	String getIncidentID() {
		return this.incidentID;
	}

	/**
	 * @param incidentID
	 *            The incidentID to set.
	 */
	void setIncidentID(String incidentID) {
		this.incidentID = incidentID;
	}

	/**
	 * @return Returns the incidentServerID.
	 */
	String getIncidentServerID() {
		return this.incidentServerID;
	}

	/**
	 * @param incidentServerID
	 *            The incidentServerID to set.
	 */
	void setIncidentServerID(String incidentServerID) {
		this.incidentServerID = incidentServerID;
	}

	/**
	 * @return Returns the logTime.
	 */
	long getLogTime() {
		return this.logTime;
	}

	/**
	 * @param logTime
	 *            The logTime to set.
	 */
	void setLogTime(long logTime) {
		this.logTime = logTime;
	}

	/**
	 * @return Returns the memoryPct.
	 */
	float getMemoryPct() {
		return this.memoryPct;
	}

	/**
	 * @param memoryPct
	 *            The memoryPct to set.
	 */
	void setMemoryPct(float memoryPct) {
		this.memoryPct = memoryPct;
	}

	/**
	 * @return Returns the pps10.
	 */
	float getPps10() {
		return this.pps10;
	}

	/**
	 * @param pps10
	 *            The pps10 to set.
	 */
	void setPps10(float pps10) {
		this.pps10 = pps10;
	}

	/**
	 * @return Returns the pps60.
	 */
	float getPps60() {
		return this.pps60;
	}

	/**
	 * @param pps60
	 *            The pps60 to set.
	 */
	void setPps60(float pps60) {
		this.pps60 = pps60;
	}

	/**
	 * @return Returns the qps10.
	 */
	float getQps10() {
		return this.qps10;
	}

	/**
	 * @param qps10
	 *            The qps10 to set.
	 */
	void setQps10(float qps10) {
		this.qps10 = qps10;
	}

	/**
	 * @return Returns the qps60.
	 */
	float getQps60() {
		return this.qps60;
	}

	/**
	 * @param qps60
	 *            The qps60 to set.
	 */
	void setQps60(float qps60) {
		this.qps60 = qps60;
	}

	/**
	 * @return Returns the uptimeMs.
	 */
	long getUptimeMs() {
		return this.uptimeMs;
	}

	/**
	 * @param uptimeMs
	 *            The uptimeMs to set.
	 */
	void setUptimeMs(long uptimeMs) {
		this.uptimeMs = uptimeMs;
	}

	@Override
	public JSONObject toJson() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		return incidentServerID;
	}

}
