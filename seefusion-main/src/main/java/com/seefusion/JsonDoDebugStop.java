package com.seefusion;

import java.util.Properties;

public class JsonDoDebugStop extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker)  {
		Properties urlParams = talker.getUrlParams();
		String ip = urlParams.getProperty("ip");
		
		if(ip != null && ip.equals("mine")){
			ip = talker.getRemoteAddr();
		} else {
			ip = "";
		}
		
		RequestInfo.unRegisterDebugger(ip);
		
		return new JSONObject();
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
