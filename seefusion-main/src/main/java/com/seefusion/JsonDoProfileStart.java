package com.seefusion;

import java.util.logging.Logger;

public class JsonDoProfileStart extends JsonRequestHandler {

	private static final Logger LOG = Logger.getLogger(JsonDoProfileStart.class.getName());
	
	@Override
	public JSONObject doJson(HttpTalker talker) throws ErrorMessage {
		SeeFusion sf = talker.getSeeFusion();
		Profiler p = sf.getProfiler();
		Profile req;
		JSONObject params = talker.getPostData();

		String name = params.getString("name");
		long intervalMs = params.getLong("interval");
		long scheduledDurationMs = params.getInt("scheduledDurationMs");

		if(intervalMs < 100) {
			throw new ErrorMessage("Interval must be at least 100ms.");
		}
		if(scheduledDurationMs < 1) {
			throw new ErrorMessage("Duration must be at least one minute.");
		}
		
		req = p.getActiveProfile();
		if (req == null) {
			LOG.info("Server profile started.  Interval " + intervalMs + "ms; scheduled duration " + scheduledDurationMs + "ms");
			ProfileDao dao = (ProfileDao)sf.getDbLogger().getDao(Profile.class);
			if (!p.start(sf.getMasterRequestList(), dao, sf.getInstanceName(), name, intervalMs, scheduledDurationMs)) {
				throw new ErrorMessage("Unable to start server profile; another is already in progress.");
			}
		}

		JSONObject ret = new JSONObject();
		ret.put("active", p.toJson());
		return ret;
	}

	@Override
	Perm getPerm() {
		return new Perm();
	}

}
