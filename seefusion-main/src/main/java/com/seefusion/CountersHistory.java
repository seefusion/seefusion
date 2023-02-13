/**
 * 
 */
package com.seefusion;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Daryl
 *
 */
public class CountersHistory extends SeeTask implements Subject<Counters> {

	long thisSec = System.currentTimeMillis() / 1000;

	int pagesThisSec = 0;

	long totalPageTimeThisSec = 0;

	int queriesThisSec = 0;

	long totalQueryTimeThisSec = 0;

	SubjectImpl<Counters> subjectImpl = new SubjectImpl<Counters>();
	
	LinkedList<Counters> countersHistory = new LinkedList<Counters>();
	
	int size;
	
	Counters mostRecentCounters = null;
	
	boolean running = true;
	
	SeeFusion sf;
	
	PooledThread thread;
	
	// these "should" be in CountersHistory, but have been moved
	// out here for speed
	int pagesThisMin = 0;

	long totalPageTimeThisMin = 0;

	int queriesThisMin = 0;

	long totalQueryTimeThisMin = 0;

	int pagesThisInterval = 0;

	long totalPageTimeThisInterval = 0;

	int queriesThisInterval = 0;

	long totalQueryTimeThisInterval = 0;

	long totalPageCount = 0;

	long totalQueryCount = 0;
	
	// the number of Counters objects representing the last 2h of 1s-increment
	// history
	int maxCounterHistory = 60;

	/*
	 * returns counters valid since the last call to this method
	 */
	long prevCountersSec = System.currentTimeMillis() / 1000;

	LinkedList<HistorySecond> historySeconds = new LinkedList<HistorySecond>();

	SizedHashMap<Long, HistorySnapshot> historySnapshots = new SizedHashMap<Long, HistorySnapshot>(120);

	Counters getIntervalCounters() {
		Counters ret = new Counters(sf);
		synchronized (historySeconds) {
			long thisSec = System.currentTimeMillis() / 1000;
			ret.setCounterDuration((int) (thisSec - prevCountersSec));
			ret.setPageCount(pagesThisInterval);
			ret.setPageTime(totalPageTimeThisInterval);
			ret.setQueryCount(queriesThisInterval);
			ret.setQueryTime(totalQueryTimeThisInterval);
			ret.setUptime(SeeFusion.getUptime());
			ret.setActiveRequests(sf.getMasterRequestList().getCurrentRequestCount());
			resetIntervalCounters();
		}
		return ret;
	}

	void resetIntervalCounters() {
		prevCountersSec = System.currentTimeMillis() / 1000;
		pagesThisInterval = 0;
		totalPageTimeThisInterval = 0;
		queriesThisInterval = 0;
		totalQueryTimeThisInterval = 0;
	}

	void newSecond(long thisSec) {
		if (this.pagesThisSec != 0 || this.queriesThisSec != 0) {
			synchronized (historySeconds) {
				historySeconds.addFirst(new HistorySecond(this.thisSec, this.pagesThisSec, this.totalPageTimeThisSec,
						this.queriesThisSec, this.totalQueryTimeThisSec, SeeFusion.getCurrentLoadAverage()));
				if (historySeconds.size() > maxCounterHistory) {
					historySeconds.removeLast();
				}
			}
			this.pagesThisSec = 0;
			this.totalPageTimeThisSec = 0;
			this.queriesThisSec = 0;
			this.totalQueryTimeThisSec = 0;
		}
		this.thisSec = thisSec;
		synchronized (this) {
			this.notifyAll();
		}
	}

	void incrementPageCounter(long pageTimeMs) {
		synchronized (historySeconds) {
			long thisSec = System.currentTimeMillis() / 1000;
			if (thisSec != this.thisSec) {
				newSecond(thisSec);
			}
			this.totalPageTimeThisSec += pageTimeMs;
			this.pagesThisSec++;
			this.totalPageTimeThisMin += pageTimeMs;
			this.pagesThisMin++;
			this.totalPageTimeThisInterval += pageTimeMs;
			this.pagesThisInterval++;
			this.totalPageCount++;
		}
	}

	void incrementQueryCounter(long queryTimeMs) {
		synchronized (historySeconds) {
			long thisSec = System.currentTimeMillis() / 1000;
			if (thisSec != this.thisSec) {
				newSecond(thisSec);
			}
			this.totalQueryTimeThisSec += queryTimeMs;
			this.queriesThisSec++;
			this.totalQueryTimeThisMin += queryTimeMs;
			this.queriesThisMin++;
			this.totalQueryTimeThisInterval += queryTimeMs;
			this.queriesThisInterval++;
			this.totalQueryCount++;
		}
	}

