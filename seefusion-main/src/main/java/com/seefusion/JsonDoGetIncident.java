package com.seefusion;

import java.sql.SQLException;
import java.util.Properties;

public class JsonDoGetIncident extends JsonRequestHandler {

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject doJson(HttpTalker talker) {
		Properties params = talker.getUrlParams();
		String id = params.getProperty("id");
		// push all available counters to client
		SeeFusion sf = talker.getSeeFusion();
		SeeDAO<Incident> dao = (SeeDAO<Incident>) sf.getDbLogger().getDao(Incident.class);
		Incident obj;
		try {
			obj = dao.getById(id);
		}
		catch (SQLException e) {
			throw new RuntimeException("Unable to fetch Incident", e);
		}
		if(obj==null) {
			throw new ErrorMessage(404, "Not Found");
		}
		else {
			JSONObject ret = obj.toJson();
			try {
				ret.put("requests", obj.getRequests());
			}
			catch (SQLException e) {
				ret.put("requests", new JSONArray());
			}
			return ret;
		}
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
