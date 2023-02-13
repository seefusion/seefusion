/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
class RuleActiveRequests extends ActiveMonitoringRule {

	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#checkCondition(com.seefusion.SeeFusion, com.seefusion.RequestInfo)
	 */
	String checkCondition(SeeFusion sf, RequestInfo req) {
		int currentRequestCount = sf.getMasterRequestList().getCurrentRequestCount();
		//debug("checking rule " + this.name + ": " + currentRequestCount + " >= " + this.triggerLimit + "?");
		if(currentRequestCount >= this.triggerLimit) {
			return "Current Request Count " + currentRequestCount + " >= limit of " + this.triggerLimit;
		} else {
			return null;
		}
	}
	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#getDescription()
	 */
	String getDescription() {
		return "Active Requests limit of " + this.triggerLimit + " requests";
	}
	
	@Override
	StabilityRuleType getTriggerType() {
		return StabilityRuleType.ACTIVEREQ;
	}

}
