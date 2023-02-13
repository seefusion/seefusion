/**
 * 
 */
package com.seefusion;

import java.util.logging.Logger;

/**
 * @author Daryl
 * 
 */
class CountersLogger extends SeeTask {
	
	private static final Logger LOG = Logger.getLogger(CountersLogger.class.getName());

	SeeFusion sf;

	long intervalMs;

	int intervalSec;

	PooledThread t;

	boolean isRunning = true;
	
	CountersLogger(SeeFusion sf) {
		this.sf = sf;
	}

	/*
	 * called when new logger thread started
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		CountersHistory hist = sf.getHistoryMinutes();
		try {
			hist.resetIntervalCounters();
			while (intervalSec > 0 && isRunning) {
				DbLogger dbLogger = sf.getDbLogger();
				if (dbLogger == null || !dbLogger.isRunning()) {
					LOG.warning("Unable to log counters, dbLogger not started.");
					intervalSec = 0;
				} else {
					dbLogger.log(sf.getHistoryMinutes().getIntervalCounters());
				}
				if (intervalSec > 0) {
					// try to fire just after the top of the second
					Util.sleep( (intervalMs - (System.currentTimeMillis() % 1000)) + 100);
				}
			}
		} finally {
			hist.resetIntervalCounters();
			t = null;
		}
	}

	void setIntervalSeconds(int seconds) {
		this.intervalSec = seconds;
		this.intervalMs = seconds * 1000;
		if (this.intervalSec > 0) {
			if (t == null) {
				t = ThreadPool.getPooledThread();
				t.start(this);
				LOG.info("Counters logger started");
			}
		}
		else if (t != null) {
			// stop the thread
			t.interrupt();
			try {
				t.join();
			} catch (InterruptedException e) {
				// ignore
			}
		}
	}

	@Override
	public String getThreadName() {
		return "CountersLogger " + sf.getInstanceName();
	}

	@Override
	void shutdown() {
		isRunning = false;
		t.interrupt();
	}

}
