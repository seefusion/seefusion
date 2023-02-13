/*
 * PageInfo.java
 *
 */

package com.seefusion;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 
 */
class RequestInfo extends DaoObjectImpl implements Cloneable {

	private String id = UUID.randomUUID().toString();

	private SeeFusion sfInstance;

	private static ThreadLocal<RequestInfo> thisPage = new ThreadLocal<RequestInfo>();

	private long beginTime = System.currentTimeMillis();

	private long totalTime = -1;

	private boolean isActive = true;

	private QueryInfo queryInfo = null, longestQueryInfo = null;

	private int responseCode = -1;

	private long longestQueryTime = 0;

	private long queryTimeMs = 0;

	private int queryRows = 0;

	private long timeCompleted = -1;

	private long outputStartedTime = -1;

	private String serverName;

	private String requestURI;

	private String queryString;

	private String remoteIP;

	private String method;

	private String pathInfo;

	private boolean isSecure;

	private int queryCount = 0;

	private String requestKey;

	private Thread thread;

	private String threadName;

	private boolean killed = false;

	private boolean killstopped = false;

	private String trace = null;

	private long traceTime = beginTime;

	private long prevTraceTime = traceTime;

	private boolean hasAlreadyThrownADamnedException = false;

	private static Object nextRequestIDSemaphore = new Object();

	private volatile static long nextRequestID = 0;

	private static Map<String, Debugger> debuggers = new HashMap<String, Debugger>();

	private String pageURL = "";

	private String incidentID;

	private HashMap<String, Object> ruleStates = new HashMap<String, Object>();

	private String profileName;

	Object getRuleState(String ruleName) {
		return ruleStates.get(ruleName);
	}

	void setRuleState(String ruleName, Object obj) {
		ruleStates.put(ruleName, obj);
	}

	void removeRuleState(String ruleName) {
		ruleStates.remove(ruleName);
	}

	/**
	 * used by unit tests
	 */
	void setRequestInfo(RequestInfo ri) {
		thisPage.set(ri);
	}

	/**
	 * @return Returns the responseCode.
	 */
	int getResponseCode() {
		return this.responseCode;
	}

	/**
	 * @param responseCode
	 *            The responseCode to set.
	 */
	void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	// null constructor needed for Loggable support
	RequestInfo() {
	}

	@Override
	public Object clone() {
		RequestInfo ret = null;
		try {
			ret = (RequestInfo) super.clone();
			if(this.queryInfo != null) {
				ret.queryInfo = (QueryInfo) this.queryInfo.clone();
			}
			// longestQueryInfo is already cloned
			ret.setLongestQueryInfo(this.getLongestQueryInfo());
			ret.sfInstance = sfInstance;
		}
		catch (CloneNotSupportedException e) {
			// should never happen
		}
		return ret;
	}

	RequestInfo(SeeFusion sfInstance, String serverName, String requestURI, String queryString, String remoteIP,
			String method, String pathInfo, boolean isSecure) {
		this.sfInstance = sfInstance;
		this.setServerName(serverName);
		this.requestURI = requestURI;
		this.setQueryString(queryString);
		this.setRemoteIP(remoteIP);
		this.method = method;
		this.pathInfo = pathInfo;
		this.isSecure = isSecure;
		long thisRequestID;
		synchronized (nextRequestIDSemaphore) {
			thisRequestID = ++nextRequestID;
		}
		this.requestKey = Long.toString(thisRequestID);

		this.thread = Thread.currentThread();
		this.threadName = thread.getName();
		this.queryInfo = null;
		this.setLongestQueryInfo(null);
		thisPage.set(this);

		pageURL = requestURI;
		if(pathInfo != null) {
			pageURL += pathInfo;
		}
		if(queryString != null) {
			pageURL += "?" + queryString;
		}
		if(remoteIP == null || "".equals(remoteIP)) {
			// nop
		}
		else {
			pageURL = pageURL + " (" + remoteIP + ")";
		}

		Debugger debugger = getDebugger(remoteIP);
		// log("Debuggers: " + myDebuggers);
		if(debugger != null) {
			setDebugPage(true);
			DebugMessage dbm = new DebugMessage(this, DebugMessage.DEBUGTYPE_PAGEBOUNDARY, "Begin " + pageURL, null);
			debugger.debug(dbm);
		}
	}

