/*
 * Filter.java
 * The servlet filter that registers active web requests via SeeFusion.createRequest()
 * At the end of the request, the request is deregistered via RequestInfo.close()
 * This also appends debug output, if active
 */

package com.seefusion;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Daryl
 */
public class Filter implements javax.servlet.Filter {

	private static final Logger LOG = Logger.getLogger(Filter.class.getName());
	
	private SeeFusion sf;

	// used by unit tests
	SeeFusion getSeeFusion() {
		return sf;
	}
	
	/** Creates a new instance of Filter */
	public Filter() {
		// log("Filter:Create "+this.toString());
	}

	@Override
	public void doFilter(javax.servlet.ServletRequest request, javax.servlet.ServletResponse response,
			javax.servlet.FilterChain chain) throws IOException, javax.servlet.ServletException {
		// log("Filter:doFilter "+this.toString());
		// log("SeeFusion.Filter.doFilter("+sfInstance.getInstanceName()+":"+
		// this.sfInstance.toString() + ")");
		//log("Filter - requestURI: "+ ((javax.servlet.http.HttpServletRequest)request).getRequestURI());
		if (!sf.isGloballyEnabled()
			|| !(request instanceof HttpServletRequest)
			|| response instanceof HttpServletResponseWrapper) {
				chain.doFilter(request, response);
				return;
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		// DOS Protection
		if(sf.getDosManager().blocks(httpRequest.getRemoteAddr())) {
			if(response instanceof HttpServletResponse) {
				((HttpServletResponse)response).sendError(504, "Gateway Timeout");
			}
			return;
		}
		
		// RemoteAddr translation
		final String forwardedForHeader = sf.getConfig().getProperty("forwardedForHeader");
		final String remoteAddr;
		if(forwardedForHeader != null && !forwardedForHeader.isEmpty()) {
			int forwardedForPosition;
			String forwardedForPositionStr = sf.getConfig().getProperty("forwardedForPosition");
			if (forwardedForPositionStr == null || forwardedForPositionStr.isEmpty() || forwardedForPositionStr.equals("1")) {
				forwardedForPosition = 1;
			}
			else {
				try {
					forwardedForPosition = Integer.parseInt(forwardedForPositionStr);
				}
				catch (NumberFormatException e) {
					forwardedForPosition = 1;
				}
			}
			remoteAddr = getForwardedRemoteAddr(httpRequest, forwardedForHeader, forwardedForPosition);
		}
		else {
			remoteAddr = httpRequest.getRemoteAddr();
		}

		// Register the request with SeeFusion
		String pathInfo = (String) httpRequest.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);
		if(pathInfo == null || pathInfo.isEmpty()) {
			pathInfo = httpRequest.getHeader("xajp-path-info");
		}
		if(pathInfo == null || pathInfo.isEmpty()) {
			pathInfo = httpRequest.getPathInfo();
		}
		RequestInfo ri = sf.createRequest(
				httpRequest.getServerName()
				,httpRequest.getRequestURI()
				,httpRequest.getQueryString()
				,remoteAddr
				,httpRequest.getMethod()
				,pathInfo
				,httpRequest.isSecure()
		);
		String seeProfileHeader = httpRequest.getHeader("X-SeeProfile");
		if(seeProfileHeader != null) {
			ri.setProfileName(seeProfileHeader);
		}
		// wrap the request
		HttpServletRequestWrapper wrappedHttpRequest = new HttpServletRequestWrapper(ri, httpRequest);
		// wrap the response
		HttpServletResponseWrapper wrappedHttpResponse = new HttpServletResponseWrapper(ri, (HttpServletResponse) response);
		ri.setHttpServletResponse(wrappedHttpResponse);
		// turn on debug flag, if applicable
		boolean isDebugIP = sf.isDebugEnabled() && sf.isDebugIP(request.getRemoteAddr());
		if (isDebugIP) {
			ri.setDebugPage(true);
		}
		// pass the request through the chain
		try {
			chain.doFilter(wrappedHttpRequest, wrappedHttpResponse);
		}
		// log errors
		catch(IOException e) {
			ri.traceException(e);
			throw(e);
		}
		catch(javax.servlet.ServletException e) {
			ri.traceException(e);
			throw(e);
		}
		catch(SeeFusionKillError e) {
			//ignore
			throw(e);
		}
		catch(Error e) {
			LOG.log(Level.WARNING, "Page request threw an unchecked error! This usually reflects a severe problem with the application. ", e);
			ri.traceException(e);
			throw(e);
		}
		catch(RuntimeException e) {
			LOG.log(Level.WARNING, "Page request threw an unchecked exception!  This usually reflects a severe problem with the application. ", e);
			ri.traceException(e);
			throw(e);
		}
		finally {
			try {
				// unregister this page request from active requests list
				ri.close();
				// add debug output, if indicated
				if (isDebugIP) {
					boolean isDebugDebug = sf.isDebugDebug();
					if (httpRequest.getRequestURI().equalsIgnoreCase("/CFIDE/main/ide.cfm")) {
						if (isDebugDebug) {
							LOG.finer(httpRequest.getRequestURI() + " Debug not appended.");
						}
					}
					else {
						PrintWriterWrapper out1 = wrappedHttpResponse.seePrintWriter();
						ServletOutputStreamWrapper out2 = wrappedHttpResponse.seeOutputStream();
						try {
							String contentType = wrappedHttpResponse.getContentType();
							boolean isTextHtml = false;
							if (contentType != null && contentType.toLowerCase().startsWith("text/html")) {
								isTextHtml = true;
							}
							if (out1 == null && out2 == null) {
								if (isDebugDebug) {
									LOG.finer(httpRequest.getRequestURI()
											+ " failed.  Did not find a PrintWriter or ServletOutputStream to write to.");
								}
							}
							else if (isTextHtml) {
								if (this.sf.debugNeverSuffixList != null && this.sf.debugNeverSuffixList.hasMatch(httpRequest.getRequestURI())) {
									if(isDebugDebug) {
										LOG.finer(httpRequest.getRequestURI() + " has NeverDebugSuffix.");
									}
								}
								else {
									if (isDebugDebug) {
										LOG.finer(httpRequest.getRequestURI() + " Debug emitted - text/html");
									}
									if (out1 != null) {
										out1.write(ri.getDebugOutputHTML());
									}
									else {
										out2.write(ri.getDebugOutputHTML().getBytes());
									}
								}
							}
							else { // not text/html
								if (this.sf.debugSuffixList != null && this.sf.debugSuffixList.hasMatch(httpRequest.getRequestURI())) {
									if(isDebugDebug) {
										LOG.finer(httpRequest.getRequestURI() + " has debug suffix.");
									}
									if (out1 != null) {
										out1.write(ri.getDebugOutputHTML());
									}
									else {
										out2.write(ri.getDebugOutputHTML().getBytes());
									}
								}
								else if (isDebugDebug) {
									LOG.finer(httpRequest.getRequestURI() + " Not text/html, not debug suffix.");
								}

							}
						}
						catch (IOException e) {
							LOG.log(Level.WARNING, "Exception in request handling", e);
						}
						if (out1 != null) {
							out1.flush();
							// out1.seeClose();
						}
						else if (out2 != null) {
							out2.flush();
							// out2.seeClose();
						}
					}
				}
			}
			catch(IOException e) {
				LOG.log(Level.SEVERE, "Unexpected exception in com.seefusion.Filter", e);
				throw(e);
			}
			catch(Error e) {
				LOG.log(Level.SEVERE, "Unexpected exception in com.seefusion.Filter", e);
				throw(e);
			}
			catch(RuntimeException e) {
				LOG.log(Level.SEVERE, "Unexpected exception in com.seefusion.Filter", e);
				throw(e);
			}
		}
	}

	static String getForwardedRemoteAddr(HttpServletRequest httpRequest, String forwardedForHeader, int forwardedForPosition) {
		try {
			String headerValue = httpRequest.getHeader(forwardedForHeader);
			if(headerValue == null || headerValue.isEmpty()) {
				return httpRequest.getRemoteAddr();
			}
			String[] tokens = headerValue.split("[, =]+");
			return tokens[tokens.length - forwardedForPosition];
		}
		catch(Exception e) {
			return httpRequest.getRemoteAddr();
		}
	}

	@Override
	public void init(FilterConfig filterConfig) {
		this.sf = SeeFusion.getInstance();
	}

	@Override
	public void destroy() {
		sf.shutdown();
	}

}
