/**
 * 
 */
package com.seefusion;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daryl
 *
 */
class SmtpSender extends SeeTask {

	private static final Logger LOG = Logger.getLogger(SmtpSender.class.getName());

	private static final String CRLF = "\r\n";

	LinkedList<SmtpMessage> sendQueue = new LinkedList<SmtpMessage>();

	PooledThread thread;

	boolean running = true;

	private final String smtpHost;

	private final String smtpUsername;

	private final String smtpPassword;

	private int smtpPort;

	SmtpSender(String smtpServer, String smtpUsername, String smtpPassword) {
		if(smtpServer.indexOf(':') == -1) {
			this.smtpHost = smtpServer;
			this.smtpPort = 25;
		}
		else {
			String[] split = smtpServer.split(":");
			this.smtpHost = split[0];
			try {
				this.smtpPort = Integer.parseInt(split[1]);
			}
			catch (NumberFormatException e) {
				LOG.warning("Unable to parse port number from \'" + smtpServer + "\'; using port 25.");
				this.smtpPort = 25;
			}
		}
		this.smtpUsername = smtpUsername;
		this.smtpPassword = smtpPassword;
		this.thread = ThreadPool.start(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (running) {
			while (!sendQueue.isEmpty()) {
				send(sendQueue.removeFirst());
			}
			synchronized (this) {
				try {
					this.wait();
				}
				catch (InterruptedException e) {
					// ignore
				}
			}
		}

	}

	void send(SmtpMessage message) {
		Socket s = null;
		try {
			InetSocketAddress inetAddr = new InetSocketAddress(smtpHost, smtpPort);
			s = new Socket();
			s.setSoTimeout(30000);
			s.connect(inetAddr);
			send(s, message);
		}
		catch (UnknownHostException e) {
			LOG.warning("Mail Send Failure: Unable to resolve host: " + smtpHost);
		}
		catch (IOException e) {
			LOG.log(Level.WARNING, "Mail Send Failure", e);
		}
		finally {
			try {
				if(s != null && s.isConnected()) {
					s.close();
				}
			}
			catch (IOException e) {
				// ignore
			}
		}
	}

	void send(Socket s, SmtpMessage message) throws IOException {
		InputStream rawIn = s.getInputStream();
		OutputStream rawOut = s.getOutputStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(rawIn));
		PrintWriter out = new PrintWriter(new BufferedOutputStream(rawOut));
		send(in, out, message);
	}

	void send(BufferedReader in, PrintWriter out, SmtpMessage message) throws IOException {
		String response;

		// mail server greeting
		response = in.readLine();
		while (response.charAt(3) == '-') {
			LOG.fine("smtp ignoring: " + response);
			response = in.readLine();
		}
		LOG.fine("smtp response: " + response);
		if(!response.startsWith("220 ")) {
			throw new IOException("Unknown response from mail server (expecting 220): " + response);
		}

		// HELO
		out.print("EHLO SeeFusion\r\n");
		out.flush();
		response = in.readLine();
		while (response.charAt(3) == '-') {
			LOG.fine("smtp ignoring: " + response);
			response = in.readLine();
		}
		if(!response.startsWith("250 ")) {
			throw new IOException("Unknown response from mail server (expecting 250): " + response);
		}

		if(smtpUsername != null && !smtpUsername.isEmpty()) {
			// SMTP Auth
			out.print("AUTH LOGIN" + CRLF);
			out.flush();
			response = in.readLine();
			while (response.charAt(3) == '-') {
				LOG.fine("smtp ignoring: " + response);
				response = in.readLine();
			}
			if(!response.startsWith("334 ")) {
				throw new IOException("Unknown response from mail server (expecting 334): " + response);
			}

			String encodedUsername = Base64.encodeBytes(smtpUsername.getBytes());
			out.print(encodedUsername + CRLF);
			out.flush();
			response = in.readLine();
			while (response.charAt(3) == '-') {
				LOG.fine("smtp ignoring: " + response);
				response = in.readLine();
			}
			if(!response.startsWith("334 ")) {
				throw new IOException("Unknown response from mail server (expecting 334): " + response);
			}

			String encodedPassword = Base64.encodeBytes(smtpPassword.getBytes());
			out.print(encodedPassword + CRLF);
			out.flush();
			response = in.readLine();
			while (response.charAt(3) == '-') {
				LOG.fine("smtp ignoring: " + response);
				response = in.readLine();
			}
			if(!response.startsWith("235 ")) {
				throw new IOException("Unknown response from mail server (expecting 235): " + response);
			}
		}

		// MAIL FROM
		out.print("MAIL FROM: " + message.smtpFrom + CRLF);
		out.flush();
		response = in.readLine();
		while (response.charAt(3) == '-') {
			LOG.fine("smtp ignoring: " + response);
			response = in.readLine();
		}
		if(!response.startsWith("250 ")) {
			throw new IOException("Unknown response from mail server (expecting 250): " + response);
		}

		// RCPT TO
		boolean isAnyRcptToOK = false;
		String[] aRcptTo = message.getSmtpToArray();
		for (int i = 0; i < aRcptTo.length; i++) {
			out.print("RCPT TO: " + aRcptTo[i] + CRLF);
			out.flush();
			response = in.readLine();
			while (response.charAt(3) == '-') {
				LOG.fine("smtp ignoring: " + response);
				response = in.readLine();
			}
			if(!response.startsWith("250 ")) {
				LOG.warning("Mail server rejected recipient " + aRcptTo[i] + ": " + response);
			}
			else {
				isAnyRcptToOK = true;
			}
		}
		if(!isAnyRcptToOK) {
			throw new IOException("No 'To' addresses were accepted by the mail server, unable to send notification");
		}

		// DATA
		out.print("DATA\r\n");
		out.flush();
		response = in.readLine();
		while (response.charAt(3) == '-') {
			LOG.fine("smtp ignoring: " + response);
			response = in.readLine();
		}
		if(!response.startsWith("354 ")) {
			throw new IOException("Unknown response from mail server (expecting 354): " + response);
		}

		// send message
		out.print("From: " + message.smtpFrom + CRLF);
		out.print("To: " + message.smtpTo + CRLF);
		out.print("Subject: " + message.smtpSubject + CRLF);
		out.print("Content-type: ");
		out.print(message.contentType);
		out.print("\r\n\r\n");
		BufferedReader msgIn = new BufferedReader(new StringReader(message.smtpBody));
		String curLine;
		while ((curLine = msgIn.readLine()) != null) {
			if(curLine.equals(".")) {
				out.print("..\r\n");
			}
			else {
				out.print(curLine + CRLF);
			}
		}
		out.print(".\r\n");
		out.flush();

		// read response
		response = in.readLine();
		while (response.charAt(3) == '-') {
			LOG.fine("smtp ignoring: " + response);
			response = in.readLine();
		}
		if(!response.startsWith("250 ")) {
			LOG.warning("Unknown response from mail server after DATA sent (expecting 250): " + response);
		}

		out.print("QUIT\r\n");
		out.flush();
		// don't bother waiting for the response.

	}

	@Override
	void shutdown() {
		running = false;
		synchronized (this) {
			this.notify();
		}
	}

	@Override
	public String getThreadName() {
		return "Notifier";
	}

}
