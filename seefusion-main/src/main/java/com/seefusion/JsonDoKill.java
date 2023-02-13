/**
 * 
 */
package com.seefusion;

import java.util.logging.Logger;

class JsonDoKill extends JsonRequestHandler {

	private static Logger LOG = Logger.getLogger(JsonDoKill.class.getName());
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.JsonRequest#doGet(com.seefusion.SocketTalker)
	 */
	
	// this was a quick refactor from XmlDoKill.. I don't think it's set up correctly.
	@Override
	public JSONObject doJson(HttpTalker talker)  {
		SeeFusion sf = talker.getSeeFusion();
		JSONObject data = talker.getPostData();
		String pid = data.get("pid").toString();
		
		RequestInfo pi = sf.getMasterRequestList().killOrKillStop(pid);
		
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			// ignore
		}

		String killResult;
		if(pi==null) {
			throw new ErrorMessage("Page no longer available to kill.");
		}
		
		killResult = pi.getKillResult();
		LOG.info("Page kill request against \'" + pi.getURI() + "\' from \'" + pi.getRemoteIP()
			+ "\' by SeeFusion administrator at " + talker.getRemoteAddr() + ": " + killResult);
		JSONObject ret = new JSONObject();
		ret.put("message", killResult);
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			Thread.interrupted();
		}
		ret.put("pages", JsonDoRequestList.doGet(sf.getMasterRequestList(), "active").get("pages"));
		return ret;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.KILL);
	}

}
