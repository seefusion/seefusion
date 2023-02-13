package com.seefusion;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

public class InputStreamWrapper extends ServletInputStream {

	private static final int BUFSIZE = 10000;
	private final ServletInputStream inputStream;
	byte[] buf = new byte[BUFSIZE];
	int bufindex = 0;
	byte[] bufCopy;

	// strictly speaking, this is subject to a race condition
	// where the buffer can be in the process of being expanded
	// while we're getting this copy, but in that case we'll
	// just get a slightly-old copy, not garbage, so not gonna
	// waste the cycles on synchronization.
	// edge case where the cached buffer doesn't get nulled out: meh.
	protected byte[] getBuf() {
		if(bufCopy == null) {
			bufCopy = Arrays.copyOfRange(buf, 0, bufindex);
		}
		return bufCopy;
	}
	
	public InputStreamWrapper(RequestInfo ri, ServletInputStream inputStream) {
		this.inputStream = inputStream;
		ri.setInputStreamWrapper(this);
	}

	private void updateBuf(byte[] b, int off, int len) {
		if(len > 0 && bufindex < BUFSIZE) {
			int bytecount =  Math.min(len, BUFSIZE-bufindex);
			for(int i=0; i < bytecount; i++) {
				buf[bufindex+i] = b[off+i];
			}
			bufindex += bytecount;
			bufCopy = null;
		}
	}
	
	@Override
	public int readLine(byte[] b, int off, int len) throws IOException {
		int ret = super.readLine(b, off, len);
		updateBuf(b, off, ret);
		return ret;
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int ret = super.read(b, off, len);
		updateBuf(b, off, ret);
		return ret;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int ret = super.read(b);
		updateBuf(b, 0, ret);
		return ret;
	}

	@Override
	public int read() throws IOException {
		int ret = inputStream.read();
		if(bufindex < BUFSIZE) {
			buf[bufindex++] = (byte)ret;
			bufCopy= null;
		}
		return ret;
	}

	@Override
	public boolean isFinished() {
		return inputStream.isFinished();
	}

	@Override
	public boolean isReady() {
		return inputStream.isReady();
	}

	@Override
	public void setReadListener(ReadListener arg0) {
		inputStream.setReadListener(arg0);
	}

}
