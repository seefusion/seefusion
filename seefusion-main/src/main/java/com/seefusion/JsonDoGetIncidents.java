package com.seefusion;

import java.sql.SQLException;

public class JsonDoGetIncidents extends JsonRequestHandler {

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject doJson(HttpTalker talker) {
		SeeFusion sf = talker.getSeeFusion();
		SeeDAO<Incident> dao = (SeeDAO<Incident>) sf.getDbLogger().getDao(Incident.class);
		try {
			return new JSONObject().put("incidents", dao.list());
		}
		catch (SQLException e) {
			throw new RuntimeException("Unable to fetch Incident", e);
		}
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
