package com.seefusion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MockSocket extends Socket {
	
	InputStream in = new ByteArrayInputStream(new byte[1]);
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	boolean isClosed = false;
	
	public InputStream getInputStream() throws IOException {
		return in;
	}
	
	public OutputStream getOutputStream() throws IOException {
		return out;
	}

	public InetAddress getInetAddress() {
		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getPort() {
		return 1234;
	}
	
	/* (non-Javadoc)
	 * @see java.net.Socket#isClosed()
	 */
	public boolean isClosed() {
		return isClosed;
	}
	
	/* (non-Javadoc)
	 * @see java.net.Socket#close()
	 */
	public synchronized void close() throws IOException {
		isClosed=true;
	}

	/* (non-Javadoc)
	 * @see java.net.Socket#isConnected()
	 */
	public boolean isConnected() {
		return !isClosed;
	}
	
}
