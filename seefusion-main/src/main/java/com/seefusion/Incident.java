/**
 * 
 */
package com.seefusion;

import java.sql.SQLException;
import java.util.UUID;

/**
 * @author Daryl
 *
 */
class Incident extends DaoObjectImpl implements DaoObject {

	String incidentID=UUID.randomUUID().toString();
	String filterName;
	long beginTime;
	long endTime;
	String incidentType;
	String thresholdType;
	String thresholdValue;
	String actionTaken;
	JSONArray requestList;

	/**
	 * Null Constructor for DaoObject
	 */
	Incident() {}
	
	JSONArray getRequests() throws SQLException {
		if(requestList == null) {
			RequestInfoDao dao = (RequestInfoDao) SeeFusion.getInstance().getDbLogger().getDao(RequestInfo.class);
			requestList = dao.listByColumnValue("incidentID", this.incidentID);
		}
		return requestList;
	}
	
	/**
	 * @return Returns the actionTaken.
	 */
	String getActionTaken() {
		return this.actionTaken;
	}

	/**
	 * @param actionTaken The actionTaken to set.
	 */
	void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	/**
	 * @return Returns the beginTime.
	 */
	long getBeginTime() {
		return this.beginTime;
	}

	/**
	 * @param beginTime The beginTime to set.
	 */
	void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * @return Returns the endTime.
	 */
	long getEndTime() {
		return this.endTime;
	}

	/**
	 * @param endTime The endTime to set.
	 */
	void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return Returns the incidentID.
	 */
	String getIncidentID() {
		return this.incidentID;
	}

	/**
	 * @param incidentID The incidentID to set.
	 */
	void setIncidentID(String incidentID) {
		this.incidentID = incidentID;
	}

	/**
	 * @return Returns the incidentType.
	 */
	String getIncidentType() {
		return this.incidentType;
	}

	/**
	 * @param incidentType The incidentType to set.
	 */
	void setIncidentType(String incidentType) {
		this.incidentType = incidentType;
	}

	/**
	 * @return Returns the thresholdType.
	 */
	String getThresholdType() {
		return this.thresholdType;
	}

	/**
	 * @param thresholdType The thresholdType to set.
	 */
	void setThresholdType(String thresholdType) {
		this.thresholdType = thresholdType;
	}

	/**
	 * @return Returns the thresholdValue.
	 */
	String getThresholdValue() {
		return this.thresholdValue;
	}

	/**
	 * @param thresholdValue The thresholdValue to set.
	 */
	void setThresholdValue(String thresholdValue) {
		this.thresholdValue = thresholdValue;
	}

	
	@Override
	public JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("incidentID", incidentID);
		ret.put("filterName", filterName);
		ret.put("beginTime", beginTime);
		ret.put("endTime", endTime);
		ret.put("incidentType", incidentType);
		ret.put("thresholdType", thresholdType);
		ret.put("thresholdValue", thresholdValue);
		ret.put("actionTaken", actionTaken);
		return ret;
	}

	@Override
	public String getId() {
		return incidentID;
	}


	public String getFilterName() {
		return filterName;
	}

}
