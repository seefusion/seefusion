package com.seefusion;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SeeFusionUrlConnection extends URLConnection {

	byte[] buf;

	public SeeFusionUrlConnection(URL u) {
		super(u);
		String fn = u.getFile();
		buf = ZipContainingJarsClassLoader.entries.get(fn);
	}

	@Override
	public void connect() throws IOException {
		String fn = getURL().getFile();
		System.out.println("Reading:" + fn);
		if(!ZipContainingJarsClassLoader.entries.containsKey(fn)) {
			throw new IOException("Not found: " + fn);
		}
		buf = ZipContainingJarsClassLoader.getFile(fn);
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(buf);
	}
	
	@Override
	public int getContentLength() {
		return buf.length;
	}
	
	

}
