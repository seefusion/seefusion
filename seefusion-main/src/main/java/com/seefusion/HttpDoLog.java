/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
class HttpDoLog extends HttpRequestHandler {

	/* (non-Javadoc)
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	@Override
	public String doGet(HttpTalker talker) {
		
		StringBuilder result = new StringBuilder(4000);
		//talker.appendHeader(result);
		//talker.appendGenericHeader("Log", result);
		result.append("<p style=\"font-family:monospace\">").append(SeeFusionHandler.getLog().replaceAll("&", "&amp;").replaceAll("<", "&lt;\r").replaceAll("\r", "<br>\r")).append("</p>");
		//talker.appendFooter(result);
		return result.toString();
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}
}
