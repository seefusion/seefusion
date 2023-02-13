package com.seefusion;

import java.sql.SQLException;

public class JsonDoGetProfilerStatus extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker) {
		JSONObject ret = new JSONObject();
		SeeFusion sf = talker.getSeeFusion();
		JSONObject status = new JSONObject();
		status.put("active", sf.getProfiler().toJson());
		try {
			JSONArray list = sf.getDbLogger().getDao(Profile.class).list();
			status.put("saved", list);
		}
		catch (SQLException e) {
			throw new ErrorMessage("Unable to load profiles", e);
		}
		ret.put("status", status);
		return ret;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}


}
