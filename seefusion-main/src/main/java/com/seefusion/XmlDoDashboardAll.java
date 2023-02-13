/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 * 
 */
class XmlDoDashboardAll extends XmlRequestHandler implements Cacheable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	@Override
	public SimpleXml doXml(HttpTalker talker) {
		Dashboard db = new Dashboard(talker.sf); 
		return db.doGetXml(talker.sf.dashboardServersList);
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
