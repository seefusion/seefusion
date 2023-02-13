/**
 * 
 */
package com.seefusion;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.SimpleTimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daryl
 * 
 */
class HttpDoFile extends HttpRequestHandler {
	
	private static final Logger LOG = Logger.getLogger(HttpDoFile.class.getName());

	String fn = null;
	byte[] contents;
	
	static {
		SimpleTimeZone gmt = new SimpleTimeZone(0, "GMT");
		Calendar cal = Calendar.getInstance(gmt);
		SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		df.setTimeZone(gmt);
		String jarPath = SeeFusion.getJarLocation();
		if(jarPath==null) {
			setLastModifiedString(df.format(cal.getTime()));
		}
		else {
			File f = new File(jarPath);
			setLastModifiedString(df.format(f.lastModified()));
		}
	}
	
	private static String lastModifiedString;
	
	public HttpDoFile(String fn, byte[] contents) {
		this.fn = fn;
		this.contents = contents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	public String doGet(HttpTalker talker) {
		OutputStream outbuf = new BufferedOutputStream(talker.getOutputStream());
		
		if (fn == null) {
			String pageName = talker.getHttpRequest().getRequestLine().getPath();
			fn = pageName.substring(1);
		}
		
		InputStream instream = null;
		int length;
		try {
			if(contents == null) {
				instream = this.getClass().getClassLoader().getResourceAsStream(fn);
				if (instream == null) {
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
					return null;
				}
				length = instream.available();
			} else {
				length = contents.length;
				// debug("HttpDoFile: using cached " + fn + " (" + length + " bytes)");
			}
		
			// detect last-modified
			String lastmodified;
			if( (lastmodified = talker.getHttpRequest().getHeaders().getProperty("if-modified-since")) != null) {
				//debug("if-modified-since: " + lastmodified);
				if( lastmodified.startsWith(getLastModifiedString()) ) {
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
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("Last-Modified", getLastModifiedString());
			//debug("last-modified: " + getLastModifiedString());
			
			// determine content-type
			String contentType = "application/octet-stream";
			// content-type
			if(fn.endsWith("/") || fn.length() < 3) {
				contentType = "text/html";
			} else {
				String ext = fn.substring(fn.length() - 3, fn.length());
				contentType = MimeMap.getContentType(ext);
			}
			headers.put("Content-Type", contentType);
			headers.put("Content-Length", Integer.toString(length));
		
			StringBuilder sb = new StringBuilder();
			talker.appendHTTPHeaders(sb, "200 OK", headers);
			outbuf.write(sb.toString().getBytes());
			if(contents != null) {
				outbuf.write(contents);
			}
			else {
				byte[] buf = new byte[1400];
				int bytecount;
				while ((bytecount = instream.read(buf)) != -1) {
					outbuf.write(buf, 0, bytecount);
				}
			}
			outbuf.flush();
			outbuf.close();
			//debug("Returned " + fn + " " + contentType);

			talker.setAutoFlush(false);
			return null;
		}
		catch (IOException e) {
			LOG.log(Level.WARNING, "Exception Sending File", e);
			return null;
		}
	}

	public static String getLastModifiedString() {
		return lastModifiedString;
	}

	public static void setLastModifiedString(String lastModifiedString) {
		HttpDoFile.lastModifiedString = lastModifiedString;
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.NONE);
	}

}
