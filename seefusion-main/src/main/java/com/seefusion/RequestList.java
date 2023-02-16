/**
 * 
 */
package com.seefusion;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Daryl
 * 
 */
class RequestList extends SeeTask implements Subject<RequestList>, Cloneable {

	Map<String, RequestInfo> currentRequests = new LinkedHashMap<String, RequestInfo>();

	private List<RequestInfo> recentPages = new LinkedList<RequestInfo>();

	private List<RequestInfo> recentSlowPages = new LinkedList<RequestInfo>();

	private SuffixList historyIgnoreSuffixList;

	private int historySize = 10;

	private int slowHistorySize = 10;

	private long slowPageThreshold;

	private long slowQueryThreshold;

	private long wakeInterval = 1000;
	
	private boolean running = true;
	
	RequestList() {
		currentRequests = new LinkedHashMap<String, RequestInfo>();
		recentPages = new LinkedList<RequestInfo>();
		recentSlowPages = new LinkedList<RequestInfo>();
		slowPageThreshold = 8000;
		slowQueryThreshold = 1000;
		ThreadPool.start(this);
	}
	
	@Override
	public String getThreadName() {
		return "Request Watcher";
	}
	
	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#getCurrentRequestCount()
	 */
	public int getCurrentRequestCount() {
		return this.currentRequests.size();
	}
	
	/* Returns a copy of the active request list 
	 * @see com.seefusion.RequestList#getCurrentRequests()
	 * @returns LinkedList of Map.Entry
	 */
	public LinkedList<Map.Entry<String, RequestInfo>> getCurrentRequests() {
		synchronized (this.currentRequests) {
			return new LinkedList<Map.Entry<String, RequestInfo>>(this.currentRequests.entrySet());
		}
	}

