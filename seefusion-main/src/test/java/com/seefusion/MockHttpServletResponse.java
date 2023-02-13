package com.seefusion;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("unused")
class MockHttpServletResponse implements HttpServletResponse {

	private int bufferSize;
	private String characterEncoding;
	private int contentLength;
	private String contentType;
	private Locale locale;
	private MockServletOutputStream out;
	private int statusCode;
	private String statusName;
	
	public MockHttpServletResponse() {
		this.out = new MockServletOutputStream();
	}
	
	@Override
	public void flushBuffer() throws IOException {
		// noop
	}

	@Override
	public int getBufferSize() {
		return this.bufferSize;
	}

	@Override
	public String getCharacterEncoding() {
		return this.characterEncoding;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public Locale getLocale() {
		return this.locale;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return this.out;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(out);
	}

	@Override
	public boolean isCommitted() {
		return true;
	}

	@Override
	public void reset() {
		// noop
	}

	@Override
	public void resetBuffer() {
		// nop
	}

	@Override
	public void setBufferSize(int arg0) {
		this.bufferSize = arg0;
	}

	@Override
	public void setCharacterEncoding(String arg0) {
		this.characterEncoding = arg0;
	}

	@Override
	public void setContentLength(int arg0) {
		this.contentLength = arg0;
	}

	@Override
	public void setContentType(String arg0) {
		this.contentType = arg0;
	}

	@Override
	public void setLocale(Locale arg0) {
		this.locale = arg0;
	}

	public byte[] getBuffer() {
		return out.getBytes();
	}

	@Override
	public void addCookie(Cookie arg0) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void addDateHeader(String arg0, long arg1) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void addHeader(String arg0, String arg1) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void addIntHeader(String arg0, int arg1) {
		throw new RuntimeException("Unimplemented");
		
	}

	@Override
	public boolean containsHeader(String arg0) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public String encodeRedirectURL(String arg0) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public String encodeRedirectUrl(String arg0) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public String encodeURL(String arg0) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public String encodeUrl(String arg0) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public String getHeader(String arg0) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public Collection<String> getHeaderNames() {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public Collection<String> getHeaders(String arg0) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public int getStatus() {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void sendError(int arg0) throws IOException {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void sendError(int arg0, String arg1) throws IOException {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void sendRedirect(String arg0) throws IOException {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void setDateHeader(String arg0, long arg1) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void setHeader(String arg0, String arg1) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void setIntHeader(String arg0, int arg1) {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void setStatus(int status) {
		this.statusCode = status;
	}

	@Override
	public void setStatus(int statusCode, String statusName) {
		this.statusCode = statusCode;
		this.statusName = statusName;
	}

	@Override
	public void setContentLengthLong(long arg0) {
		throw new RuntimeException("Unimplemented");
	}

}
