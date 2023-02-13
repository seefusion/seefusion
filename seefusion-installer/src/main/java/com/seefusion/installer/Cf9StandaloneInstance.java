package com.seefusion.installer;

import java.io.File;
import java.io.IOException;

public class Cf9StandaloneInstance extends Instance {

	private String dir;

	public Cf9StandaloneInstance(String dir) {
		this.dir = canon(dir);
	}

	@Override
	public boolean isValid() {
		File f1 = new File(dir + "/lib/cfusion.jar");
		File f2 = new File(dir + "/wwwroot/WEB-INF/web.xml");
		return f1.exists() && f2.exists();
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
	boolean isInstalled() {
		try {
			String xml = Util.readFile(dir + "/wwwroot/WEB-INF/web.xml");
			return InstallWebXml.isInstalled(xml);
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	String getName() {
		return "Standalone";
	}

	@Override
	String getJarDirectory() {
		return canon(dir + "/lib/");
	}
	
	@Override
	void install() throws InstallationException {
		InstallCopyJar.install(this);

		// attempt to copy macromedia_drivers.jar and tools.jar
		File srcDir = new File(getDirectory() + File.separatorChar + "lib");
		File destDir = new File(getJarDirectory());
		Util.blindCopy("macromedia_drivers.jar", srcDir, destDir);
		Util.blindCopy("mysql-connector-java-commercial-5.0.5-bin.jar", srcDir, destDir);
	}
	
	@Override
	void uninstall() throws InstallationException {
		// Copy tools.jar, macromedia_drivers.jar:
		InstallCopyJar.uninstall(this);
		Util.blindDelete(getJarDirectory(), "macromedia_drivers.jar");
		Util.blindDelete(getJarDirectory(), "mysql-connector-java-commercial-5.0.5-bin.jar");
	}

}