	RequestInfo getOldestActiveRequest() {
		synchronized (this.currentRequests) {
			if(this.currentRequests.isEmpty()) {
				return null;
			}
			return (this.currentRequests.entrySet().iterator().next()).getValue();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#getRecentRequests()
	 * @returns LinkedList of RequestInfo
	 */
	public List<RequestInfo> getRecentRequests() {
		// log("SeeFusion Classloader: " +
		// this.getClass().getClassLoader().toString());
		synchronized (this.currentRequests) {
			// log("SeeFusion getRecentRequests instance: " + this.toString());
			// log("SeeFusion getRecentRequests size: " +
			// Integer.toString(this.last10Pages.size()));
			// log("SeeFusion getRecentRequests hashcode: " +
			// this.last10Pages.hashCode());
			return new LinkedList<RequestInfo>(this.recentPages);
		}
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#getSlowRequests()
	 * @returns List of RequestInfo
	 */
	public List<RequestInfo> getSlowRequests() {
		synchronized (this.currentRequests) {
			return new LinkedList<RequestInfo>(this.recentSlowPages);
		}
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#releaseRequest(com.seefusion.RequestInfo)
	 */
	public void releaseRequest(RequestInfo pi) {
		if (pi.isActive()) {
			pi.setInactive();
			synchronized (currentRequests) {
				currentRequests.remove(pi.getRequestKey());
				if (historyIgnoreSuffixList == null || !historyIgnoreSuffixList.hasMatch(pi.getRequestURI())) {
					if (historySize > 0) {
						this.recentPages.addFirst(pi);
						if (this.recentPages.size() > historySize) {
							this.recentPages.removeLast();
						}
					}
					if (slowHistorySize > 0) {
						if (pi.getElapsedTime() >= slowPageThreshold) {
							recentSlowPages.addFirst(pi);
							if (recentSlowPages.size() > slowHistorySize) {
								recentSlowPages.removeLast();
							}
						}
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#createRequest(com.seefusion.RequestInfo)
	 */
	public void createRequest(RequestInfo pi) {
		synchronized (currentRequests) {
			currentRequests.put(pi.getRequestKey(), pi);
		}
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#kill(java.lang.String)
	 */
	public RequestInfo kill(String pid) {
		RequestInfo pi = currentRequests.get(pid);
		if (pi != null) {
			pi.kill();
		}
		return pi;
	}
	
	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#kill(java.lang.String)
	 */
	public RequestInfo killOrKillStop(String pid) {
		RequestInfo pi = getActiveRequest(pid);
		if (pi != null) {
			pi.killOrKillStop();
		}
		return pi;
	}

	public RequestInfo getActiveRequest(String pid) {
		return currentRequests.get(pid);
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#killStop(java.lang.String)
	 */
	public RequestInfo killStop(String pid) {
		RequestInfo pi = currentRequests.get(pid);
		if (pi != null) {
			pi.killStop();
		}
		return pi;
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#getHistorySize()
	 */
	public int getHistorySize() {
		return historySize;
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#setHistorySize(int)
	 */
	public void setHistorySize(int size) {
		historySize = size;
		while(recentPages.size() > size) {
			recentPages.removeLast();
		}
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#getSlowHistorySize()
	 */
	public int getSlowHistorySize() {
		return slowHistorySize;
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#setSlowHistorySize(int)
	 */
	public void setSlowHistorySize(int size) {
		slowHistorySize = size;
		while(recentSlowPages.size() > size) {
			recentSlowPages.removeLast();
		}
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#getSlowPageThreshold()
	 */
	public long getSlowPageThreshold() {
		return slowPageThreshold;
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#setslowPageThreshold(long)
	 */
	public void setSlowPageThreshold(long slowPageThreshold) {
		this.slowPageThreshold = slowPageThreshold;
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#setHistoryIgnoreSuffixList(com.seefusion.SuffixList)
	 */
	public void setHistoryIgnoreSuffixList(SuffixList suffixList) {
		this.historyIgnoreSuffixList = suffixList;
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#getSlowQueryThreshold()
	 */
	public long getSlowQueryThreshold() {
		return slowQueryThreshold;
	}

	/* (non-Javadoc)
	 * @see com.seefusion.RequestList#setSlowQueryThreshold(long)
	 */
	public void setSlowQueryThreshold(long slowQueryThreshold) {
		this.slowQueryThreshold = slowQueryThreshold; 
		
	}

	@Override
	public void run() {
		while(running) {
			synchronized(this) {
				try {
					this.wait(wakeInterval);
				}
				catch (InterruptedException e) {
					//ignore
				}
				if(running && hasObservers()) {
					notifyObservers();
				}
			}
		}
	}
	
	SubjectImpl<RequestList> subjectImpl = new SubjectImpl<RequestList>();
	@Override
	public void addObserver(Observer<RequestList> o) {
		subjectImpl.addObserver(o);
	}
	@Override
	public void removeObserver(Observer<RequestList> o) {
		subjectImpl.removeObserver(o);
	}
	public void notifyObservers() {
		subjectImpl.notifyObservers(this);
	}
	public boolean hasObservers() {
		return subjectImpl.hasObservers();
	}
	
	@Override
	void shutdown() {
		running = false;
		synchronized(this) {
			this.notify();
		}
	}

	List<String> getActiveThreadNames() {
		List<String> ret = new LinkedList<String>();
		LinkedList<Map.Entry<String, RequestInfo>> activeRequests = getCurrentRequests();
		for(Iterator<Map.Entry<String, RequestInfo>> iter = activeRequests.iterator(); iter.hasNext(); ) {
			RequestInfo ri = iter.next().getValue();
			ret.add(ri.getThreadName());
		}
		return ret;
	}

	public List<Entry<String, RequestInfo>> getProfileRequests(String id) {
		LinkedList<Map.Entry<String, RequestInfo>> ret = getCurrentRequests();
		for(Iterator<Map.Entry<String, RequestInfo>> iter = ret.iterator(); iter.hasNext(); ) {
			RequestInfo ri = iter.next().getValue();
			if(!id.equals(ri.getProfileName())) {
				iter.remove();
			}
		}
	    return null;
    }

	public JSONObject toJSON() {
		JSONObject ret = new JSONObject();
		ret.put("active", requestListToJSON(currentRequests.values()));
		ret.put("recent", requestListToJSON(recentPages));
		ret.put("slow", requestListToJSON(recentSlowPages));
		return ret;
	}

	private JSONArray requestListToJSON(Collection<RequestInfo> reqs) {
		JSONArray ret = new JSONArray();
		for(RequestInfo ri : reqs) {
			ret.add(ri.toJson());
		}
		return ret;
	}

	public boolean isRequestActive(String requestID) {
		return currentRequests.containsKey(requestID);
	}

	public int getIPCount(String remoteAddr) {
		int ret = 0;
		LinkedList<Map.Entry<String, RequestInfo>> list = getCurrentRequests();
		for(Map.Entry<String, RequestInfo> ri : list) {
			if(remoteAddr.equals(ri.getValue().getRemoteIP())) {
				ret++;
			}
		}
		return ret;
	}

	public Collection<SimpleXml> toXml() {
		LinkedList<SimpleXml> ret = new LinkedList<SimpleXml>();
		SimpleXml foo = new SimpleXml("runningRequests");
		addRequestListToXml(foo, currentRequests.values());
		ret.add(foo);
		foo = new SimpleXml("completedRequests");
		addRequestListToXml(foo, recentPages);
		ret.add(foo);
		foo = new SimpleXml("slowRequests");
		addRequestListToXml(foo, recentSlowPages);
		ret.add(foo);
		return ret;
	}

	private void addRequestListToXml(SimpleXml ret, Collection<RequestInfo> reqs) {
		for(RequestInfo ri : reqs) {
			ret.add(ri.toXml());
		}
	}

}
