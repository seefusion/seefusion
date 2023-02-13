package com.seefusion;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class JsonDoDeleteProfile extends JsonRequestHandler {

	private static final Logger LOG = Logger.getLogger(JsonDoDeleteProfile.class.getName());
	
	@Override
	Perm getPerm() {
		return new Perm(Perm.CONFIG);
	}

	@Override
	JSONObject doJson(HttpTalker talker) {
		SeeFusion sf = talker.getSeeFusion();
		String id = talker.getUrlParams().getProperty("id");
		ProfileDao pr;
		try {
			pr = (ProfileDao)sf.getDbLogger().getDao(Profile.class);
			pr.deleteById(id);
		}
		catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception deleting profile " + id.replaceAll("[^a-zA-Z0-9]+", ""), e);
			throw new ErrorMessage("Profile not found.");
		}
		return new JSONObject("{success:true}");
	}

}
