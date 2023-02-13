package com.seefusion;

import java.util.HashMap;

public class MimeMap {

	static HashMap<String,String> map;
	
	static {
		map = new HashMap<String,String>();
		map.put("swf", "application/x-shockwave-flash");
		map.put("htm", "text/html");
		map.put("html", "text/html");
		map.put("tml", "text/html");
		map.put(".js", "text/javascript");
		map.put("jar", "application/x-java-archive");
		map.put("gif", "image/gif");
		map.put("jpg", "image/jpeg");
		map.put("peg", "image/jpeg");
		map.put("png", "image/png");
		map.put("css", "text/css");
	}
	
	
	public static String getContentType(String ext) {
		String ret = map.get(ext.toLowerCase());
		if(ret==null) {
			ret = "application/" + ext;
		}
		return ret;
	}

}
