package com.seefusion;

import java.util.Properties;

class JsonDoCounters extends JsonRequestHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.JsonRequest#doGet(com.seefusion.SocketTalker)
	 */
	@Override
	public JSONObject doJson(HttpTalker talker)  {
		Properties params = talker.getUrlParams();
		String sInterval =  params.getProperty("interval");
		int interval = 3;
		try {
			interval = Integer.parseInt(sInterval);
		}
		catch (NumberFormatException e) {
			// ignore
		}
		CountersHistory history = talker.getSeeFusion().getHistoryMinutes();
		try {
			synchronized (history) {
				history.wait(1000);
			}
		}
		catch (InterruptedException e) {
			//ignore
		}
		return history.getCounters(interval).toJson();
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
