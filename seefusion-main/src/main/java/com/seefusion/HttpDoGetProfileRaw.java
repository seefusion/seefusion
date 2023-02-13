/**
 * 
 */
package com.seefusion;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daryl
 * 
 */
class HttpDoGetProfileRaw extends HttpRequestHandler {
	
	private static final Logger LOG = Logger.getLogger(HttpDoGetProfileRaw.class.getName());
	
	@Override
	public String doGet(HttpTalker talker) throws IOException {
		Properties urlParams = talker.getUrlParams();
		SeeFusion sf = talker.getSeeFusion();
		String id = urlParams.getProperty("id");
		
		Profile pr;
		try {
			pr = (Profile)sf.getDbLogger().getDao(Profile.class).getById(id);
		}
		catch (SQLException e) {
			LOG.log(Level.SEVERE, "Exception getting profile " + id.replaceAll("[^a-zA-Z0-9]+", ""), e);
			pr = null;
		}
		if(pr==null) {
			talker.setResponseCode("404 Not Found");
			return null;
		}
		else {
			return pr.toString();
		}

	}
	
	static Perm perm = new Perm(Perm.LOGGEDIN);
	@Override
	Perm getPerm() {
		return perm;
	}
}
