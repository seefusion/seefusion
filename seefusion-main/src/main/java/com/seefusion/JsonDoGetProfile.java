package com.seefusion;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class JsonDoGetProfile extends JsonRequestHandler {

	private static final Logger LOG = Logger.getLogger(JsonDoGetProfile.class.getName());

	@Override
	JSONObject doJson(HttpTalker talker) {
		SeeFusion sf = talker.getSeeFusion();
		String id = talker.getUrlParams().getProperty("id");
		final JSONObject ret = new JSONObject();
		Profile profile;
		try {
			profile = (Profile)sf.getDbLogger().getDao(Profile.class).getById(id);
			if(profile==null) {
				throw new ErrorMessage("Profile not found.");
			}
			ret.put("profile", profile.toJson());
			ret.put("analysis", profile.getAnalysis(sf).toJson());
		}
		catch (IOException e) {
			throw new ErrorMessage("Unable to read StackInfo: " + e.toString());
		}
		catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception getting profile " + id.replaceAll("[^a-zA-Z0-9]+", ""), e);
			throw new ErrorMessage("Unable to load profile: " + e.toString());
		}
		return ret;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
