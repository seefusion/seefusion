package com.seefusion.installer;

public class InstallationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InstallationException(String string) {
		super(string);
	}

	public InstallationException(Exception e) {
		super(e);
	}

}
