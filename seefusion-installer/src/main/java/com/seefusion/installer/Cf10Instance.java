package com.seefusion.installer;

import java.io.File;
import java.io.IOException;

public class Cf10Instance extends Instance {

	private String dir;
	private final String name;

	public Cf10Instance(String instanceDir, String name) {
		this.dir = canon(instanceDir);
		this.name = name;
	}

	@Override
	boolean isValid() {
		File f = new File(dir + "/runtime/conf/server.xml");
		return f.exists();
	}

	@Override
	int getInstalledPort() {
		return 0;
	}

	@Override
	String getDirectory() {
		return dir;
	}

	@Override
	boolean isInstalled() throws InstallationException {
		String xml;
		try {
			xml = Util.readFile(dir + "/runtime/conf/server.xml");
		} catch (IOException e) {
			return false;
		}
		InstallTomcatValve installer = new InstallTomcatValve(xml);
		return installer.isInstalled();
	}

	@Override
	String getName() {
		return name;
	}

	@Override
	String getJarDirectory() {
		return canon(dir + "/runtime/lib");
	}
	
	private String getConfFileName() {
		return dir + "/runtime/conf/server.xml";
	}
	
	private String readConfig() throws InstallationException {
		try {
			return Util.readFile(getConfFileName());
		} catch (IOException e) {
			throw new InstallationException(e);
		}		
	}
	
	private void writeConfig(String config) throws InstallationException {
		try {
			Util.writeFile(getConfFileName(), config);
		} catch (IOException e) {
			throw new InstallationException(e);
		}
	}

	@Override
	void install() throws InstallationException {
		String xml = readConfig();
		InstallTomcatValve installer = new InstallTomcatValve(xml);
		installer.install();
		writeConfig(installer.getXml());
		File srcDir = new File(getDirectory() + File.separatorChar + "lib");
		File destDir = new File(getJarDirectory());
		InstallCopyJar.install(this);
		Util.blindCopy("macromedia_drivers.jar", srcDir, destDir);
	}

	@Override
	void uninstall() throws InstallationException {
		String xml = readConfig();
		InstallTomcatValve installer = new InstallTomcatValve(xml);
		installer.uninstall();
		writeConfig(installer.getXml());
		InstallCopyJar.uninstall(this);
		Util.blindDelete(getJarDirectory(), "macromedia_drivers.jar");
	}

}
