package com.seefusion;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class HttpRequest {
	
	@SuppressWarnings({ "PMD.UnusedPrivateField", "unused" })
	private static final Logger LOG = Logger.getLogger(HttpRequest.class.getName());
	
	private RequestLine requestLine;
	private Properties headers = new Properties();
	private String body;
	private Perm perm;
	private AuthToken token;
	private final SeeFusion sf;
	private static final Perm PERM_NONE = new Perm(Perm.NONE).lock();
		
	public HttpRequest(SeeFusion sf, BufferedReader in) throws IOException {
		this.sf = sf;
		String requestLineString = in.readLine();
		this.requestLine = new RequestLine(requestLineString);
		
		//debug("pagename is '" + requestLine.getPath() + "'");

		headers = readHeaders(in);
		if(requestLine.getRequestMethod().equals("post")) {
			body = readBody(in);
		}

		this.perm = null;
		// debug("starting perm: " + this.perm);

		// check for HTTP authentication
		String headerValue;
		if ((headerValue = (String) headers.get(SeeFusion.AUTH_HEADER_NAME.toLowerCase())) != null) {
			// debug("Found " + SeeFusion.AUTH_HEADER_NAME + " header");
			this.token = sf.getAuthTokenCache().get(headerValue);
			//if(this.token==null) {
			//	debug("HttpRequest: session expired: " + headerValue);
			//}
			this.perm = this.token == null ? PERM_NONE : this.token.getPerm();
			// debug("perm set from " + SeeFusion.AUTH_HEADER_NAME + " header");
		}
		else if ((headerValue = (String) headers.get("authorization")) != null) {
			// debug("authorization header: " + headerValue);
			int pos = headerValue.toLowerCase().indexOf("basic");
			if (pos != -1 && headerValue.length() > (pos + 6)) {
				String authToken = new String(Base64.decode(headerValue.substring(pos + 6)));
				//System.out.println("authToken: " + authToken);
				// get password from user:pass token
				if ((pos = authToken.indexOf(':')) != -1) {
					if (pos < authToken.length() - 1) {
						String pass = authToken.substring(pos + 1);
						//System.out.println("pass: " + pass);
						this.perm = sf.getPerm(pass);
						//debug("perm set from auth header");
					}
				}
			}
		}
		else if((headerValue = (String) headers.get("cookie")) != null) {
			String[] cookies = headerValue.split(";[ ]*");
			for(String cookie : cookies) {
				if(cookie.length() > 12 && cookie.toLowerCase().substring(0, 11).equals("sfpassword=")) {
					this.perm = sf.getPerm(cookie.substring(11));
					// debug("perm set from cookie");
				}
			}
		}
		if(this.perm == null) {
			this.perm = sf.getPerm("");
		}
		// debug("request's effective perm: " + this.perm);
	}

	protected HttpRequest(SeeFusion sf, String requestLineString) {
		// used by unit test mock object
		this.perm = new Perm(Perm.LOGGEDIN, Perm.KILL, Perm.CONFIG);
		this.sf = sf;
		this.requestLine = new RequestLine(requestLineString);
	}

	private Properties readHeaders(BufferedReader in) throws IOException {
		Properties ret = new Properties();
		String curLine;
		while ((curLine = in.readLine()) != null) {
			if (curLine.equals("")) {
				break;
			}
			Util.parseKeyValue(curLine, ':', ret);
		}
		return ret;
	}

	private String readBody(BufferedReader in) throws IOException {
		if(headers.containsKey("content-length")){
			int len = Integer.parseInt(headers.getProperty("content-length"));
			char[] buf = new char[Math.min(16384, len)];
			in.read(buf);
			return new String(buf);
		}
		else {
			throw new ErrorMessage("No content-length in request headers");
		}
	}

	RequestLine getRequestLine() {
		return requestLine;
	}

	String getBody() {
		return body;
	}
	
	Perm getPerm() {
		return perm;
	}
	
	public void setPerm(Perm userPerm) {
		this.perm = userPerm;
	}

	Properties getHeaders() {
		return headers;
	}

	public void invalidateToken() {
		if(this.token != null) {
			sf.getAuthTokenCache().remove(this.token.getToken());
		}
	}
	
}