	public RequestInfo(String id, String serverName) {
		this.id = id;
		this.serverName = serverName;
		this.beginTime = System.currentTimeMillis();
	}

	private Debugger getDebugger(String ip) {
		synchronized (debuggers) {
			if(debuggers.containsKey(ip)) {
				return debuggers.get(ip);
			}
		}
		return null;
	}

	/**
	 * @return Returns the incidentID.
	 */
	String getIncidentID() {
		return this.incidentID;
	}

	/**
	 * @param incidentID
	 *            The incidentID to set.
	 */
	void setIncidentID(String incidentID) {
		this.incidentID = incidentID;
	}

	boolean hasAlreadyThrownADamnedException() {
		return hasAlreadyThrownADamnedException;
	}

	void hasJustThrownADamnedException(boolean ornot) {
		hasAlreadyThrownADamnedException = ornot;
	}

	String getRequestKey() {
		return requestKey;
	}

	DebugMessage trace(String s) {
		DebugMessage ret = null;
		trace = s;
		prevTraceTime = traceTime;
		traceTime = System.currentTimeMillis();
		if(prevTraceTime == beginTime) {
			ret = new DebugMessage(this, DebugMessage.DEBUGTYPE_TRACE, "Trace: ", s);
		}
		else {
			ret = new DebugMessage(this, DebugMessage.DEBUGTYPE_TRACE,
					"Trace (" + (traceTime - prevTraceTime) + "ms interval): ", s);
		}
		if(this.isDebugPage) {
			this.addDebugOutput(ret);
		}
		if(killed) {
			if(killMessage == null) {
				throw new SeeFusionKillError();
			}
			else {
				throw new SeeFusionKillError(killMessage);
			}
		}
		return ret;
	}

