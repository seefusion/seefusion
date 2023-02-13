package com.seefusion;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MockFilterChain implements FilterChain {

	private final String response;

	MockFilterChain(String response) {
		this.response = response;
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse response)
			throws IOException, ServletException {
		PrintWriter w = response.getWriter();
		w.print(this.response);
		w.flush();
	}

}
