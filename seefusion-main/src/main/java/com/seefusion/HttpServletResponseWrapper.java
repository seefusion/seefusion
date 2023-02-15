/*
 * HttpServletResponseWrapper
 * 
 * Used to monitor http response start time (relative to request start) and output size
 */

package com.seefusion;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;


/**
 *
 * @author  Daryl
 */
class HttpServletResponseWrapper extends javax.servlet.http.HttpServletResponseWrapper {
    
    private RequestInfo pi;
    private String contentType = null;
    private ServletOutputStreamWrapper outputStream = null;
    private PrintWriterWrapper printWriter = null;
    
    public HttpServletResponseWrapper(RequestInfo pi, javax.servlet.http.HttpServletResponse response) throws IOException  {
        super(response);
        this.pi = pi;
    }

    public byte[] getBytes() {
    	if(outputStream != null) {
    		return outputStream.getBytes();
    	}
    	else if (printWriter != null) {
    		String s = printWriter.getBuffer().toString();
    		if(getCharacterEncoding() != null) {
	    		try {
					return s.getBytes(getCharacterEncoding());
				} catch (UnsupportedEncodingException e) {
					//punt
					return s.getBytes();
				}
    		}
    		else {
    			return s.getBytes();
    		}
    	}
    	else {
    		return null;
    	}
	}
    

    @Override
    public ServletOutputStream getOutputStream() throws IOException {        
        //return super.getOutputStream();
    	outputStream = new ServletOutputStreamWrapper(this, super.getOutputStream());
        return outputStream;
    }
    
    @Override
    public java.io.PrintWriter getWriter() throws IOException {
        printWriter = new PrintWriterWrapper(this, super.getWriter());
        return printWriter;
    }
    
    void outputStarted() {
    	if(this.responseCode == -1) {
    		setResponseCode(200);
    	}
    	pi.outputStarted();
    }

    @Override
    public void setContentType(String type) {
        super.setContentType(type);
        contentType = type;
    }
    @Override
    public String getContentType() {
        return contentType;
    }
    PrintWriterWrapper seePrintWriter() {
        return printWriter;
    }
    ServletOutputStreamWrapper seeOutputStream() {
        return outputStream;
    }
    
    int responseCode = -1;

    void setResponseCode(int responseCode) {
    	this.responseCode = responseCode;
    	pi.setResponseCode(responseCode);
    }
    
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponseWrapper#sendError(int, java.lang.String)
	 */
    @Override
	public void sendError(int arg0, String arg1) throws IOException {
		setResponseCode(arg0);
		super.sendError(arg0, arg1);
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponseWrapper#sendError(int)
	 */
    @Override
	public void sendError(int arg0) throws IOException {
		setResponseCode(arg0);
		super.sendError(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponseWrapper#sendRedirect(java.lang.String)
	 */
    @Override
	public void sendRedirect(String arg0) throws IOException {
		setResponseCode(302);
		super.sendRedirect(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponseWrapper#setStatus(int, java.lang.String)
	 */
	@SuppressWarnings("deprecation")
    @Override
	public void setStatus(int arg0, String arg1) {
		setResponseCode(arg0);
		super.setStatus(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponseWrapper#setStatus(int)
	 */
    @Override
	public void setStatus(int arg0) {
		setResponseCode(arg0);
		super.setStatus(arg0);
	}

}
