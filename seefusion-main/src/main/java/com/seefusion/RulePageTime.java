/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
class RulePageTime extends ActiveMonitoringRule {

	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#checkCondition(com.seefusion.SeeFusion, com.seefusion.RequestInfo)
	 */
	String checkCondition(SeeFusion sf, RequestInfo req) {
		long value = req.getElapsedTime();
		if(value >= this.triggerLimit) {
			return "Page Time at " + value + "ms >= limit of " + this.triggerLimit + "ms";
		} else {
			return null;
		}
	}
		
	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#getDescription()
	 */
	String getDescription() {
		return "Page Time limit of " + this.triggerLimit + "ms";
	}
	
	@Override
	StabilityRuleType getTriggerType() {
		return StabilityRuleType.PAGETIME;
	}

}
