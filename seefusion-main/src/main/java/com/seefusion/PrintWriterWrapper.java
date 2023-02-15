/*
 * WriterWrapper.java
 *
 */

package com.seefusion;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author  Daryl
 */
class PrintWriterWrapper extends PrintWriter {
	
	PrintWriter pw;
	HttpServletResponseWrapper notifyTarget;
	boolean isOutputStarted = false;
	StringWriter sw = new StringWriter();
	PrintWriter bufWriter = new PrintWriter(sw);
	static final int MAX_BUFFER_LENGTH = 20000;
	boolean _isBufferFull = false;
	
	/** Creates a new instance of WriterWrapper */
	PrintWriterWrapper(HttpServletResponseWrapper notifyTarget, PrintWriter pw) {
		super(new NotQuiteWriter());
		this.pw = pw;
		this.notifyTarget = notifyTarget;
	}
	void notifyOutput() {
		this.isOutputStarted=true;
		notifyTarget.outputStarted();
	}
	
	private boolean isBufferFull() {
		if(!_isBufferFull) {
			_isBufferFull = sw.getBuffer().length() >= MAX_BUFFER_LENGTH;
		}
		return _isBufferFull;
	}
	
	StringBuffer getBuffer() {
		return sw.getBuffer();
	}

	@Override
	public void println(float x) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.println(x);
		pw.println(x);
	}
	
	@Override
	public void print(char[] s) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.print(s);
		pw.print(s);
	}
	
	@Override
	public void println(Object x) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.println(x);
		pw.println(x);
	}
	
	@Override
	public void close() {
		if(!isOutputStarted) notifyOutput();
		flush();
	}

	void seeClose() {
		pw.close();
	}
	
	@Override
	public void print(int i) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.print(i);
		pw.print(i);
	}
	
	@Override
	public void println(int x) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.println(x);
		pw.println(x);
	}
	
	@Override
	public void println(char x) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.println(x);
		pw.println(x);
	}
	
	@Override
	public void print(boolean b) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.print(b);
		pw.print(b);
	}
	
	@Override
	public void println(boolean x) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.println(x);
		pw.println(x);
	}
	
	@Override
	public void print(char c) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.print(c);
		pw.print(c);
	}
	
	@Override
	public void write(char[] cbuf) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.print(cbuf);
		pw.write(cbuf);
	}
	
	@Override
	public void write(String str) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.write(str);
		pw.write(str);
	}
	
	@Override
	public void println() {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.println();
		pw.println();
	}
	
	@Override
	public void print(float f) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.print(f);
		pw.print(f);
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.write(cbuf, off, len);
		pw.write(cbuf, off, len);
	}
	
	@Override
	public void println(long x) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.println(x);
		pw.println(x);
	}
	
	@Override
	public boolean checkError() {
		return pw.checkError();
	}
	
	@Override
	public void print(Object obj) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.print(obj);
		pw.print(obj);
	}
	
	@Override
	public void print(String s) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.print(s);
		pw.print(s);
	}
	
	@Override
	public void print(long l) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.print(l);
		pw.print(l);
	}
	
	@Override
	public void println(double x) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.println(x);
		pw.println(x);
	}
	
	@Override
	public void write(int c) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.write(c);
		pw.write(c);
	}
	
	@Override
	public void flush() {
		if(!isOutputStarted) notifyOutput();
		pw.flush();
	}
	
	@Override
	public void println(String x) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.println(x);
		pw.println(x);
	}
	
	@Override
	public void print(double d) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.print(d);
		pw.print(d);
	}
	
	@Override
	public void println(char[] x) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.println(x);
		pw.println(x);
	}
	
	@Override
	public void write(String str, int off, int len) {
		if(!isOutputStarted) notifyOutput();
		if(!isBufferFull()) bufWriter.write(str, off, len);
		pw.write(str, off, len);
	}
		
}
