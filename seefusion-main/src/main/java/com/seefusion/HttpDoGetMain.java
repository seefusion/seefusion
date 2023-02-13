/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 * Modified 7/22 by Tim to use HtmlLayout for Http
 */
class HttpDoGetMain extends HttpRequestHandler {
	
	static Object semaphore = new Object();
	static String ret;
	static long currentsec = 0;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	public String doGet(HttpTalker talker) {
		return null;
		// no longer used
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}
}
