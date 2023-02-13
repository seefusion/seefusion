package com.seefusion.installer;

import java.io.File;
import java.io.IOException;

public class TomcatInstance extends Instance {

	private final String dir;
	private final String name;

	public TomcatInstance(String dir, String name) {
		this.dir = canon(dir);
		this.name = name;
	}

	@Override
	public boolean isValid() {
		File f = new File(dir + "/conf/server.xml");
		return f.exists();
	}

	@Override
	public int getInstalledPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	String getDirectory() {
		return dir;
	}

	@Override
	boolean isInstalled() throws InstallationException {
		String xml = readConfig();
		InstallTomcatValve installer = new InstallTomcatValve(xml);
		return installer.isInstalled();
	}

	@Override
	String getName() {
		return name;
	}

	@Override
	String getJarDirectory() {
		return canon(dir + "/lib/");
	}

	private String getConfFileName() {
		return dir + "/conf/server.xml";
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
		InstallCopyJar.install(this);
		writeConfig(installer.getXml());
	}

	@Override
	void uninstall() throws InstallationException {
		String xml = readConfig();
		InstallTomcatValve installer = new InstallTomcatValve(xml);
		installer.uninstall();
		InstallCopyJar.uninstall(this);
		writeConfig(installer.getXml());
	}
}
