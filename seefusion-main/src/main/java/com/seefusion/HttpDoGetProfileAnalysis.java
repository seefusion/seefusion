/**
 * 
 */
package com.seefusion;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.seestack.SeeStack;

/**
 * @author Daryl
 * 
 */
class HttpDoGetProfileAnalysis extends HttpRequestHandler {
	
	private static final Logger LOG = Logger.getLogger(HttpDoGetProfileAnalysis.class.getName());
	
	static final String CRLF = "\r\n";
	
	@Override
	public String doGet(HttpTalker talker) throws IOException {
		Properties urlParams = talker.getUrlParams();
		String id = urlParams.getProperty("id");
		SeeFusion sf = talker.getSeeFusion();
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
			return "";
		}
		else {
			talker.setContentType("text/xml");
			SeeStack ss = new SeeStack(talker.getSeeFusion().getConfig().getConfigDir(), null);
			StringBuilder ret = new StringBuilder();
			ret.append(ss.process(new StackParserSeeFusion(pr.getSnapshots())).toString());
			
			return ret.toString();
		}

	}
	
	static Perm perm = new Perm(Perm.LOGGEDIN);
	@Override
	Perm getPerm() {
		return perm;
	}
}
