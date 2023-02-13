package com.seefusion;

import java.util.logging.Level;
import java.util.logging.Logger;

class PooledThread implements Runnable {

	private static final Logger LOG = Logger.getLogger(PooledThread.class.getName());
	
	private Thread thread;
	private SeeTask task;
	private Object joinSemaphore = new Object();
	
	PooledThread() {
		thread = new Thread(this);
		thread.setName("SeeFusion PooledThread");
		thread.setDaemon(true);
		thread.start();
	}
	
	void start(SeeTask task) {
		this.task = task;
		synchronized(this) {
			this.notify();
		}
	}
	
	@Override
	public void run() {
		try{
			while(true) {
				if(task == null) {
					// wait for someone to call start(task)
					synchronized(this) {
						try {
							wait();
						}
						catch (InterruptedException e) {
							// ignore
						}
					}
				}
				if(task != null) {
					thread.setName("SeeFusion " + task.getThreadName());
					task.run();
				}
				thread.setName("SeeFusion idle (pooled) thread");
				thread.setPriority(Thread.NORM_PRIORITY);
				synchronized(joinSemaphore) {
					// if any threads have join()ed this task, wake them up
					task = null;
					joinSemaphore.notifyAll();
				}
				ThreadPool.returnThread(this);
			}
		}
		catch(RuntimeException e) {
			LOG.log(Level.SEVERE, "Unexpected exception from SeeFusion " + task.getThreadName(), e);
			throw e;
		}
	}

	public void interrupt() {
		thread.interrupt();
	}

	public void join() throws InterruptedException {
		synchronized(joinSemaphore) {
			if(task != null) {
				// wait for the notifyAll() after the task completes
				joinSemaphore.wait();
			}
		}
		
	}
	public void join(int i) throws InterruptedException {
		synchronized(joinSemaphore) {
			if(task != null) {
				// wait for the notifyAll() after the task completes
				joinSemaphore.wait(i);
			}
		}
    }

	public void setPriority(int newPriority) {
		thread.setPriority(newPriority);
    }

	public SeeTask getTask() {
		return task;
	}


}
