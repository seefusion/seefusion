package com.seefusion.installer;

public interface Installer {

	String getXml();

	boolean isInstalled() throws InstallationException;

	void uninstall() throws InstallationException;

	void install() throws InstallationException;

}
