package com.seefusion;

import java.util.Calendar;

public class JsonDoAbout extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker)  {
		JSONObject ret = new JSONObject();
		ret.put("version", SeeFusion.getVersion());
		ret.put("buildNumber", "");
		ret.put("year", Calendar.getInstance().get(Calendar.YEAR));
		return ret;
	}

	@Override
	Perm getPerm() {
		return new Perm();
	}

}
