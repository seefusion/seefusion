/**
 * 
 */
package com.seefusion.installer;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Daryl
 *
 */
class FilterWebXml extends javax.swing.filechooser.FileFilter implements java.io.FileFilter, FilenameFilter {

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		return pathname.isDirectory() || pathname.getName().equalsIgnoreCase("web.xml");
	}

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		return name.equalsIgnoreCase("web.xml");
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return "web.xml";
	}

}
