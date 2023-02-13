package com.seefusion;

import java.util.LinkedList;

class ThreadPool {

	static LinkedList<PooledThread> pool = new LinkedList<PooledThread>();
	
	static PooledThread getPooledThread() {
		synchronized(pool) {
			if(pool.isEmpty()) {
				return new PooledThread();
			}
			return pool.removeFirst();
		}
	}
	
	static void returnThread(PooledThread t) {
		synchronized(pool) {
			pool.addLast(t);
		}
	}

	public static PooledThread start(SeeTask task) {
		PooledThread ret = getPooledThread();
		ret.start(task);
		return ret;
    }
	
}
