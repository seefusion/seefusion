/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
class XmlDoGetServer extends XmlRequestHandler implements Cacheable {

	@Override
	public SimpleXml doXml(HttpTalker talker) {
		SeeFusion sf = talker.getSeeFusion();
		RequestList requestList = sf.getMasterRequestList();
		SimpleXml ret = new SimpleXml("seefusioninfo");
		ret.addTag("copyright", SeeFusionMain.COPYRIGHT);
		ret.addTag("version", "SeeFusion v." + SeeFusion.getVersion());
		ret.addTag("timestamp", Util.now());
		Perm userPerm = talker.getHttpRequest().getPerm();
		ret.addTag("isauthenticated", Boolean.toString(userPerm.has(Perm.LOGGEDIN)));
		ret.addTag("canauthenticate", Boolean.toString(userPerm.has(Perm.LOGGEDIN)));
		ret.addTag("cankill", Boolean.toString(userPerm.has(Perm.KILL)));
		ret.addTag("canconfig", Boolean.toString(userPerm.has(Perm.CONFIG)));

		Counters[] stats = sf.getHistoryMinutes().getCounterIntervals();
		long freeMemory = sf.getFreeMemory();
		long totalMemory = sf.getTotalMemory();
		SimpleXml xml = new SimpleXml("server");
		xml.addTag("name", sf.getInstanceName());
		xml.addTag("maxrequests", sf.maxRequests);
		xml.add(requestList.toXml());
		ret.add(xml);
		for (int i = 0; i < stats.length; ++i) {
			ret.add(stats[i].toXml());
		}
		xml = new SimpleXml("memory");
		xml.addTag("available", freeMemory);
		xml.addTag("currentmax", totalMemory);
		ret.add(xml);
		ret.addTag("uptime", Util.msFormat(SeeFusion.getUptime()));
		return ret;
	}
	
	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

	@Override
	public long getCacheableDurationMs() {
		return 2500;
	}
}
