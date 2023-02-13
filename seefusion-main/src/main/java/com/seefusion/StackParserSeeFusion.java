/**
 * 
 */
package com.seefusion;

import com.seestack.IStackParser;
import com.seestack.Method;
import com.seestack.SeeStackInfo;
import com.seestack.SeeThread;
import com.seestack.Threads;

/**
 * @author Daryl
 *
 */
public class StackParserSeeFusion implements IStackParser {

	private final ThreadStacks threads;

	StackParserSeeFusion(ThreadStacks pr) {
		this.threads = pr;
	}
	
	/* (non-Javadoc)
	 */
	@Override
	public Threads parseStack(SeeStackInfo info) {		
		Threads ret = new Threads();
		SeeThread curThread = null;
		for (ThreadStack ts : threads.values()) {
			curThread = new SeeThread(ts.threadName, ts.threadName, info.findThreadInfo(ts.threadName));
			curThread.setCount(ts.count);

			for (ThreadStackElement e : ts.stack) {
				curThread.addMethod(new Method(e.toString(), e.getClassName() + "." + e.getMethodName(),
						e.getFileName() + ":" + e.getLineNumber(), info));
			}

			ret.add(curThread);
		}
		return ret;
	}


}
