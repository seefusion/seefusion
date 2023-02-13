package com.seefusion.installer;

import java.io.File;
import java.io.IOException;

public class JrunInstance extends Instance {

	private final String dir;
	private final String name;

	public JrunInstance(String dir, String name) {
		this.dir = canon(dir);
		this.name = name;
	}

	File getWebXmlFile() {
		return new File(this.dir + "/cfusion-ear/cfusion-war/WEB-INF/web.xml");
	}
	
	String getWebXmlContents() {
		try {
			return Util.readFile(getWebXmlFile());
		} catch (IOException e) {
			return null;
		}
	}
	
	@Override
	boolean isValid() {
		return getWebXmlFile().exists();
	}

	@Override
	int getInstalledPort() {
		// TODO: something
		return 0;
	}

	@Override
	String getDirectory() {
		return dir;
	}
	
	@Override
	boolean isInstalled() {
		String xml = getWebXmlContents();
		if(xml==null) {
			return false;
		}
		return InstallWebXml.isInstalled(xml);
	}

	@Override
	String getName() {
		return name;
	}

	@Override
	String getJarDirectory() {
		return canon(dir + "/cfusion-ear/cfusion-war/WEB-INF/lib/");
	}

	@Override
	void install() throws InstallationException {
		// copy SeeFusion.jar
		File srcFile = Util.getThisJarFile();
		File destFile = new File(getJarDirectory() + "/SeeFusion.jar");
		Util.copyFile(srcFile, destFile);
		
		// attempt to copy macromedia_drivers.jar and mysql drivers jar
		File srcDir = new File(getDirectory() + "/cfusion-ear/cfusion-war/WEB-INF/cfusion/lib");
		File destDir = new File(getJarDirectory());
		
		Util.blindCopy("macromedia_drivers.jar", srcDir, destDir);
		Util.blindCopy("mysql-connector-java-commercial-5.0.5-bin.jar", srcDir, destDir);

		// create SeeFusion.xml
		
		
		// update web.xml with SeeFusion
		String xml = getWebXmlContents();
		if(xml==null) {
			throw new InstallationException("Unable to read web.xml");
		}
		xml = InstallWebXml.install(xml);
		try {
			Util.writeFile(getWebXmlFile(), xml);
		} catch (IOException e) {
			throw new InstallationException(e.getMessage());
		}

	}

	@Override
	void uninstall() {
		Util.blindDelete(getJarDirectory(), "macromedia_drivers.jar");
		Util.blindDelete(getJarDirectory(), "mysql-connector-java-commercial-5.0.5-bin.jar");		
	}

	
}
