/*
 * SeeFusionMain.java
 *
 * Main entry point when called as an executable .jar file
 */

package com.seefusion;

import java.io.File;
import java.util.Map;

/**
 * 
 * @author Daryl
 */
public class SeeFusionMain {

	static final boolean DEBUG_ENABLED = true;
	
	public static final String COPYRIGHT = "Copyright (c)2005-"
			+ java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
			+ " Daryl Banttari";

	/** Creates a new instance of SeeFusionMain */
	public SeeFusionMain() {
	}

	public static void main(String[] args) {
		if (args.length > 0 && args[0].equals("-version")) {
			System.out.println(getVersion());
		}
		else if (args.length > 0 && args[0].equals("-properties")) {
			for(Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}
		}
		else if (args.length > 0 && args[0].equals("-hmm")) {
			String classpath = System.getProperty("java.class.path");
			String[] paths = classpath.split("[" + System.getProperty("path.separator") + "]");
			for(int i=0; i < paths.length; i++) {
				if(paths[i].endsWith("SeeFusion.jar")) {
					File f = new File(paths[i]);
					System.out.println(f.getAbsolutePath());
					break;
				}
			}
		}
		else if (args.length > 0 && args[0].equals("-server")) {
			// start standalone HttpListener/HttpTalker
		}
		else {
			com.seefusion.installer.Main.main(args);
		}
	}

	public static String getVersion() {
		String ret = SeeFusionMain.class.getPackage().getImplementationVersion();
		if (ret == null) ret = "5.0.test";
		return ret; 
	}
	
	public static String getFullVersionString() {
		return "SeeFusion " + getVersion() + " " + COPYRIGHT;
	}

}
