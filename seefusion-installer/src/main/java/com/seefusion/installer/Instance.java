package com.seefusion.installer;

import java.io.File;
import java.io.IOException;

public abstract class Instance {
	
	abstract boolean isValid();
	
	/**
	 * tests for SeeFusion installation status for this instance
	 * @return "listen , or 
	 */
	abstract int getInstalledPort();

	abstract String getDirectory();
	
	abstract String getName();
	
	abstract boolean isInstalled() throws InstallationException;

	abstract String getJarDirectory();
	
	String canon(String path) {
		try {
			return new File(path).getCanonicalPath();
		} catch (IOException e) {
			return path;
		}
	}

	abstract void install() throws InstallationException;

	abstract void uninstall() throws InstallationException;

}
