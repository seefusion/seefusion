package com.seefusion;

import java.util.logging.Logger;

public class JsonDoSaveConfig extends JsonRequestHandler {
	
	private static Logger LOG = Logger.getLogger(JsonDoSaveConfig.class.getName());

	@Override
	public JSONObject doJson(HttpTalker talker)  {
		JSONObject req = talker.getPostData();
		JSONObject ret = new JSONObject();
		try {
			setProperty(req.getString("configitem"), req.get("value").toString(), talker);
			return ret.put("message", "");
		}
		catch(ConfigException e) {
			return ret.put("message", e.getMessage());
		}
	}
	
	static void setProperty(String option, String value, HttpTalker talker) throws ConfigException  {	
		SeeFusion sf = talker.getSeeFusion();
		Config config = sf.getConfig();

		StringBuilder result = new StringBuilder();
		if (value.equals(Password.NO_CHANGE)) {
			// do nothing
			return;
		}
		String curValue = config.getProperty(option);
		if(curValue == null && value.equals("")) {
			// nop
			return;
		}
		if(!value.equals(curValue)) {
			if (value.length() > 1000) value = value.substring(0, 1000);
			if (option.equals("dbPassword") || option.equals("smtpPassword")) {
				if (!value.equals(Password.NO_CHANGE)) {
					if (value.length() == 0) {
						LOG.info("User from " + talker.getRemoteAddr() + " cleared the property \'" + option + "\'.");
					}
					else {
						LOG.info("User from " + talker.getRemoteAddr() + " set property \'" + option + "\'.");
					}
				}
			}
			else if (option.indexOf("Password") != -1) {
				Password password = new Password(value);
				value = password.toString();
				value = Password.safe(value);
				if (!value.equals(Password.NO_CHANGE)) {
					if( option.equalsIgnoreCase("httpPassword") || option.equalsIgnoreCase("httpKillPassword") ) {
						// don't lock the user out of config, if a config password doesn't already exist
						Password configPassword = sf.getHttpConfigPassword();
						if( configPassword == null || configPassword.equals("") ) {
							config.setProperty("httpConfigPassword", value);
						}
					}
					if (value.length() == 0) {
						LOG.info("User from " + talker.getRemoteAddr() + " cleared the property \'" + option + "\'.");
					}
					else {
						LOG.info("User from " + talker.getRemoteAddr() + " set property \'" + option + "\'.");
					}
				}
			}
			else {
				LOG.info("User from " + talker.getRemoteAddr() + " set property \'" + option + "\' to \'" + value + "\'.");
			}
			try {
				SeeFusionHandler.startCapture(result);
				config.setProperty(option, value);
				sf.configure();
			}
			finally {
				SeeFusionHandler.stopCapture(result);
			}
			config.write();
		}
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.CONFIG);
	}


}
