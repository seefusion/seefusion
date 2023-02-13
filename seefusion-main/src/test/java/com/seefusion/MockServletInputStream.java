package com.seefusion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

class MockServletInputStream extends ServletInputStream {

	InputStream in;

	MockServletInputStream(String s) {
		this.in = new ByteArrayInputStream(s.getBytes());
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public boolean isFinished() {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public boolean isReady() {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void setReadListener(ReadListener arg0) {
		throw new RuntimeException("Unimplemented");
	}

}
