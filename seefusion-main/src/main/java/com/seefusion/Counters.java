/**
 * 
 */
package com.seefusion;

import java.sql.Date;
import java.util.UUID;

/**
 * 
 * 
 */
public class Counters extends DaoObjectImpl implements DaoObject {
	
	private String id;
	
	private long counterTimestamp;

	private int counterDuration;

	private int pageCount;

	private long pageTime;

	private int queryCount;

	private long queryTime;

	private long uptime;

	private int activeRequests;

	private long freeMemory;

	private long maxMemory;

	private double loadAverage;

	private String instanceName;

	Counters(SeeFusion sf) {
		counterTimestamp = System.currentTimeMillis();
		setInstantInTimeCounters(sf);
	}

	void setInstantInTimeCounters(SeeFusion sf) {
		instanceName = sf.getInstanceName();
		freeMemory = sf.getFreeMemory();
		maxMemory = sf.getTotalMemory();
		activeRequests = sf.getMasterRequestList().getCurrentRequestCount();
		uptime = SeeFusion.getUptime();
		loadAverage = SeeFusion.getCurrentLoadAverage();
	}
	
    Counters(long timestamp, int counterDuration, int pageCount, long totalPageTimeMs, int queryCount, long totalQueryTimeMs, SeeFusion sf) {
    	this.id = UUID.randomUUID().toString();
        this.counterTimestamp = timestamp;
        this.counterDuration = counterDuration;
        this.pageCount = pageCount;
        this.pageTime = totalPageTimeMs;
        this.queryCount = queryCount;
        this.queryTime = totalQueryTimeMs;
        setInstantInTimeCounters(sf);
    }

	
	public Counters() {
	}

	public Counters(String id, Date timestamp, String instanceName, int durationSeconds, int numPages, long pageTime,
			int numQueries, long queryTime, long memoryUsed, long memorySize, long uptimeMs, int activeRequests) {
				this.id = id;
				this.counterTimestamp = timestamp.getTime();
				this.instanceName = instanceName;
				this.counterDuration = durationSeconds;
				this.pageCount = numPages;
				this.pageTime = pageTime;
				this.queryCount = numQueries;
				this.queryTime = queryTime;
				this.freeMemory = memorySize - memoryUsed;
				this.maxMemory = memorySize;
				this.uptime = uptimeMs;
				this.activeRequests = activeRequests;
	}

	/**
	 * @return Returns the activeRequests.
	 */
	public int getActiveRequests() {
		return this.activeRequests;
	}

	/**
	 * @param activeRequests
	 *            The activeRequests to set.
	 */
	void setActiveRequests(int activeRequests) {
		this.activeRequests = activeRequests;
	}

	public long getAvgPageTime() {
		return pageCount == 0 ? 0 : (long) ((float) pageTime / (float) pageCount);
	}

	public long getAvgQueryTime() {
		return queryCount == 0 ? 0 : (long) ((float) queryTime / (float) queryCount);
	}

	/**
	 * @param pageCount
	 *            The pageCount to set.
	 */
	void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	void addPageCount(int pageCount) {
		this.pageCount += pageCount;
	}

	/**
	 * @return Returns the pageCount.
	 */
	public int getPageCount() {
		return pageCount;
	}

	/**
	 * @param pageTime
	 *            The pageTime to set.
	 */
	void setPageTime(long pageTime) {
		this.pageTime = pageTime;
	}

	void addPageTime(long pageTime) {
		this.pageTime += pageTime;
	}

	/**
	 * @return Returns the total pageTime.
	 */
	public long getPageTime() {
		return pageTime;
	}

