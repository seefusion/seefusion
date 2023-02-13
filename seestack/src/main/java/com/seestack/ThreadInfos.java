/**
 * 
 */
package com.seestack;

import java.util.LinkedList;

/**
 * @author Daryl
 *
 */
public class ThreadInfos extends LinkedList<ThreadInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	ThreadInfos() {
		super();
	}
	
	public ThreadInfo find(String threadName) {
		for(ThreadInfo info : this) {
			if(threadName.startsWith(info.namePrefix)) {
				return info;
			}
		}
		return null;
	}
	
}
