/**
 * 
 */
package com.seefusion;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Daryl
 * 
 */
class HttpDoKill extends HttpRequestHandler {
	
	private static final Logger LOG = Logger.getLogger(HttpDoKill.class.getName());
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	@Override
	public String doGet(HttpTalker talker) {
		SeeFusion sf = talker.getSeeFusion();
		
		// attempt to kill a page
		// don't use 500 errors since IE won't display them
		Properties urlParams = talker.getUrlParams();
		String pid = urlParams.getProperty("pid");
		// params[14]=java.net.URLEncoder.encode(this.remoteIP + ":" +
		// this.requestURI + ":" + this.requestKey);
		RequestInfo pi = sf.getMasterRequestList().kill(pid);
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			// ignore
		}
		String killResult;
		if(pi==null) {
			killResult="Page no longer available to kill.";
		} else {
			killResult=pi.getKillResult();
			LOG.info("Page kill request against \'" + pi.getURI() + "\' from \'" + pi.getRemoteIP()
				+ "\' by SeeFusion administrator at " + talker.getRemoteAddr() + ": " + killResult);
		}
		try {
			talker.sendRedirect("/?alert=" + java.net.URLEncoder.encode(killResult, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			// never happen
		}
		return null;

	}

	static Perm perm = new Perm(Perm.LOGGEDIN);
	@Override
	Perm getPerm() {
		return perm;
	}

}