	/**
	 * @param queryCount
	 *            The queryCount to set.
	 */
	void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}

	void addQueryCount(int queryCount) {
		this.queryCount += queryCount;
	}

	/**
	 * @return Returns the queryCount.
	 */
	public int getQueryCount() {
		return queryCount;
	}

	/**
	 * @param queryTime
	 *            The queryTime to set.
	 */
	void setQueryTime(long queryTime) {
		this.queryTime = queryTime;
	}

	void addQueryTime(long queryTime) {
		this.queryTime += queryTime;
	}

	/**
	 * @return Returns the queryTime.
	 */
	public long getQueryTime() {
		return queryTime;
	}

	/**
	 * @param counterDuration
	 *            The counterDuration to set (seconds).
	 */
	void setCounterDuration(int counterDuration) {
		this.counterDuration = counterDuration;
	}

	/**
	 * @return Returns the counterDuration (sec).
	 */
	public int getCounterDuration() {
		return counterDuration;
	}

	/**
	 * @param counterTime
	 *            The counterTime to set.
	 */
	void setCounterTimestamp(long counterTime) {
		this.counterTimestamp = counterTime;
	}

	/**
	 * @return Returns the counterTime.
	 */
	public long getCounterTimestamp() {
		return counterTimestamp;
	}

	void addHistorySecond(HistorySecond curHistorySecond) {
		// pages
		addPageTime(curHistorySecond.totalPageTimeMs);
		addPageCount(curHistorySecond.pageCount);
		// queries
		addQueryTime(curHistorySecond.totalQueryTimeMs);
		addQueryCount(curHistorySecond.queryCount);
	}

	public float getPagesPerSecond() {
		return counterDuration == 0 ? 0 : Math.round((float) pageCount / (float) counterDuration * (float) 10.0)
				/ (float) 10.0;
	}

	public float getQueriesPerSecond() {
		return counterDuration == 0 ? 0 : Math.round((float) queryCount / (float) counterDuration * (float) 10.0)
				/ (float) 10.0;
	}

	/**
	 * @param uptime
	 *            The uptime to set.
	 */
	void setUptime(long uptime) {
		this.uptime = uptime;
	}

	/**
	 * @return Returns the uptime.
	 */
	public long getUptimeMs() {
		return uptime;
	}

	SimpleXml toXml() {
		SimpleXml ret = new SimpleXml("counters");
		ret.addTag("timestamp", Util.flexDateFormat(this.counterTimestamp));
		ret.addTag("timestampLong", this.counterTimestamp);
		ret.addTag("duration", this.counterDuration);
		ret.addTag("pageCount", this.pageCount);

		float avgPageTimeMs = (float)0.0;
		if (pageCount > 0) {
			avgPageTimeMs = Math.round(((float) this.pageTime / (float) this.pageCount) * 10) / 10;
		}
		ret.addTag("avgPageTimeMs", avgPageTimeMs);
		
		ret.addTag("queryCount", this.queryCount);
		float avgQueryTimeMs = (float)0.0;
		if (queryCount > 0) {
			avgQueryTimeMs = Math.round(((float) this.queryTime / (float) this.queryCount) * 10) / 10;
		}
		ret.addTag("avgQueryTimeMs", avgQueryTimeMs);
		
		int memPct;
		if (this.freeMemory > 0) {
			memPct = (int) (Math.round((1.0 - (float) this.freeMemory / (float) this.maxMemory) * 100));
		} else {
			memPct = 0;
		}
		ret.addTag("memPct", memPct);

		ret.addTag("uptime", Util.msFormat(this.uptime));
		
		ret.addTag("activeRequests", this.activeRequests);
		return ret;
	}
	
	@Override
	public JSONObject toJson()  {
		JSONObject ret = new JSONObject();
		ret.put("timestamp", this.counterTimestamp);
		ret.put("duration", this.counterDuration);
		ret.put("pageCount", this.pageCount);

		float avgPageTimeMs = (float)0.0;
		if (pageCount > 0) {
			avgPageTimeMs = Math.round(((float) this.pageTime / (float) this.pageCount) * 10) / 10;
		}
		ret.put("avgPageTimeMs", avgPageTimeMs);
		
		ret.put("queryCount", this.queryCount);
		float avgQueryTimeMs = (float)0.0;
		if (queryCount > 0) {
			avgQueryTimeMs = Math.round(((float) this.queryTime / (float) this.queryCount) * 10) / 10;
		}
		ret.put("avgQueryTimeMs", avgQueryTimeMs);
		
		ret.put("memoryAvailableMiB", this.freeMemory / 1048576);
		ret.put("memoryUsedMiB", (this.maxMemory-this.freeMemory) / 1048576);
		ret.put("memoryTotalMiB", this.maxMemory / 1048576);
			
		ret.put("uptime", Util.msFormat(this.uptime));
		ret.put("loadAverage", this.loadAverage);
		
		ret.put("activeRequests", this.activeRequests);
		return ret;
	}

	static String rpad(String s, int width) {
		StringBuilder buf = new StringBuilder(s);
		if (s.length() < width) {
			buf = new StringBuilder(width);
			buf.append(s);
			while (buf.length() < width) {
				buf.append(' ');
			}
		} else {
			buf = new StringBuilder(s.length() + 1);
			buf.append(s);
			buf.append(' ');
		}
		return buf.toString();
	}

	static String lpad(String s, int width) {
		StringBuilder buf;
		if (s.length() > width) {
			buf = new StringBuilder(s.length() + 1);
			buf.append(s);
		} else {
			buf = new StringBuilder(width + 1);
			int spaceCount = width - s.length() + 1;
			for (int i = 0; i < spaceCount; i++) {
				buf.append(' ');
			}
			buf.append(s);
		}
		return buf.toString();
	}

	@Override
	public String getId() {
		return id;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public long getMaxMemory() {
		return maxMemory;
	}

}