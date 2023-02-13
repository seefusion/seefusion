/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
class RuleQueryTime extends ActiveMonitoringRule {

	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#checkCondition(com.seefusion.SeeFusion, com.seefusion.RequestInfo)
	 */
	String checkCondition(SeeFusion sf, RequestInfo req) {
		QueryInfo qi = req.getQueryInfo();
		if(qi == null) {
			return null;
		}
		long value = qi.getElapsedTime();
		if(value >= this.triggerLimit) {
			return "Longest Active Query Time of " + value + "ms >= limit of " + this.triggerLimit + "ms";
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#getDescription()
	 */
	String getDescription() {
		return "Longest Active Query Time limit of " + this.triggerLimit + "ms";
	}
	
	@Override
	StabilityRuleType getTriggerType() {
		return StabilityRuleType.QUERYTIME;
	}

}
