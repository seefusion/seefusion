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
class FilterJvmConfig extends javax.swing.filechooser.FileFilter implements java.io.FileFilter, FilenameFilter {

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		return pathname.isDirectory() || pathname.getName().equalsIgnoreCase("jvm.config");
	}

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		return name.equalsIgnoreCase("jvm.config");
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return "jvm.config";
	}

}
