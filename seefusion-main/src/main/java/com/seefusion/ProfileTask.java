package com.seefusion;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

public class ProfileTask extends TimerTask {

	private Profile profile;
	private RequestList reqList;
	private Profiler profiler;

	ProfileTask(Profiler profiler, Profile profile, RequestList reqList) {
		this.profiler = profiler;
		this.profile = profile;
		this.reqList = reqList;
	}
	
	List<Thread> getThreadList() {
		List<Thread> ret = new LinkedList<Thread>();
		List<Map.Entry<String, RequestInfo>> foo = reqList.getCurrentRequests();
		RequestInfo ri;
		for (Map.Entry<String, RequestInfo> bar : foo) {
			ri = bar.getValue();
			ret.add(ri.getThread());
		}
		return ret;
	}

	@Override
	public void run() {
		List<Thread> tlist = getThreadList();
		// System.out.println("ProfilingRequest logging " + tlist.size() + "
		// threads.");
		for (Thread t : tlist) {
			profile.addSnapshot(new ThreadStack(t));
		}
		if (profile.isComplete()) {
			profiler.stop();
		}
	}
	
}
