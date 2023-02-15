/*
 * HttpTalker.java
 */

package com.seefusion;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * 
 * @author TheArchitect
 */
class HttpTalker extends SeeTask {

	private static final String CRLF = "\r\n";

	HttpRequestMap httpRequestMap;
	
	SeeFusion sf;

	Socket socket;

	PooledThread thread;

	ResourceBundle resources;

	JSONArray configJson;

	static final byte[] bCRLF = { 13, 10 };

	// strings from resource bundle
	MessageFormatFactory messageFormats;

	BufferedReader in;

	PrintWriter out;

	private OutputStream rawOut;

	String remoteAddr;
	
	int remotePort;

	boolean isAuthSometimesRequired;

	boolean flushAndClose = true;

	Object[] params = new Object[10];

	private HttpRequestHandler thisHttpRequest = null;
	
	private String responseCode = "200 OK";
	
	private String contentType = "text/html";

	protected HttpRequest httpRequest;

	// for tests
	HttpTalker(SeeFusion sf) throws IOException {
		this.sf = sf;
		this.httpRequestMap = HttpRequestMap.getInstance(sf.getWebroot());
		this.resources = sf.getResources();
		this.messageFormats = new MessageFormatFactory(resources);
		String configJsonRaw;
		try {
			configJsonRaw = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("com/seefusion/config.json"))).lines().collect(Collectors.joining("\n"));
		}
		catch (NullPointerException e) {
			// unit test?
			configJsonRaw = new BufferedReader(new InputStreamReader(new FileInputStream("./src/main/resources/com/seefusion/config.json"))).lines().collect(Collectors.joining("\n"));

		}
		this.configJson = new JSONArray(configJsonRaw);
	}

	HttpTalker(SeeFusion sf, Socket s) throws IOException {
		this.sf = sf;
		this.httpRequestMap = HttpRequestMap.getInstance(sf.getWebroot());
		this.resources = sf.getResources();
		this.messageFormats = new MessageFormatFactory(resources);
		InputStream configJson = Thread.currentThread().getContextClassLoader().getResourceAsStream("com/seefusion/config.json");
		if(configJson==null) {
			throw new IOException("Could not load config.json from \"com/seefusion/config.json\"");
		}
		String configJsonRaw = new BufferedReader(new InputStreamReader(configJson)).lines().collect(Collectors.joining("\n"));
		this.configJson = new JSONArray(configJsonRaw);
		this.remoteAddr = s.getInetAddress().getHostAddress();
		this.remotePort = s.getPort();
		socket = s;
		thread = ThreadPool.start(this);
	}

	void setResponseCode(String code) {
		this.responseCode = code;
	}

	void setContentType(String type) {
		this.contentType = type;
	}

	void setAutoFlush(boolean tf) {
		this.flushAndClose = tf;
	}

	@Override
	public void run() {
		// log("request from " + socket.getRemoteSocketAddress().toString() +
		// ":" + socket.getPort());
		in = null;
		out = null;
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			rawOut = new BufferedOutputStream(socket.getOutputStream());
			out = new PrintWriter(rawOut);

			if (sf.getIpWhitelist().contains(remoteAddr)) {
				StringBuilder result = new StringBuilder(1000);
				Object[] params = new Object[10];
				params[0] = remoteAddr;
				setResponseCode("403 Forbidden");
				appendHTTPHeaders(result);
				messageFormats.format("unauthorized", params, result);
				// appendFooter(result);
				out.print( result.toString() );
				out.flush();
				return;
			}

			String response = doHttp();
			if(!this.cancelResponse) {
				StringBuilder ret = new StringBuilder();
				appendHTTPHeaders(ret);
				ret.append(response);
				out.print(ret.toString());
			}
		}
		catch (IOException e) {
			// meh, just close the socket
		}
		catch (Exception e) {
			out.print("HTTP/1.0 500 Error\r\ncontent-type: text/html\r\n\r\n");
			out.print("<html><body><h3>Exception in page:</h3>\r\n<pre>");
			e.printStackTrace(out);
			flushAndClose = true;
		}
		finally {
			if (flushAndClose) {
				if (out != null) {
					out.flush();
					out.close();
				}
				try {
					if (in != null) in.close();
				}
				catch (IOException e) {
				}
				try {
					if (socket != null) socket.close();
				}
				catch (IOException e) {
				}
			}
		}
	}
	
	// let the try/catch in the calling method handle an exception if there is one
	private String doHttp() throws Exception, JSONException {
		StringBuilder ret = new StringBuilder(2000);
		try {
			httpRequest = new HttpRequest(sf, in);
			perm = httpRequest.getPerm();
			
			String pageName = httpRequest.getRequestLine().getPath().toLowerCase().replace('\\', '/');
			//debug("containsPage(" + pageName + "): " + httpRequestMap.containsPage(pageName));
			if (!httpRequestMap.containsPage(pageName)) {
				setResponseCode("404 Page Not Found");
				return("404 Page '" + Util.xmlStringFormat(pageName) + "' Not Found.");
			}
		
			HttpRequestHandler httpRequestHandler = httpRequestMap.getHttpRequest(pageName);
			Perm reqPerm = httpRequestHandler.getPerm();
			if (reqPerm.equals(Perm.NONE)){
				// no permissions req
				return httpRequestHandler.doGet(this);
			}
			else {
				// some sort of auth required, check auth flags
				//System.out.println("authReqFlags: " + authReqFlags + "; " + (userPermFlags & authReqFlags));
				if (perm.mayI(reqPerm)) {
					//Logger.debug("userperm, reqperm: ", userPerm, reqPerm);
					return httpRequestHandler.doGet(this);
				}
				else {
					authFailHttp(perm, reqPerm, httpRequest.getRequestLine().getPath());
				}
			}
		}
		catch(Exception e) {
			if(e instanceof ErrorMessage) {
				ErrorMessage em = (ErrorMessage)e;
				setResponseCode(em.getCode() + " " + em.getMessage());
				ret.append("<html><head><title>SeeFusion Error</title></head><body>");
				ret.append(em.getMessage());
			}
			else {
				setResponseCode("500 Server Error");
				ret.append("<html><head><title>SeeFusion Error</title></head><body>");
				ret.append("An error has occured processing your request.<pre>");
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				ret.append(sw.toString());
			}
			ret.append("</pre></body></html>");
		}
		return ret.toString();
	}

	private void authFailHttp(Perm userPerm, Perm reqPerm, String requestPath) {
		setResponseCode("403 Forbidden");
		// which permission(s) denied?
		Perm deniedPerms = userPerm.whyCantI(reqPerm);
		StringBuilder msg = new StringBuilder(1000);
		if (deniedPerms.has(Perm.LOGGEDIN)) {
			if (requestPath.equalsIgnoreCase("/logout")) {
				doMessage("Logged out.  <a href=\"/\">Log back in.</a>\r\n");
			}
			else {
				doAuth(out);
				return;
			}
		}

		if (deniedPerms.has(Perm.KILL)) {
			msg.append("You must be logged in with 'Kill' authorization to use this feature.\r\n");
		}
		if (deniedPerms.has(Perm.CONFIG)) {
			msg.append("You must be logged in with 'Config' authorization to use this feature.\r\n");
		}
		if (getHttpRequest().getRequestLine().getPath().equals("/dashboard/xml")) {
			SimpleXml response = new SimpleXml("dashboardinfo");
			response.addTag("name", sf.getInstanceName());
			response.addTag("status", "error");
			response.addTag("message", msg.toString());
			out.print(response.toString());
		}
		else {
			doMessage(msg.toString());
		}
	}

	Perm perm;

	private boolean cancelResponse = false;

	void serverSideRedirect(String pageName) throws IOException {
		thisHttpRequest = (httpRequestMap.getHttpRequest(pageName));
		if (thisHttpRequest == null) {
			doMessage("Error: redirect to page '" + pageName + " failed: page not found.");
		}
		else {
			thisHttpRequest.doGet(this);
		}
		this.cancelResponse  = true;
	}

	void doMessage(String message) {
		this.cancelResponse = true;
		StringBuilder ret = new StringBuilder();
		appendHTTPHeaders(ret);
		ret.append(message);
		out.print(ret.toString());
		out.flush();
	}

	void sendRedirect(String loc) {
		out.print("HTTP/1.0 302 Document Moved.\r\n");
		out.print("location: " + loc.replaceAll("[\r\n]", "") + "\r\n\r\n");
		out.flush();
	}

	void doAuth(PrintWriter out) {
		setResponseCode("400 Authentication required");
		//addHTTPHeader("WWW-Authenticate", "basic realm=\"SeeFusion\"\r\n");
		doMessage("A password is required.");
	}

	private LinkedHashMap<String, String> httpHeaders = new LinkedHashMap<String, String>();

	void addHTTPHeader(String name, String value) {
		httpHeaders.put(name, value);
	}

	private void appendHTTPHeaders(StringBuilder ret) {
		this.httpHeaders.put("cache-control", "no-cache");
		if(this.httpHeaders.containsKey("Content-Type")) {
			// noop
		}
		else if(this.responseCode.charAt(0) == '2') {
			addHTTPHeader("Content-Type", this.contentType + ";charset=UTF-8");
		}
		else {
			addHTTPHeader("Content-Type", "text/html;charset=UTF-8");
		}
		appendHTTPHeaders(ret, this.responseCode, this.httpHeaders);
	}
	
	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	void appendHTTPHeaders(StringBuilder ret, String responseCode, Map<String, String> httpHeaders) {
		SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
		ret.append("HTTP/1.0 ").append(responseCode).append(CRLF);
		ret.append("Server: SeeFusion ").append(SeeFusion.getVersion()).append(CRLF);
		ret.append("Date: ").append(df.format(new Date())).append(CRLF);
		ret.append("X-Frame-Options: deny\r\n");
		ret.append("X-Content-Type-Options: nosniff\r\n");
		ret.append("X-XSS-Protection: 1; mode=block\r\n");
		
		for(Map.Entry<String, String> kv : httpHeaders.entrySet()) {
			ret.append(kv.getKey()).append(": ").append(kv.getValue()).append(CRLF);
		}
		ret.append(CRLF);
	}
	
	String getOtherServerLinks() {
		List<ServerEntry> otherServersList = sf.getDashboardServersList();
		if (otherServersList == null) {
			return "";
		}
		StringBuilder ret = new StringBuilder(300);
		int i = 1;
		for(ServerEntry entry : otherServersList) {
			ret.append(" -- <a href=\"#server"
					+ Integer.toString(i)
					+ "\">"
					+ entry.getName()
					+ "</a>\r\n");
			++i;
		}
		return ret.toString();
	}

	List<ServerEntry> getOtherServersList() {
		return sf.getDashboardServersList();
	}

	String getRemoteHost() {
		return remoteAddr;
	}

	/**
	 * @return Returns isAuthSometimesRequired.
	 */
	boolean isAuthSometimesRequired() {
		return this.isAuthSometimesRequired;
	}

	/**
	 * @return Returns the messageFormats.
	 */
	MessageFormatFactory getMessageFormats() {
		return this.messageFormats;
	}

	/**
	 * @return Returns the out.
	 */
	PrintWriter getPrintWriter() {
		return this.out;
	}

	/**
	 * @return Returns the rawOut.
	 */
	OutputStream getOutputStream() {
		// if using OutputStream, don't try to prepend headers
		this.cancelResponse = true;
		return this.rawOut;
	}

	/**
	 * @return Returns the remoteAddr.
	 */
	String getRemoteAddr() {
		return this.remoteAddr;
	}

	/**
	 * @return Returns the resources.
	 */
	ResourceBundle getResourceBundle() {
		return this.resources;
	}

	/**
	 * @return Returns the sf.
	 */
	SeeFusion getSeeFusion() {
		return this.sf;
	}
	/**
	 * @return Returns the configJson template.
	 */
	JSONArray getConfigJson() {
		return this.configJson;
	}

	/**
	 * @return Returns the urlParams.
	 */
	Properties getUrlParams() {
		return this.httpRequest.getRequestLine().getQueryProperties();
	}

	@Override
	void shutdown() {
		try {
			socket.close();
		}
		catch (IOException e) {
			// ignore
		}
	}

	/**
	 * @return the remotePort
	 */
	int getRemotePort() {
		return this.remotePort;
	}

	@Override
	String getThreadName() {
	    return "Socket Talker " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

	HttpRequest getHttpRequest() {
		return httpRequest;
	}

	// this isn't really for caching/performance; it's so unit tests can force postData to be whatever they want it to be
	JSONObject postData = null;

	void setPostData(JSONObject postData) {
		this.postData = postData;
	}

	public JSONObject getPostData() {
		if(postData == null) {
			String body = getHttpRequest().getBody();
			if(body==null || body.length()==0) {
				throw new ErrorMessage("No item posted");
			}
			postData = new JSONObject(body);
		}
		return postData;
	}
	public boolean hasPostData() {
		if(postData != null) {
			return true;
		}
		String body = getHttpRequest().getBody();
		if(body==null || body.length()==0) {
			return false;
		}
		else {
			postData = new JSONObject(body);
		}
		return true;
	}

	public JSONObject getJsonData() {
		return getPostData();
	}

	public AuthTokenCache getAuthTokenCache() {
		return sf.getAuthTokenCache();
	}

	public JSONArray getPostDataArray() {
		String body = getHttpRequest().getBody();
		if(body==null || body.length()==0) {
			throw new ErrorMessage("No item posted");
		}
		return new JSONArray(body);
	}

}
