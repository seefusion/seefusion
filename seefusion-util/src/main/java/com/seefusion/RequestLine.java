package com.seefusion;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.StringTokenizer;

class RequestLine {
	
	private String requestMethod;
	private String uri;
	private String queryString;
	private String protocol;
	private String path;
	private String requestLineString;
	private Properties queryProperties;

	RequestLine(String requestLineString) {
		if (requestLineString == null) {
			throw new NullPointerException("Null argument passed to RequestLine constructor");
		}
		setRequestLineString(requestLineString);
		String[] parts = requestLineString.split(" ");
		//for(String part : parts) System.out.println(part);
		requestMethod = parts[0].toLowerCase();
		uri = parts[1];
		protocol = parts[2];
		
		int pos = uri.indexOf('?');
		if (pos != -1) {
			path = uri.substring(0, pos);
			queryString = uri.substring(pos+1, uri.length());
		}
		else {
			path = uri;
		}
	}
	
	private void setRequestLineString(String requestLineString) {
		this.requestLineString = requestLineString;
	}
	
	@Override
	public String toString() {
		return this.requestLineString;
	}


	String getRequestMethod() {
		return requestMethod;
	}
	
	String getPath() {
		return path;
	}
	String getQueryString() {
		return queryString;
	}
	String getProtocol() {
		return protocol;
	}
	
    Properties getQueryProperties() {
		if(this.queryProperties == null) {
			this.queryProperties = parseProperties(queryString);
		}
		return this.queryProperties;
	}
    
    static Properties parseProperties(String queryString) {
    	Properties ret = new Properties();
    	if(queryString != null && queryString.length() > 0) {
			StringTokenizer st = new StringTokenizer(queryString, "?&");
			String param;
			String name;
			String value;
			int pos;
			while (st.hasMoreTokens()) {
				param = st.nextToken();
				pos = param.indexOf('=');
				if (pos == -1) {
					name = param;
					value = "";
				}
				else if (pos == 0) {
					break;
				}
				else if (pos == param.length() - 1) {
					name = param.substring(0, param.length() - 1);
					value = "";
				}
				else {
					name = param.substring(0, pos);
					value = param.substring(pos + 1, param.length());
				}
				try {
					ret.put(URLDecoder.decode(name, "UTF-8"), URLDecoder.decode(value, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// will never happen
					e.printStackTrace();
				}
			}
	    }
		return ret;
    }
}
