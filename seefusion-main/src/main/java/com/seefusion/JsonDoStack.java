package com.seefusion;

import java.io.IOException;
import java.util.Properties;

import com.seestack.SeeStack;

public class JsonDoStack extends JsonRequestHandler {

	@Override
	public JSONObject doJson(HttpTalker talker)  {
		Properties urlParams = talker.getUrlParams();
		String filter = urlParams.getProperty("threadname");
		String requestID = urlParams.getProperty("requestid");
		boolean activeOnly = "true".equalsIgnoreCase(urlParams.getProperty("activeonly"));
		
		SeeFusion sf = talker.getSeeFusion();
		JSONObject ret = new JSONObject();
		
		if(!requestID.equals("")) {
			return doJson(sf, requestID);
		}
		
		try {
			StackHelper stackHelper = StackHelper.getInstance();
			ThreadStacks stacks;

			stacks = stackHelper.traceAllFilteredExcept(Thread.currentThread().getName(), filter, activeOnly ? sf.getActiveThreadNames() : null);
			StringBuilder rawStack = new StringBuilder();
			for (ThreadStack stack : stacks.values()) {
				rawStack.append(stack.toString());
			}
			ret.put("rawstack", rawStack.toString());
			
			Config config = talker.getSeeFusion().getConfig();
			SeeStack ss = new SeeStack(config.getConfigDir(), config.getProperty("localPackages"));
			JSONObject analysis = ss.process(new StackParserSeeFusion(stacks)).toJson();
			ret.put("seestack", analysis);
		} catch (IOException e) {
			throw new ErrorMessage("Unable to generate stack trace: " + e.toString());
		}
		return ret;
	}

	JSONObject doJson(SeeFusion sf, String requestID) {
		JSONObject ret = new JSONObject();
		RequestInfo pi = sf.getMasterRequestList().getActiveRequest(requestID);

		if(pi==null) {
			StringBuilder sb = new StringBuilder();
			String comma = "";
			for(String key : sf.getMasterRequestList().currentRequests.keySet()) {
				sb.append(comma).append(key);
				comma = ",";
			}
			ret.put("message", "Request is no longer available.");
			return ret;
		}
		
		StackHelper stackHelper = StackHelper.getInstance();
		ThreadStack stack = stackHelper.traceOne(pi.getThreadName());
		ret.put("rawstack", stack.toString());
		ThreadStacks threadStacks = new ThreadStacks();
		threadStacks.add(stack);

		Config config = sf.getConfig();
		try {
			SeeStack ss = new SeeStack(config.getConfigDir(), config.getProperty("localPackages"));
			JSONObject analysis = ss.process(new StackParserSeeFusion(threadStacks)).toJson();
			ret.put("seestack", analysis);
		}
		catch (IOException ex) {
			throw new ErrorMessage("Unable to load seestack metadata");
		}

		return ret;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}

}
