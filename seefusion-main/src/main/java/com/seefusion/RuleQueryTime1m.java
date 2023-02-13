/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
class RuleQueryTime1m extends ActiveMonitoringRule {

	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#checkCondition(com.seefusion.SeeFusion, com.seefusion.RequestInfo)
	 */
	String checkCondition(SeeFusion sf, RequestInfo req) {
		Counters c = sf.getHistoryMinutes().getMostRecentCounters();
		if(c==null) {
			return null;
		}
		long value = c.getAvgQueryTime();
		if(value >= this.triggerLimit) {
			return "Avg Query Time of " + value + "ms >= limit of " + this.triggerLimit + "ms";
		} else {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#getDescription()
	 */
	String getDescription() {
		return "Avg Query Time limit of " + this.triggerLimit + "ms";
	}

	@Override
	StabilityRuleType getTriggerType() {
		return StabilityRuleType.QUERYTIME1M;
	}
}
