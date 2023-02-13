package com.seefusion.installer;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Cf9StandaloneInstallation extends Installation {

	String dir;
	
	public Cf9StandaloneInstallation(String dir) {
		this.dir = dir;
	}

	@Override
	public boolean isValid(String prefix) {
		File f = new File(prefix + dir + "/lib/cfusion.jar");
		return f.exists();
	}

	@Override
	public Cf9StandaloneInstallation clone() {
		return new Cf9StandaloneInstallation(dir);
	}

	@Override
	public String getDirectory() {
		return dir;
	}
	
	@Override
	public List<Instance> getInstances() {
		LinkedList<Instance> ret = new LinkedList<Instance>();
		ret.add(new Cf9StandaloneInstance(dir));
		return ret;
	}

	@Override
	public Installation getInstallation(String prefix, String dir) {
		return new Cf9StandaloneInstallation(prefix + dir);
	}

	@Override
	public String getName() {
		return "ColdFusion 9 Standalone";
	}
	
}
