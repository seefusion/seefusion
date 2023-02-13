package com.seefusion;

public class JsonDoProfileServerStop extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker)  {
		Profiler p = talker.getSeeFusion().getProfiler();

		p.stop();
		
		return new JsonDoGetProfilerStatus().doJson(talker);
	}

	@Override
	Perm getPerm() {
		return new Perm();
	}

}
