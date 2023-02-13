/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
class RuleMemPct extends ActiveMonitoringRule {

	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#checkCondition(com.seefusion.SeeFusion, com.seefusion.RequestInfo)
	 */
	String checkCondition(SeeFusion sf, RequestInfo req) {
		int memoryPct = 100 - (int)sf.getAvailableMemoryPct();
		if(memoryPct >= this.triggerLimit) {
			System.gc();
			memoryPct = 100 - (int)sf.getAvailableMemoryPct();
			if(memoryPct >= this.triggerLimit) {
				return "Memory at " + memoryPct + "% >= limit of " + this.triggerLimit + "%";
			}
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see com.seefusion.StabilityRule#getDescription()
	 */
	String getDescription() {
		return "Memory limit of " + this.triggerLimit + "%";
	}
	
	@Override
	StabilityRuleType getTriggerType() {
		return StabilityRuleType.MEMORYPCT;
	}

}