	void addQueryTime(long queryTimeMs) {
		long thisSec = System.currentTimeMillis() / 1000;
		if (thisSec != this.thisSec) {
			newSecond(thisSec);
		}
		this.totalQueryTimeThisSec += queryTimeMs;
		this.totalQueryTimeThisInterval += queryTimeMs;
	}

	void decrementQueryCounter(long queryTimeMs) {
		synchronized (historySeconds) {
			this.totalQueryTimeThisSec -= queryTimeMs;
			this.queriesThisSec--;
			this.totalQueryTimeThisInterval -= queryTimeMs;
			this.queriesThisInterval--;
		}
	}

	Counters[] getCounterIntervals() {
		Counters[] ret = new Counters[3];
		ret[0] = getCounters(1);
		ret[1] = getCounters(10);
		ret[2] = getCounters(60);
		return ret;
	}

	List<Counters> getCounters() {
		LinkedList<Counters> ret;
		synchronized(countersHistory) {
			ret = new LinkedList<Counters>(countersHistory);
		}
		return ret;
	}

	Counters getCounters(int durationSec) {
		Counters ret = new Counters(sf);
		long thisSec = System.currentTimeMillis() / 1000;
		ret.setCounterDuration(durationSec);
		if (thisSec != this.thisSec) {
			newSecond(thisSec);
		}
		
		synchronized (historySeconds) {
			for(HistorySecond curHistorySecond : historySeconds) {
				if (curHistorySecond.second >= (thisSec - durationSec)) {
					ret.addHistorySecond(curHistorySecond);
				}
			}
		}
		ret.setUptime(SeeFusion.getUptime());
		ret.setActiveRequests(sf.getMasterRequestList().getCurrentRequestCount());

		return ret;
	}
	
	CountersHistory(int size, SeeFusion sf) {
		this.sf = sf;
		this.size = size;
		thread = ThreadPool.getPooledThread();
		thread.start(this);
		//debug("CountersHistory Constructor");
	}
	
	@Override
	public String getThreadName() {
		return "History Sleepythread";
	}
	
	Counters getMostRecentCounters() {
		return mostRecentCounters;
	}
		
	public void newMinute() {
		long now = (System.currentTimeMillis() / 60000) * 60000;
		synchronized (historySeconds) {
			mostRecentCounters = new Counters(now, 60, pagesThisMin, totalPageTimeThisMin,
					queriesThisMin, totalQueryTimeThisMin, sf); 
			synchronized (countersHistory) {
				countersHistory.addLast(mostRecentCounters);
				while (countersHistory.size() > this.size) {
					countersHistory.removeFirst();
				}
				publish(mostRecentCounters);
			}
			//debug("CountersHistory: notifying xml counters watchers");
			//sf.xmlBroadcaster.broadcast(new SimpleXml("history", mostRecentCounters));
			pagesThisMin = 0;
			totalPageTimeThisMin = 0;
			queriesThisMin = 0;
			totalQueryTimeThisMin = 0;
		}
		HistorySnapshot snap = new HistorySnapshot(now, sf);
		synchronized (historySnapshots) {
			historySnapshots.put(now, snap);
		}
	}
	
	void publish(Counters counters) {
		subjectImpl.notifyObservers(counters);		
	}

	@Override
	public void run() {
		while(running) {
			// wake 100ms after the top of each minute
			// (100ms buffer to prevent "double tick" timer problems in Windows)
			long now = System.currentTimeMillis();
			long msAfterMinBoundary = now - ((now / 60000) * 60000);
			long nextMinWait = 60000 - msAfterMinBoundary;
			Util.sleep(nextMinWait + 100);
			newMinute();
		}
	}
	
	@Override
	public void shutdown() {
		running=false;
		thread.interrupt();
	}

	public Map<Long, HistorySnapshot> getHistorySnapshots() {
		return historySnapshots;
	}

	/**
	 * @return Returns the totalPageCount.
	 */
	long getTotalPageCount() {
		return this.totalPageCount;
	}

	/**
	 * @return Returns the totalQueryCount.
	 */
	long getTotalQueryCount() {
		return this.totalQueryCount;
	}

	@Override
	public void addObserver(Observer<Counters> o) {
		subjectImpl.addObserver(o);
	}

	@Override
	public void removeObserver(Observer<Counters> o) {
		subjectImpl.removeObserver(o);
	}

}
