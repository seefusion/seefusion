/**
 * 
 */
package com.seefusion;

import java.util.List;
import java.util.Map;

/**
 * @author Daryl
 *
 */
public class StackHelperJava5 extends StackHelper {

	/* (non-Javadoc)
	 * @see com.seefusion.StackHelper#kill(java.lang.String)
	 */
	@SuppressWarnings("deprecation")
	@Override
	String kill(String threadName) {
		Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
		for (Map.Entry<Thread, StackTraceElement[]> threadMap : threads.entrySet()) {
			Thread thread = threadMap.getKey();
			if(thread.getName().equals(threadName)) {
				thread.stop();
				break;
			}
		}
		return "Thread.stop() called.";
	}

	/* (non-Javadoc)
	 * @see com.seefusion.StackHelper#traceAllFilteredExcept(java.lang.String, java.util.List, java.io.Writer, java.lang.String)
	 */
	@Override
	ThreadStacks traceAll() {
		ThreadStacks ret = new ThreadStacks();
		Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
		for (Map.Entry<Thread, StackTraceElement[]> thread : threads.entrySet()) {
			ret.add(new ThreadStack(thread.getKey(), thread.getValue()));
		}
		return ret;
	}
	/* (non-Javadoc)
	 * @see com.seefusion.StackHelper#traceAllFilteredExcept(java.lang.String, java.util.List, java.io.Writer, java.lang.String)
	 */
	@Override
	ThreadStacks traceAllFilteredExcept(String threadNameToSkip, String filter, List<String> threadNames) {
		ThreadStacks ret = new ThreadStacks();
		Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
		String lfilter = filter==null ? "" : filter.toLowerCase();
		for (Map.Entry<Thread, StackTraceElement[]> thread : threads.entrySet()) {
			String threadName = thread.getKey().getName(); 
			if(threadNames == null || threadNames.contains(threadName)) {
				if(threadName.toLowerCase().contains(lfilter)) {
					ret.add(new ThreadStack(thread.getKey(), thread.getValue()));
				}
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.seefusion.StackHelper#traceOne(java.lang.String, java.util.List, java.io.Writer)
	 */
	@Override
	ThreadStack traceOne(String targetThreadName) {
		return getThread(targetThreadName);
	}

	private ThreadStack getThread(String name) {
		Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
		for(Thread t : threads.keySet()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return new ThreadStack(t, threads.get(t));
			}
		}
		return null;
	}	
}