	DebugMessage traceException(Throwable t) {
		if(responseCode == -1) {
			responseCode = 500;
		}
		DebugMessage ret = null;
		prevTraceTime = traceTime;
		traceTime = System.currentTimeMillis();
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));
		String s = sw.toString();
		if(prevTraceTime == beginTime) {
			ret = new DebugMessage(this, DebugMessage.DEBUGTYPE_TRACE, "Trace: ", s);
		}
		else {
			ret = new DebugMessage(this, DebugMessage.DEBUGTYPE_TRACE,
					"Trace (" + (traceTime - prevTraceTime) + "ms interval): ", s);
		}
		if(this.isDebugPage) {
			this.addDebugOutput(ret);
		}
		if(killed) {
			if(killMessage == null) {
				throw new SeeFusionKillError();
			}
			else {
				throw new SeeFusionKillError(killMessage);
			}
		}
		return ret;
	}

	String getTrace() {
		return trace;
	}

	long getTraceTime() {
		return traceTime;
	}

	String getTraceInfo() {
		if(trace == null) {
			return "";
		}
		return trace + " @" + Long.toString(traceTime - beginTime) + "ms";
	}

	String getRemoteIP() {
		return remoteIP;
	}

	String getURI() {
		return requestURI;
	}

	String killResult;

	String killMessage;

	void kill(String s) {
		killMessage = s;
		kill();
	}

	void kill() {
		killed = true;
		if(queryInfo != null && queryInfo.kill(killMessage)) {
			killResult = "Query cancelled.";
		}
		else {
			// this was commented out for some reason? maybe i wanted to hold
			// off until killStop() before interrupting.... meh
			thread.interrupt();
			killResult = "Page marked for cancellation.";
		}
	}

	void killStop() {
		killStop(null);
	}

	@SuppressWarnings("deprecation")
	void killStop(String s) {
		killed = true;
		if(killstopped) {
			thread.stop();
			Util.sleep(50);
			thread.interrupt();
			Util.sleep(50);
			thread.stop();
		}
		else {
			killstopped = true;
			if(s == null) {
				thread.stop();
			}
			else {
				thread.stop();
			}
			Util.sleep(50);
			thread.interrupt();
			Util.sleep(50);
			killResult = "Thread.stop() called.";
		}
	}

	String getKillResult() {
		return killResult;
	}

	void killOrKillStop() {
		killOrKillStop(null);
	}

	void killOrKillStop(String s) {
		if(!killed) {
			kill(s);
		}
		else {
			killStop(s);
		}
	}

	boolean isKilled() {
		return killed;
	}

	static void registerDebugger(Debugger debugger) {
		// 'debuggers' is a hashmap of (ipaddress) = (List of ip strings)
		String ip = debugger.getDebuggerIP();
		synchronized (debuggers) {
			debuggers.put(ip, debugger);
		}
	}

	static void unRegisterDebugger(Debugger debugger) {
		String ip = debugger.getDebuggerIP();
		synchronized (debuggers) {
			debugger.unRegistered();
			if(!debuggers.containsKey(ip)) {
				debuggers.remove(ip);
			}
		}
	}

	static void unRegisterDebugger(String ip) {
		synchronized (debuggers) {
			if(!debuggers.containsKey(ip)) {
				return;
			}
			Debugger debugger = debuggers.get(ip);
			unRegisterDebugger(debugger);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("PageInfo: serverName=").append(this.getServerName()).append(";requestURI=").append(this.requestURI);
		sb.append(";queryString=").append(this.getQueryString()).append(";remoteIP=").append(this.getRemoteIP());
		sb.append(";requestKey=").append(this.requestKey);
		return sb.toString();
	}

	static RequestInfo getCurrent() {
		return thisPage.get();
	}

	long getElapsedTime() {
		if(totalTime != -1) {
			return totalTime;
		}
		else {
			return System.currentTimeMillis() - beginTime;
		}
	}

	long getEndTime() {
		if(totalTime == -1) {
			return 0;
		}
		else {
			return beginTime + totalTime;
		}
	}

	long getBeginTime() {
		return beginTime;
	}

	void outputStarted() {
		if(this.getOutputStartedTime() == -1) {
			this.setOutputStartedTime(System.currentTimeMillis());
		}
	}

	boolean isActive() {
		return isActive;
	}

	void setInactive() {
		if(isActive) {
			this.thread = null;
			if(queryInfo != null && queryInfo.isActive()) {
				try {
					queryInfo.setInactive();
				}
				catch (SQLException e) {
					// ignore
				}
				queryInfo.setException("Warning: Statement not properly closed.");
			}
			totalTime = System.currentTimeMillis() - beginTime;
			setTimeCompleted(System.currentTimeMillis());
			if(this.isDebugPage) {
				// log("Total page time " + totalTime + "ms; Total Queries: " +
				// getQueryCount() + "; Total Query Time: "+ getQueryTime() +
				// "ms");
				long queryCount = getQueryCount();
				DebugMessage dbm = new DebugMessage(this, DebugMessage.DEBUGTYPE_PAGEBOUNDARY, null, null);
				if(queryCount > 0) {
					dbm.setTitle("Total page time " + totalTime + "ms; total queries: " + queryCount + " (total rows "
							+ getQueryRows() + "); total query time: " + getQueryTime() + "ms ("
							+ Long.toString((long) (((float) getQueryTime() / (float) totalTime) * 100.0)) + "%)");
				}
				else {
					dbm.setTitle("Total page time " + totalTime + "ms; no queries.");
				}
				this.addDebugOutput(dbm);
			}
		}
		isActive = false;
		// remove this page from threadlocal map
		thisPage.set(null);
	}

	protected int resultsetCount = 0;

	void logQuery(QueryInfo qi, long ms) {
		// new Exception().printStackTrace();
		if(ms >= longestQueryTime) {
			longestQueryTime = ms;
			// protect against QueryInfo reuse (eg when executeBatch() )
			setLongestQueryInfo((QueryInfo) qi.clone());
		}
		// log query if desired
		long dbLoggerQueryThreshold = sfInstance.getDbLoggerQueryThreshold();
		// Logger.log("..." + sfInstance.isLogQueryExceptions() + "," +
		// qi.hasException());
		if((dbLoggerQueryThreshold != 0 && (ms >= dbLoggerQueryThreshold || dbLoggerQueryThreshold == 1))
				|| (sfInstance.isLogQueryExceptions() && qi.hasException())) {
			DbLogger dbLogger = sfInstance.getDbLogger();
			if(dbLogger != null) {
				dbLogger.log(qi);
			}
		}
		// gen debug output if applicable
		if(this.isDebugPage) {
			String stack = qi.getStack();
			if(stack == null || stack.length() == 0) {
				stack = "(No debugStackTargets found.)\r\n";
			}
			ResultSetImpl rs = qi.getResultSet();
			if(rs != null) {
				queryRows += rs.sfGetRowCount();
			}
			if(qi.isSimpleQuery()) {
				if(rs != null) {
					String title = "Query: " + qi.getQueryElapsedTime() + "ms + Resultset: " + rs.sfGetElapsedTime()
							+ "ms, " + rs.sfGetRowCount() + "rows:\r\n" + stack;
					addDebugOutput(new DebugMessage(this, DebugMessage.DEBUGTYPE_QUERY, title, qi.getQueryText()));
				}
			}
			else if(rs != null) {
				String title = "ResultSet " + resultsetCount + ": " + rs.sfGetElapsedTime() + "ms, "
						+ rs.sfGetRowCount() + "rows";
				addDebugOutput(new DebugMessage(this, DebugMessage.DEBUGTYPE_QUERY, title, null));
			}
			else {
				String title = "Query, " + ms + "ms:\r\n" + stack;
				addDebugOutput(new DebugMessage(this, DebugMessage.DEBUGTYPE_QUERY, title, qi.getQueryText()));
			}

		}
	}

	void addQueryTime(QueryInfo qi) {
		// logException("debug:",new Exception());
		// update stats
		long ms = qi.getElapsedTime();
		sfInstance.getHistoryMinutes().incrementQueryCounter(ms);
		setQueryCount(getQueryCount() + 1);
		setQueryTimeMs(getQueryTimeMs() + ms);
		logQuery(qi, ms);
		resultsetCount = 0;
	}

	void addResultSetTime(ResultSetImpl rs) {
		QueryInfo qi = getQueryInfo();
		// log(rs.sfGetRowCount() + " QueryRows added");
		if(qi.isSimpleQuery()) {
			long ms = qi.getElapsedTime();
			sfInstance.getHistoryMinutes().incrementQueryCounter(ms);
			setQueryCount(getQueryCount() + 1);
			setQueryTimeMs(getQueryTimeMs() + ms);
			logQuery(qi, qi.getElapsedTime());
			qi.setSimpleQuery(false);
			resultsetCount = 1;
		}
		else {
			long ms = rs.sfGetElapsedTime();
			sfInstance.getHistoryMinutes().addQueryTime(ms);
			setQueryTimeMs(getQueryTimeMs() + ms);
			resultsetCount++;
			logQuery(qi, rs.sfGetElapsedTime());
		}
		// sfInstance.addResultSetTime(ms);
		// sfInstance.incrementResultSetCounter(ms);
		// resultSetCount++;
	}

	long getQueryTime() {
		return getQueryTimeMs();
	}

	int getQueryCount() {
		return queryCount;
	}

	int getQueryRows() {
		return queryRows;
	}

	long getTimeToOutput() {
		if(getOutputStartedTime() == -1) {
			return 0;
		}
		else {
			return getOutputStartedTime() - beginTime;
		}
	}

	Object[] getInfoDashboardParams() {
		Object[] params = new Object[6];

		params[0] = new Long(getElapsedTime());
		if(getQueryCount() > 0) {
			params[1] = new Long(getQueryTimeMs());
			params[2] = new Integer(getQueryCount());
		}
		else {
			params[1] = "--";
			params[2] = "--";
		}

		if(queryInfo != null && queryInfo.isActive()) {
			params[3] = new Long(queryInfo.getElapsedTime());
			params[4] = new Integer(queryInfo.getResultCount());
		}
		else {
			params[3] = "--";
			params[4] = "--";
		}
		params[5] = getRequestString();
		return params;

	}

	String getInfoDashboard(MessageFormatFactory messageFormats, String messageFormat,
			boolean isListingRunningRequests) {
		if(isListingRunningRequests) {
			if(!thread.isAlive()) {
				sfInstance.releaseRequest(this);
				return "";
			}
		}
		Object[] params = getInfoDashboardParams();
		params[5] = Util.showHide((String) params[5], 60);
		StringBuilder info = new StringBuilder();
		messageFormats.format(messageFormat, params, info);
		return info.toString();
	}

	void addInfoDashboardXml(SimpleXml xml, boolean isListingRunningRequests) {
		if(isListingRunningRequests) {
			if(!thread.isAlive()) {
				sfInstance.releaseRequest(this);
				return;
			}
		}
		Object[] params = getInfoDashboardParams();

		xml.add(new SimpleXml("longestpagetime", params[0].toString()));
		xml.add(new SimpleXml("totalquerytime", params[1].toString()));
		xml.add(new SimpleXml("totalquerycount", params[2].toString()));
		xml.add(new SimpleXml("longestquerytime", params[3].toString()));
		xml.add(new SimpleXml("longestqueryrows", params[4].toString()));
		xml.add(new SimpleXml("requesturi", params[5].toString()));
	}

	String getRequestString() {
		if(getQueryString() != null) {
			return getServerName() + requestURI + '?' + getQueryString();
		}
		else {
			return getServerName() + requestURI;
		}
	}

	/**
	 * Getter for property queryInfo.
	 * 
	 * @return Value of property queryInfo.
	 */
	QueryInfo getQueryInfo() {
		return queryInfo;
	}

	/**
	 * Setter for property queryInfo.
	 * 
	 * @param queryInfo
	 *            New value of property queryInfo.
	 */
	void setQueryInfo(com.seefusion.QueryInfo queryInfo) {
		// Logger.log("setQueryInfo " + queryInfo)
		this.queryInfo = queryInfo;
	}

	boolean isDebugPage = false;

	@SuppressWarnings("PMD.AvoidStringBufferField")
	StringBuilder debugOutput;

	boolean isDebugPage() {
		return isDebugPage;
	}

	void setDebugPage(boolean tf) {
		isDebugPage = tf;
		if(tf) {
			debugOutput = new StringBuilder(2000);
		}
	}

	String getRawDebugOutput() {
		if(debugOutput == null)
			return "";
		return debugOutput.toString();
	}

	String getDebugOutputHTML() {
		return "</table></table></table></table></center></p></div></ol></ul><hr><b>SeeFusion " + SeeFusion.getVersion()
				+ " debug output -- " + sfInstance.getInstanceName() + "</b><hr>" + getRawDebugOutput();
	}

	void addDebugOutput(DebugMessage dbm) {
		if(isDebugPage) {
			this.debugOutput.append(dbm.toString()).append("<hr>\r\n");
			// log("Debuggers: "+myDebuggers);
			if(!debuggers.isEmpty()) {
				Debugger debugger;
				for (Map.Entry<String, Debugger> item : debuggers.entrySet()) {
					debugger = item.getValue();
					// log("Sending debug output to listener at
					// "+talker.getDebuggerIP());
					debugger.debug(dbm);
				}
			}
		}
	}

	SeeFusion getSeeFusion() {
		return this.sfInstance;
	}

	void close() {
		// log("pi.close()");
		thisPage.set(null);
		sfInstance.releaseRequest(this);
	}

	private InputStreamWrapper inputStreamWrapper;

	private HttpServletResponseWrapper wrappedHttpResponse;

	public InputStreamWrapper getInputStreamWrapper() {
		return inputStreamWrapper;
	}

	/**
	 * @return Returns the requestURI.
	 */
	String getRequestURI() {
		return this.requestURI;
	}

	/**
	 * @param requestURI
	 *            The requestURI to set.
	 */
	void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	/**
	 * @return Returns the pageURL.
	 */
	String getPageURL() {
		return this.pageURL;
	}

	/**
	 * @param pageURL
	 *            The pageURL to set.
	 */
	void setPageURL(String pageURL) {
		this.pageURL = pageURL;
	}

	Thread getThread() {
		return thread;
	}

	String getThreadName() {
		return threadName;
	}

	void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	String getProfileName() {
		return profileName;
	}

	public static void addNullInfoDashboardXml(SimpleXml xml) {
		xml.add(new SimpleXml("longestpagetime"));
		xml.add(new SimpleXml("totalquerytime"));
		xml.add(new SimpleXml("totalquerycount"));
		xml.add(new SimpleXml("longestquerytime"));
		xml.add(new SimpleXml("longestqueryrows"));
		xml.add(new SimpleXml("requesturi"));
	}

	public byte[] getResponseBytes() {
		return wrappedHttpResponse.getBytes();
	}

	public void setInputStreamWrapper(InputStreamWrapper inputStreamWrapper) {
		this.inputStreamWrapper = inputStreamWrapper;
	}

	public void setHttpServletResponse(HttpServletResponseWrapper wrappedHttpResponse) {
		this.wrappedHttpResponse = wrappedHttpResponse;
	}

	public QueryInfo getLongestQueryInfo() {
		return longestQueryInfo;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void addInfoDashboardJson(JSONObject ret, boolean isListingRunningRequests) {
		if(isListingRunningRequests) {
			if(!thread.isAlive()) {
				sfInstance.releaseRequest(this);
				return;
			}
		}
		Object[] params = getInfoDashboardParams();

		ret.put("longestpagetime", params[0].toString());
		ret.put("totalquerytime", params[1].toString());
		ret.put("totalquerycount", params[2].toString());
		ret.put("longestquerytime", params[3].toString());
		ret.put("longestqueryrows", params[4].toString());
		ret.put("requesturi", params[5].toString());

	}

	public SimpleXml toXml() {
		SimpleXml ret = new SimpleXml("request");
		StringBuilder url = new StringBuilder(getServerName());
		url.append(requestURI);
		if(pathInfo != null) {
			url.append(pathInfo);
		}
		if(getQueryString() != null) {
			url.append('?').append(getQueryString());
			ret.addTag("queryString", getQueryString());
		}
		else {
			ret.addTag("queryString", "");
		}
		ret.addTag("url", url.toString());
		ret.addTag("pid", requestKey);
		ret.addTag("requestURI", requestURI);
		ret.addTag("serverName", getServerName());
		ret.addTag("method", method);
		ret.addTag("pathInfo", pathInfo == null ? "" : pathInfo);
		ret.addTag("isSecure", isSecure);
		ret.addTag("threadName", getThreadName());
		if(isActive) {
			if(this.isKilled()) {
				ret.addTag("killType", "killstop");
				ret.addTag("killUrl", "/json/killstop?pid=" + this.requestKey);
			}
			else {
				ret.addTag("killType", "kill");
				ret.addTag("killUrl", "/json/kill?pid=" + this.requestKey);
			}
		}
		ret.addTag("ip", getRemoteIP());
		ret.addTag("time", getElapsedTime());
		ret.addTag("trace", getTraceInfo());
		if(this.isActive) {
			StackHelper stackHelper = StackHelper.getInstance();
			ThreadStack stack = stackHelper.traceOne(getThreadName());
			ret.addTagCdata("stack", stack.toString());
		}
		ret.addTag("queryTime", getQueryTimeMs());
		ret.addTag("queryCount", getQueryCount());
		if(getOutputStartedTime() != -1) {
			ret.addTag("timeToFirstByte", getTimeToOutput());
		}
		if(responseCode != -1) {
			ret.addTag("responseCode", responseCode);
		}
		else {
			ret.addTag("responseCode", "");
		}
		if(getTimeCompleted() != -1) {
			ret.addTag("completed", getTimeCompleted()); // js will sort w/ this
			ret.addTag("completedAgoMs", System.currentTimeMillis() - getTimeCompleted());
		}
		else {
			ret.addTag("completed", "");
			ret.addTag("completedAgoMs", "");
		}

		boolean longQueryActive = false;
		long longQueryElapsed = 0;
		int longQueryRows = 0;
		String longQuerySql = "";
		if(queryInfo != null) {
			if(queryInfo.isActive() || getLongestQueryInfo() == null) {
				longQueryActive = true;
				longQueryElapsed = queryInfo.getElapsedTime();
				longQueryRows = queryInfo.getResultCount();
				longQuerySql = queryInfo.getQueryText();
			}
			else {
				longQueryActive = false;
				longQueryElapsed = getLongestQueryInfo().getElapsedTime();
				longQueryRows = getLongestQueryInfo().getResultCount();
				longQuerySql = getLongestQueryInfo().getQueryText();
			}
		}
		ret.addTag("longQueryActive", longQueryActive);
		ret.addTag("longQueryElapsed", longQueryElapsed);
		ret.addTag("longQueryRows", longQueryRows);
		ret.addTag("longQuerySql", longQuerySql);
		return ret;
	}

	@Override
	public JSONObject toJson() {
		JSONObject ret = new JSONObject();
		StringBuilder url = new StringBuilder(getServerName());
		url.append(requestURI);
		if(pathInfo != null) {
			url.append(pathInfo);
		}
		if(getQueryString() != null) {
			url.append('?').append(getQueryString());
			ret.put("queryString", getQueryString());
		}
		else {
			ret.put("queryString", "");
		}
		ret.put("pid", requestKey);
		ret.put("url", url.toString());
		ret.put("requestURI", requestURI);
		ret.put("serverName", getServerName());
		ret.put("method", method);
		ret.put("pathInfo", pathInfo == null ? "" : pathInfo);
		ret.put("isSecure", isSecure);
		ret.put("threadName", getThreadName());
		ret.put("ip", getRemoteIP());
		ret.put("time", getElapsedTime());
		ret.put("trace", getTraceInfo());
		if(this.isActive) {
			StackHelper stackHelper = StackHelper.getInstance();
			ThreadStack stack = stackHelper.traceOne(getThreadName());
			if(stack != null) {
				ret.put("stack", stack.toString());
			}
		}
		ret.put("queryTime", getQueryTimeMs());
		ret.put("queryCount", getQueryCount());
		if(getOutputStartedTime() != -1) {
			ret.put("timeToFirstByte", getTimeToOutput());
		}
		if(responseCode != -1) {
			ret.put("responseCode", responseCode);
		}
		else {
			ret.put("responseCode", "");
		}
		if(getTimeCompleted() != -1) {
			ret.put("completed", getTimeCompleted()); // js will sort w/ this
			ret.put("completedAgoMs", System.currentTimeMillis() - getTimeCompleted());
		}
		else {
			ret.put("completed", "");
			ret.put("completedAgoMs", "");
		}

		boolean longQueryActive = false;
		long longQueryElapsed = 0;
		int longQueryRows = 0;
		String longQuerySql = "";
		if(queryInfo != null) {
			if(queryInfo.isActive() || getLongestQueryInfo() == null) {
				longQueryActive = true;
				longQueryElapsed = queryInfo.getElapsedTime();
				longQueryRows = queryInfo.getResultCount();
				longQuerySql = queryInfo.getQueryText();
			}
			else {
				longQueryActive = false;
				longQueryElapsed = getLongestQueryInfo().getElapsedTime();
				longQueryRows = getLongestQueryInfo().getResultCount();
				longQuerySql = getLongestQueryInfo().getQueryText();
			}
		}
		ret.put("longQueryActive", longQueryActive);
		ret.put("longQueryElapsed", longQueryElapsed);
		ret.put("longQueryRows", longQueryRows);
		ret.put("longQuerySql", longQuerySql);
		ret.put("incidentID", incidentID);
		return ret;
	}

	@Override
	public String getId() {
		return id;
	}

	public void setBeginTime(long time) {
		this.beginTime = time;
	}

	public long getTimeCompleted() {
		return timeCompleted;
	}

	public void setTimeCompleted(long timeCompleted) {
		this.timeCompleted = timeCompleted;
	}

	public void setRemoteIP(String remoteIP) {
		this.remoteIP = remoteIP;
	}

	public long getOutputStartedTime() {
		return outputStartedTime;
	}

	public void setOutputStartedTime(long outputStartedTime) {
		this.outputStartedTime = outputStartedTime;
	}

	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}

	public long getQueryTimeMs() {
		return queryTimeMs;
	}

	public void setQueryTimeMs(long queryTimeMs) {
		this.queryTimeMs = queryTimeMs;
	}

	public void setLongestQueryInfo(QueryInfo longestQueryInfo) {
		this.longestQueryInfo = longestQueryInfo;
	}

	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public void formatEmailHtmlRow(SeeFusion sf, MessageFormatFactory messageFormats, String messageName, boolean isActiveRequests, boolean altRowStyle, StringBuilder stringBuilder) {
		int i=0;
		Object[] params = new Object[20];

		// style
		params[i++] = altRowStyle ? "even" : "odd";

		// Request
		if(Util.isFrameworkActionParamSet()) {
			String frameworkAction = Util.findFrameworkAction(queryString);
			if(frameworkAction != null) {
				params[i++] = frameworkAction + "(" + serverName + requestURI + ")";
			}
			else {
				params[i++] = serverName + requestURI;
			}
		}
		else {
			params[i++] = serverName + requestURI;
		}
		
		// Completed
		if(timeCompleted != -1) {
			if(sf.isDisplayRelativeTimes) {
				params[i++] = "-" + Util.msFormat(System.currentTimeMillis() - timeCompleted);
			}
			else {
				params[i++] = df.format(new Date(timeCompleted));
			}
		}
		else {
			params[i++] = getTraceInfo();
		}
		
		// ip
		params[i++] = remoteIP;

		// Response (200@50ms)
		if(responseCode == -1) {
			params[i++] = "--";
		}
		else {
			params[i++] = new Integer(responseCode);
		}
		if(outputStartedTime == -1) {
			params[i++] = "--";
		}
		else {
			params[i++] = new Long(getTimeToOutput());
		}
		
		// Elapsed
		params[i++] = new Long(getElapsedTime());
		
		// Queries and Qry Time
		if(queryCount > 0) {
			params[i++] = new Integer(queryCount);
			params[i++] = new Long(queryTimeMs);
		}
		else {
			params[i++] = "--";
			params[i++] = "--";
		}

		// Long Qry, Rows, SQL
		String sql;
		if(queryInfo != null) {
			params[i++] = "<em>" + queryInfo.getElapsedTime() + "</em>";
			params[i++] = "<em>" + queryInfo.getResultCount() + "</em>";
			sql = Util.xmlStringFormat(queryInfo.getQueryText());
		}
		else if(longestQueryInfo != null) {
			params[i++] = longestQueryInfo.getElapsedTime();
			params[i++] = longestQueryInfo.getResultCount();
			sql = Util.xmlStringFormat(longestQueryInfo.getQueryText());
		}
		else {
			params[i++] = "";
			params[i++] = "";
			sql = null;
		}
		messageFormats.format(messageName, params, stringBuilder);
		if (sql != null) {
			params[1] = sql;
			messageFormats.format("reqsql", params, stringBuilder);
		}
	}

	public void setPathInfo(String pathInfo) {
		this.pathInfo = pathInfo;
	}

}
