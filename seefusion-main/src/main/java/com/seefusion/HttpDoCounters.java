/**
 * 
 */
package com.seefusion;

//import java.util.Properties;

/**
 * @author Daryl
 *
 */
class HttpDoCounters extends HttpRequestHandler {

	/* (non-Javadoc)
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	public String doGet(HttpTalker talker) {
		/* legacy. commenting out to remove errors
		Properties urlParams = talker.getUrlParams();
		SeeFusion sf = talker.getSeeFusion();
		
		int interval = sf.getCountersLogInterval();
		if (urlParams.containsKey("interval")) {
			try {
				interval = Integer.parseInt(urlParams.getProperty("interval"));
			}
			catch (NumberFormatException e) {
				talker.doMessage("Unable to parse interval to integer.");
			}
		}
		if (interval < 1) {
			interval = 1;
		}
		if (interval > 60) {
			interval = 60;
		}
		StringBuilder buf = new StringBuilder();
		talker.appendGenericHeader("Counters", true, buf);
		OutputStream rawOut = talker.getOutputStream();
		
//		out.print("HTTP/1.0 200 OK\r\ncontent-type: text/html\r\n\r\n");
		out.print(buf.toString());
//		out.print("<html><head>\r\n");
		out.print("<script language=\"javascript\">window.focus();</script>\r\n");
//		out.print("</head>\r\n<body>\r\n");
		out.print(talker.getResourceBundle().getString("countersHeader"));
		out.print("<pre>\r\n");
		out.println(Counters.getHtmlHeader());
		out.flush();
		try {
			while (true) {
				Counters c = sf.getCounters(interval);
				rawOut.write(c.getHtmlString().getBytes());
				rawOut.write(SocketTalker.bCRLF);
				rawOut.flush();
				// try to fire at the top of the second
				// add 100ms to make sure we make it into the next second
				long sleeptime = ((long)interval * 1000) - (System.currentTimeMillis() % 1000) + 100; 
				//System.out.println(sleeptime);
				Util.sleep(sleeptime);
			}
		}
		catch (IOException e) {
			// browser hit 'stop', ignore
		}
		*/
		return null;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
