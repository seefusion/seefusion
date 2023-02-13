/**
 * 
 */
package com.seefusion.installer;

import java.io.*;
import java.util.Properties;

/**
 * @author Daryl
 * 
 */
class InstallProperties {

	void setDefaultProperties(Properties p) {
		p.setProperty("name", "{hostname}");
		p.setProperty("listeners", "all:8999");
		p.setProperty("historySize", "20");
		p.setProperty("slowHistorySize", "20");
		p.setProperty("slowPageThreshold", "8000");
		p.setProperty("debugStackTargets", ".cfm: .cfc:");
	}

	String install() {
		return null;
	}

	String uninstall() {
		return null;
	}

	Properties readProps(File f) throws IOException {
		Properties ret = new Properties();
		try {
			InputStream in = new FileInputStream(f);
			ret.load(in);
			in.close();
		}
		catch (FileNotFoundException e) {
			// can't happen
		}
		return ret;
	}

	void write(File f, Properties p) throws IOException {
		//OutputStream out = new FileOutputStream(model.fileSFProps);
		//p.store(out, null);
		//out.close();
	}

}
