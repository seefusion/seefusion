package com.seefusion;

import java.io.IOException;
import java.util.UUID;

class Profile extends DaoObjectImpl implements DaoObject {

	private String id;

	private String name;

	ThreadStacks threadStacks = new ThreadStacks();
	private String threadStacksJson = null;

	/**
	 * actual start time (System.currentTimeMillis())
	 */
	private long startTick;

	/**
	 * interval at which to take thread snapshots
	 */
	private long intervalMs = 1000;

	/**
	 * tick after which to call stop() (System.currentTimeMillis())
	 */
	private long scheduledDurationMs;

	/**
	 * actual runtime (after stopped)
	 */
	private long actualDurationMs = -1;

	private final String instanceName;

	int stackCount = 0;

	/**
	 * runtime
	 * 
	 * @param sf
	 * @param profiler
	 * @param name
	 * @param intervalMs 
	 */
	Profile(String instanceName, String name, long intervalMs, long scheduledDurationMs) {
		this.instanceName = instanceName;
		this.name = name;
		this.id = UUID.randomUUID().toString();
		this.intervalMs = intervalMs;
		this.scheduledDurationMs = scheduledDurationMs;
	}
	
	Profile(String id, String instanceName, String name, long startTick, long scheduledDurationMs, long intervalMs,
			long actualDurationMs, int stackCount, String threadStacksJson) {
		this.id = id;
		this.instanceName = instanceName;
		this.name = name;
		this.startTick = startTick;
		this.scheduledDurationMs = scheduledDurationMs;
		this.setIntervalMs(intervalMs);
		this.actualDurationMs = actualDurationMs;
		this.stackCount = stackCount;
		setThreadStacks(threadStacksJson);
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("id", id);
		ret.put("instanceName", instanceName);
		ret.put("name", name);
		ret.put("startTick", getStartTick());
		ret.put("intervalMs", getIntervalMs());
		ret.put("scheduledDurationMs", getScheduledDurationMs());
		ret.put("actualDurationMs", getActualDurationMs());
		ret.put("snapshotCount", stackCount);
		return ret;
	}
	
	long getScheduledDurationMs() {
		return scheduledDurationMs;
	}

	@Override
	@SuppressWarnings("PMD.OverrideBothEqualsAndHashcode")
	public boolean equals(Object o) {
		return o != null && o instanceof Profile && ((Profile) o).id.equals(this.id);
	}

	ThreadStacks getSnapshots() {
		if(threadStacks.isEmpty() && threadStacksJson != null) {
			// rematerialize from previously loaded JSON
			threadStacks = new ThreadStacks(new JSONArray(threadStacksJson));
		}
		return threadStacks;
	}

	String getName() {
		return name;
	}

	static final String CRLF = "\r\n";

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		for (ThreadStack ts : getSnapshots().values()) {
			ret.append(ts.toString());
			ret.append(CRLF);
		}
		return ret.toString();
	}

	long getActualDurationMs() {
		if(actualDurationMs == -1) {
			return System.currentTimeMillis() - startTick;
		}
		return actualDurationMs;
	}

	@Override
	public String getId() {
		return id;
	}

	void setIntervalMs(long intervalMs) {
		this.intervalMs = intervalMs;
	}

	long getIntervalMs() {
		return intervalMs;
	}

	private long getScheduledEndTick() {
		return startTick + scheduledDurationMs;
	}

	long getStartTick() {
		return startTick;
	}

	ProfileAnalysis getAnalysis(SeeFusion sf) throws IOException {
		return new ProfileAnalysis(sf, this);
	}

	void addSnapshot(ThreadStack threadStack) {
		threadStacks.add(threadStack);
		stackCount++;
	}

	String getInstanceName() {
		return instanceName;
	}
	
	void notifyStarted() {
		this.startTick = System.currentTimeMillis();
	}

	void notifyStopped() {
		actualDurationMs = System.currentTimeMillis()-getStartTick();
	}

	boolean isComplete() {
		return startTick != 0 && System.currentTimeMillis() > getScheduledEndTick();
	}

	String getThreadStacksJson() {
		if(threadStacksJson == null || threadStacksJson.length() != threadStacks.size()) {
			// using temp var to mitigate possible concurrency problems
			JSONArray ret = new JSONArray();
			for (ThreadStack threadStack : threadStacks.values()) {
				ret.add(threadStack.toJson());
			}
			threadStacksJson = ret.toString();
		}
		return threadStacksJson;
	}

	void setThreadStacks(String json) {
		threadStacksJson = json;
	}

	int getStackCount() {
		return stackCount;
	}
	
	
}
