/**
 * 
 */
package com.seefusion;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.SimpleTimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daryl
 * 
 * This is used for development/testing.  It serves UI content from a filesystem webroot instead of from the jar's /ui/ directory
 * eg <devwebroot>C:\seefusion\seefusion\seefusion-ui\dist</devwebroot>
 */
class HttpDoFileServer extends HttpRequestHandler {
	
	private static Logger LOG = Logger.getLogger(HttpDoFileServer.class.getName());

	private String webroot;
	
	public HttpDoFileServer(File webroot) throws IOException {
		this.webroot = webroot.getCanonicalPath();
		String sep = System.getProperty("file.separator");
		if(!this.webroot.endsWith(sep)) {
			this.webroot += sep;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	@Override
	public String doGet(HttpTalker talker) {
		OutputStream outbuf = new BufferedOutputStream(talker.getOutputStream());

		HttpRequest req = talker.getHttpRequest();
		RequestLine reqLine = req.getRequestLine();
		String path = reqLine.getPath();
		String pageName = path.substring(1);
		if(pageName.isEmpty()) {
			pageName = "index.html";
		}
		//debug("pagename:" + pageName);
		String fn = webroot + pageName;
		File f = new File(fn);

		if (!containsPage(f) ) {
			String s = "HTTP/1.0 404 Not Found: " + Util.xmlStringFormat(fn) + "\r\n\r\n";
			try {
				outbuf.write(s.getBytes());
				outbuf.flush();
				outbuf.close();
			}
			catch (IOException e) {
				// ignore
			}
			LOG.info("404 " + Util.xmlStringFormat(fn));
		}
		else {
			// detect last-modified
			String lastModified = formatTimeString(f);
			String lastModifiedHeader;
			if( (lastModifiedHeader = talker.getHttpRequest().getHeaders().getProperty("if-modified-since")) != null) {
				//debug("if-modified-since: " + lastmodified);
				if( lastModifiedHeader.startsWith(lastModified) ) {
					try {
						//debug("sending 304 not-modified for " + fn);
						outbuf.write("HTTP/1.0 304 Not Modified\r\n\r\n".getBytes());
						outbuf.flush();
						outbuf.close();
					}
					catch (IOException e) {
						// ignore
					}
					return null;
				}
			}
			int bytecount;
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Last-Modified", lastModified);
			//debug("last-modified: " + lastModified);
			
			// determine content-type
			String contentType = "application/octet-stream";
			// content-type
			if(fn.endsWith("/") || fn.length() < 3) {
				contentType = "text/html";
			}
			else {
				String ext = fn.substring(fn.length() - 3, fn.length());
				contentType = MimeMap.getContentType(ext);
			}
			try {
				headers.put("Content-Type", contentType);
				headers.put("Content-Length", Long.toString(f.length()));
			
				StringBuilder sb = new StringBuilder();
				talker.appendHTTPHeaders(sb, "200 OK", headers);
				outbuf.write(sb.toString().getBytes());
				InputStream in = new FileInputStream(f);
				byte[] buf = new byte[1400];
				while ((bytecount = in.read(buf)) != -1) {
					outbuf.write(buf, 0, bytecount);
				}
				outbuf.flush();
				outbuf.close();
				in.close();
				//debug("Returned " + fn + " " + contentType);
			}
			catch (IOException e) {
				LOG.log(Level.SEVERE, "Exception Sending File", e);
			}
		}
		talker.setAutoFlush(false);
		return null;
	}

	String formatTimeString(File f) {
		SimpleTimeZone gmt = new SimpleTimeZone(0, "GMT");
		SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		df.setTimeZone(gmt);
		return df.format(f.lastModified());
	}
	
	@Override
	Perm getPerm() {
		return new Perm(Perm.NONE);
	}

	boolean containsPage(String fn) {
		if(fn==null || fn.isEmpty() || fn.equals("/")) {
			fn = "index.html";
		}
		return containsPage(new File(webroot + fn));
	}
	
	boolean containsPage(File f) {
		boolean ret = false;
		try {
			ret = f.exists() && f.canRead() && f.getCanonicalPath().startsWith(webroot);
			if(!ret) {
				// log("rejecting " + f.getAbsolutePath() + "! exists:" + f.exists() + "; canread:" + f.canRead() + "; canon path:" + f.getCanonicalPath() + "; webroot:" + webroot);
				new Exception("Cannot read " + f.getCanonicalPath()).printStackTrace();
			}
		}
		catch(IOException e) {
			LOG.log(Level.WARNING, "Error getting file canonical path", e);
		}
		return ret;
	}

}
