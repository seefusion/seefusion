/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
class RulePageTime1m extends ActiveMonitoringRule {

	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#checkCondition(com.seefusion.SeeFusion, com.seefusion.RequestInfo)
	 */
	String checkCondition(SeeFusion sf, RequestInfo req) {
		Counters c = sf.getHistoryMinutes().getMostRecentCounters();
		if(c==null) {
			return null;
		}
		long value = c.getAvgPageTime();
		if(value >= this.triggerLimit) {
			return "Avg Page Time of " + value + "ms >= limit of " + this.triggerLimit + "ms";
		} else {
			return null;
		}
	}
	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#getDescription()
	 */
	String getDescription() {
		return "Avg Page Time limit of " + this.triggerLimit + "ms";
	}
	
	@Override
	StabilityRuleType getTriggerType() {
		return StabilityRuleType.PAGETIME1M;
	}

}
