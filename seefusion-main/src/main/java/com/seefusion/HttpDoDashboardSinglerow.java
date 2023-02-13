/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 * 
 */

class HttpDoDashboardSinglerow extends HttpRequestHandler {

	static Object semaphore = new Object();
	static String ret;
	static long currentsec = 0;
	
	/*
	 * dashboard call from another server
	 * 
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	public String doGet(HttpTalker talker) {
		
		synchronized (semaphore) {
			if (currentsec != System.currentTimeMillis() / 1000) {
				RequestList requestList = talker.getSeeFusion().getMasterRequestList();
				StringBuilder result = new StringBuilder(2000);

				result.append("HTTP/1.0 200 OK\r\ncontent-type: text/html\r\n\r\n")
					.append(OutputFormatter.dashboardDetailHtml(talker.getSeeFusion(), requestList));
				
				ret = result.toString();
				currentsec = System.currentTimeMillis() / 1000;
			}
			else {
//				System.out.println("Cached!");
			}
		}
		return ret;
	}

	static Perm perm = new Perm(Perm.LOGGEDIN);
	@Override
	Perm getPerm() {
		return perm;
	}
}
