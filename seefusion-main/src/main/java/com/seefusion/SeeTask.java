package com.seefusion;

abstract class SeeTask implements Runnable {

	Thread taskThread;
	
	abstract String getThreadName();
	
	void setTaskThread(Thread t) {
		this.taskThread = t;
	}
	
	/**
	 * This should begin an orderly (asynchronous) shutdown of the thread.
	 */
	abstract void shutdown();
	
	void join() {
		try {
	        taskThread.join();
        }
        catch (InterruptedException e) {
	        Thread.interrupted();
        }
	}

}
