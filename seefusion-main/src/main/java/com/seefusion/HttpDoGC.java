/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 * 
 */
class HttpDoGC extends HttpRequestHandler {

	static long lastCall = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	public String doGet(HttpTalker talker) {
		long startTick = System.currentTimeMillis();
		if (startTick - lastCall < 10000) {
			talker.doMessage("GC may not be called via SeeFusion more often than once every 10 seconds.");
		}
		else {
			System.gc();
			lastCall = System.currentTimeMillis();
			long dur = lastCall - startTick;
			talker.doMessage("Full garbage collection complete (" + Long.toString(dur) + "ms).");
		}
		return null;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
