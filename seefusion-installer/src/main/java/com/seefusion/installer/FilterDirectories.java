/**
 * 
 */
package com.seefusion.installer;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * @author Daryl
 *
 */
class FilterDirectories extends javax.swing.filechooser.FileFilter implements FileFilter, FilenameFilter {

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		return f.isDirectory();
	}

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		File f = new File(dir, name);
		return f.isDirectory();
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return "Directories";
	}

}
