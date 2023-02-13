/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 * 
 */
class XmlDoDashboard extends XmlRequestHandler implements Cacheable {

	static Object semaphore = new Object();

	static String ret;

	static long currentsec = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	public SimpleXml doXml(HttpTalker talker) {
		SimpleXml ret;
		SeeFusion sf = talker.getSeeFusion();
		synchronized (semaphore) {
			RequestList requestList = sf.getMasterRequestList();
			ret = OutputFormatter.dashboardDetailXml(talker.getSeeFusion(), requestList);
		}
		return ret;
	}

	static Perm perm = new Perm(Perm.LOGGEDIN);
	@Override
	Perm getPerm() {
		return perm;
	}
	
	@Override
	public long getCacheableDurationMs() {
		return 2500;
	}
}
