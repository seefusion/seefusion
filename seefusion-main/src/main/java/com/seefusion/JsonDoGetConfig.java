package com.seefusion;

public class JsonDoGetConfig extends JsonRequestHandler {

	@Override
	JSONObject doJson(HttpTalker talker)  {
		return doJsonImpl(talker);
	}
	
	static JSONObject doJsonImpl(HttpTalker talker) {
		/* we're building a JSON representation of the config sections and items
		 * [
		 *    {title: 'General', items: [ {}, {}, {} ]}
		 *    ,{title: 'Security', items: [ {}, {}, {} ]}
		 * ]
		 */
		JSONObject json = new JSONObject();
		json.put("driverNames", new JSONArray(DbLogger.DRIVERS.keySet()));
		
		SeeFusion sf = talker.getSeeFusion();
		MessageFormatFactory messageFormats = talker.getMessageFormats();
		JSONArray configJson = talker.getConfigJson();
		
		Config config = sf.getConfig();
		DbLogger dbLogger = sf.getDbLogger();
		
		for(Object _section : configJson) {
			JSONObject section = (JSONObject)_section;
			if("Database Logging".equals(section.getString("title"))) {
				if(dbLogger != null && dbLogger.isRunning()){
					if(dbLogger.isDatabaseOk()){
						section.put("active", true);
						section.put("warnings", "");
					} else {
						json.put("active", true);
						json.put("warnings", messageFormats.getMessage("configMenuSectionDsnLoggingActiveWarningText"));
					}
				} else {
					json.put("active", false);
					json.put("warnings", "");
				}
			}
			for(Object _item : section.getJSONArray("items")) {
				JSONObject item = (JSONObject)_item;
				String configitem = item.getString("configitem");
				String value = config.getProperty(configitem);
				if (item.getString("type").equals("password")) {
					if (value != null && value.length() != 0) {
						value = Password.NO_CHANGE;
					}
				}
				item.put("value", value==null ? "" : value);
			}
			
		}
		json.put("sections", configJson);
		talker.setContentType("application/json");
		return json;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.CONFIG);
	}

}
