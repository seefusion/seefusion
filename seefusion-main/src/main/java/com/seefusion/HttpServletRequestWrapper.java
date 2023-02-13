package com.seefusion;

import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

public class HttpServletRequestWrapper extends
		javax.servlet.http.HttpServletRequestWrapper {

	RequestInfo ri;
	
	public HttpServletRequestWrapper(RequestInfo ri, HttpServletRequest request) {
		super(request);
		this.ri = ri;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		// TODO: I'd like to get at least the first portion of post data if it's post, too
		return new InputStreamWrapper(ri, super.getInputStream());
	}


}
