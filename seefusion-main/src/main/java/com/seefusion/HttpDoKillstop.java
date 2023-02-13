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
class HttpDoKillstop extends HttpRequestHandler {

	private static final Logger LOG = Logger.getLogger(HttpDoKillstop.class.getName());
	
	/* (non-Javadoc)
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
		RequestInfo pi = sf.getMasterRequestList().killStop(pid);
		Util.sleep(500);
		String killResult;
		if(pi==null) {
			killResult="Page no longer available to kill.";
		} else {
			String uri = pi.getURI();
			String remoteIP = pi.getRemoteIP();
			killResult = pi.getKillResult();
			try {
				if(pi.isActive()) {
					//log("Naughty thread doesn't want to die!  Getting out the sledgehammer...");
					StackHelper stack = StackHelper.getInstance();
					String forceResult =" // " + stack.kill(pi.getThreadName());
					pi = sf.getMasterRequestList().killStop(pid);
					Util.sleep(200);
					if(pi.isActive()) {
						killResult += forceResult; 
					}
				}
			} catch(Exception e) {
				killResult += e.toString();
				e.printStackTrace();
			}
			LOG.info("Page kill request against \'" + uri + "\' from \'" + remoteIP
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

	@Override
	Perm getPerm() {
		return new Perm(Perm.KILL, Perm.LOGGEDIN);
	}
}
