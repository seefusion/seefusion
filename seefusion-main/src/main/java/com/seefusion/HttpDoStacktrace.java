/**
 * 
 */
package com.seefusion;

import java.io.IOException;
import java.util.Properties;

/**
 * @author Daryl
 * 
 */
class HttpDoStacktrace extends HttpRequestHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	@Override
	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	public String doGet(HttpTalker talker) throws IOException {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		Properties urlParams = talker.getUrlParams();
		String threadName = urlParams.getProperty("threadname");

		StringBuilder ret = new StringBuilder(2000);
		StackHelper stackHelper = StackHelper.getInstance();
		if (threadName == null || threadName.equals("")) {
			ret.append("<blockquote><pre>");
			ThreadStacks stacks = stackHelper.traceAll();
			for (ThreadStack stack : stacks.values()) {
				ret.append(stack.toString());
			}
			ret.append("</pre></blockquote>");
		}
		else {
			ret.append("<blockquote><pre>");
			stackHelper.traceOne(threadName);
			ret.append("</pre></blockquote>");
		}

		Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
		return ret.toString();
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
