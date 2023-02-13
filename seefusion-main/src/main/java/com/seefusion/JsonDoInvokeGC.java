package com.seefusion;

public class JsonDoInvokeGC extends JsonRequestHandler {

	static long lastCall = 0;

	public JSONObject doJson(HttpTalker talker)  {
		long startTick = System.currentTimeMillis();
		
		if (startTick - lastCall < 10000) {
			throw new ErrorMessage("GC cannot be called more than once every 10 seconds.");
		}
		else {
			System.gc();
			lastCall = System.currentTimeMillis();
			long dur = lastCall - startTick;
			
			JSONObject ret = new JsonDoServerInfo().doJson(talker); // append the server info, so the UI can immediately reflect the gc
			ret.put("success", "Full garbage collection complete (" + Long.toString(dur) + "ms).");
			return ret;
		}
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
