package com.seefusion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

class MockServletOutputStream extends ServletOutputStream {

	ByteArrayOutputStream out;
	
	MockServletOutputStream() {
		out = new ByteArrayOutputStream();
	}
	
	@Override
	public void write(int arg0) throws IOException {
		out.write(arg0);
	}

	public byte[] getBytes() {
		return out.toByteArray();
	}

	@Override
	public boolean isReady() {
		throw new RuntimeException("Unimplemented");
	}

	@Override
	public void setWriteListener(WriteListener arg0) {
		throw new RuntimeException("Unimplemented");
	}

}
