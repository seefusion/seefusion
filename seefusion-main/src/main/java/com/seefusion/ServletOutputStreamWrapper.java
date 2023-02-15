/*
 * ServletOutputStream.java
 *
 */

package com.seefusion;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.WriteListener;

class ServletOutputStreamWrapper extends javax.servlet.ServletOutputStream {
    
	HttpServletResponseWrapper notifyTarget;
    javax.servlet.ServletOutputStream sos;
    boolean isOutputStarted = false;
    ByteArrayOutputStream captureBuf;
	private PrintWriter captureWriter;
    
    /** Creates a new instance of ServletOutputStream */
    public ServletOutputStreamWrapper(HttpServletResponseWrapper notifyTarget, javax.servlet.ServletOutputStream sos) {
        this.sos = sos;
        this.notifyTarget = notifyTarget;
        this.captureBuf = new ByteArrayOutputStream();
        this.captureWriter = new PrintWriter(captureBuf);
    }
    
	public byte[] getBytes() {
		return captureBuf.toByteArray();
	}
    
    void notifyOutput() {
        this.isOutputStarted=true;
        notifyTarget.outputStarted();
    }
    
    @Override
    public void println(float param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        captureWriter.println(param);
        sos.println(param);
    }
    
    @Override
    public void write(byte[] b) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.write(b);
    }
    
    @Override
    public void print(long param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.print(param);
    }
    
    @Override
    public void println(double param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.println(param);
    }
    
    @Override
    public void print(int param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.print(param);
    }
    
    @Override
    public void println(int param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.println(param);
    }
    
    @Override
    public void println(char param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.println(param);
    }
    
    @Override
    public void print(boolean param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.print(param);
    }
    
    @Override
    public void println(boolean param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.println(param);
    }
    
    @Override
    public void print(char param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.print(param);
    }
    
    @Override
    public void println(String str) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.println(str);
    }
    
    @Override
    public void write(byte[] b, int off, int len) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.write(b, off, len);
    }
    
    @Override
    public void println() throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.println();
    }
    
    @Override
    public void print(float param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.print(param);
    }
    
    @Override
    public void print(double param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.print(param);
    }
    
    @Override
    public void println(long param) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.println(param);
    }
    
    @Override
    public void print(String str) throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.print(str);
    }
    
    @Override
    public void close() throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        flush();
    }
    
    public void seeClose() {
        try {
            sos.close();
        } catch(java.io.IOException e) {
            //ignore
        }
    }
    
    @Override
    public void write(int b) throws java.io.IOException {
    	captureBuf.write(b);
        sos.write(b);
    }
    
    @Override
    public void flush() throws java.io.IOException {
        if(!isOutputStarted) notifyOutput();
        sos.flush();
    }

	@Override
	public boolean isReady() {
		return sos.isReady();
	}

	@Override
	public void setWriteListener(WriteListener arg0) {
		sos.setWriteListener(arg0);
	}

}
