/*
 * DashboardFetcher.java
 *
 */

package com.seefusion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * 
 * @author Daryl
 */
class DashboardFetcher extends SeeTask {
	
	private static final Logger LOG = Logger.getLogger(DashboardFetcher.class.getName());

	ServerEntry curServer;
	PooledThread thread;
	@SuppressWarnings("PMD.AvoidStringBufferField")
	StringBuilder result = new StringBuilder();
	Boolean resultOK = null;
	String authToken; // sf.getHttpPasswordB64Token()
	String url;
	String key;
	Socket s = null;

	static HashMap<String, DashboardFetcher> activeFetchers = new HashMap<String, DashboardFetcher>();

	static DashboardFetcher getDashboardFetcher(ServerEntry curServer, String url, String authToken) {
		DashboardFetcher ret;
		String key = curServer.name + ":" + curServer.port + "/" + url;
		synchronized (activeFetchers) {
			ret = activeFetchers.get(key);
			if (ret == null) {
				ret = new DashboardFetcher(key, curServer, url, authToken);
				activeFetchers.put(key, ret);
			}
		}
		return ret;
	}

	protected DashboardFetcher(String key, ServerEntry curServer, String url, String authToken) {
		this.curServer = curServer;
		this.url = url;
		this.authToken = authToken;
		this.key = key;
		thread = ThreadPool.start(this);
	}

	@Override
	void join() {
		try {
			if (thread != null) {
				thread.join();
			}
		}
		catch (InterruptedException e) {
			Thread.interrupted();
		}
	}

	ServerEntry getServerEntry() {
		return curServer;
	}

	@Override
	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		String curLine;
		try {
			InetSocketAddress sa = new InetSocketAddress(curServer.inetAddress, curServer.port);
			s = new Socket();
			s.connect(sa, 5000);
			s.setSoTimeout(5000);
			out = new PrintWriter(s.getOutputStream());
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out.print("get " + url + " http/0.9\r\n");
			out.print(SeeFusion.AUTH_HEADER_NAME + ": sfpassword=" + authToken == null ? "" : authToken + "\r\n");
			out.print("\r\n");
			out.flush();
			resultOK = Boolean.FALSE;
			// read http response line
			curLine = in.readLine();
			if (curLine != null && curLine.toLowerCase().startsWith("http/1.0 200 ")) {
				// remove headers
				while (curLine.length() > 0) {
					curLine = in.readLine();
				}
				while (curLine != null) {
					curLine = in.readLine();
					if (curLine != null) {
						result.append(curLine).append('\n');
						resultOK = Boolean.TRUE;
					}
				}
			}
			else {
				LOG.warning("Unreadable response from " + curServer.name + ": " + result);
			}
		}
		catch (IOException e) {
			resultOK = Boolean.FALSE;
			result = new StringBuilder().append("Unable to connect: ").append(e.toString());
		}
		finally {
			if (in != null) try {
				in.close();
			}
			catch (IOException e) {
			}
			if (out != null) out.close();
			if (s != null) try {
				s.close();
			}
			catch (IOException e) {
			}
			thread = null;
		}
		synchronized (activeFetchers) {
			activeFetchers.remove(key);
		}
	}

	boolean isResultOK() {
		join();
		return resultOK.booleanValue();
	}

	String getResult() {
		join();
		return result.toString();
	}

	@Override
	public String getThreadName() {
		return "Dashboard Fetcher";
	}
	
	@Override
	void shutdown() {
		if(s != null) {
			try {
				s.close();
			} catch (IOException e) {
			}
		}
	}


}
