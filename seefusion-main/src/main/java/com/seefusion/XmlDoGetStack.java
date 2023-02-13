package com.seefusion;

import java.util.Properties;

public class XmlDoGetStack extends XmlRequestHandler {

	@Override
	public SimpleXml doXml(HttpTalker talker)  {
		Properties urlParams = talker.getUrlParams();
		String filter = urlParams.getProperty("threadname");
		String requestID = urlParams.getProperty("requestid");
		boolean activeOnly = "true".equalsIgnoreCase(urlParams.getProperty("activeonly"));
		
		SeeFusion sf = talker.getSeeFusion();
		StackHelper stackHelper = StackHelper.getInstance();
		SimpleXml ret = new SimpleXml("stack");
		
		ThreadStacks stacks;
		stacks = stackHelper.traceAllFilteredExcept(Thread.currentThread().getName(), filter, activeOnly ? sf.getActiveThreadNames() : null);
		for (ThreadStack stack : stacks.values()) {
			SimpleXml thread = new SimpleXml("thread");
			thread.setAttribute("name", stack.threadName);
			thread.setCDATA(stack.toString());
			ret.add(thread);
		}
		if(requestID != null && !requestID.equals("")) {
			// ensure this request is still running
			if(!sf.getMasterRequestList().isRequestActive(requestID)) {
				ret.addTag("error", "Requested page already completed.  Showing current trace of that handler thread:");
			}
		}
		
		return ret;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
