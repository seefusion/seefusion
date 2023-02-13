package com.seefusion.installer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class TomcatInstallation extends Installation {

	String dir;
	
	public TomcatInstallation(String dir) {
		this.dir = dir;
	}

	@Override
	public boolean isValid(String prefix) {
		File f = new File(prefix + dir + "/conf/server.xml");
		return f.exists();
	}
	
	@Override
	public TomcatInstallation clone() {
		return new TomcatInstallation(dir);
	}
	
	@Override
	public String getDirectory() {
		return dir;
	}
	
	@Override
	public List<Instance> getInstances() {
		List<Instance> ret = new LinkedList<Instance>();
		ret.add(new TomcatInstance(dir, "Tomcat"));
		return ret;
	}

	@Override
	public Installation getInstallation(String prefix, String dir) {
		return new TomcatInstallation(prefix+dir);
	}

	@Override
	public String getName() {
		return "Apache Tomcat";
	}
}
