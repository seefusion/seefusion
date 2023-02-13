package com.seefusion;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class JsonDoDeleteIncident extends JsonRequestHandler {

	private static final Logger LOG = Logger.getLogger(JsonDoDeleteIncident.class.getName());
	
	@Override
	Perm getPerm() {
		return new Perm(Perm.CONFIG);
	}

	@Override
	JSONObject doJson(HttpTalker talker) {
		SeeFusion sf = talker.getSeeFusion();
		String id = talker.getUrlParams().getProperty("id");
		IncidentDao pr;
		try {
			pr = (IncidentDao)sf.getDbLogger().getDao(Incident.class);
			pr.deleteById(id);
		}
		catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception deleting Incident " + id.replaceAll("[^a-zA-Z0-9]+", ""), e);
			throw new ErrorMessage("Incident not found.");
		}
		return new JSONObject("{success:true}");
	}

}
