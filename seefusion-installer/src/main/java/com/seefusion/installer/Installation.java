package com.seefusion.installer;

import java.util.List;

public abstract class Installation {

	public abstract String getName();
	
	public abstract boolean isValid(String prefix);

	public abstract String getDirectory();
	
	public abstract List<Instance> getInstances();
	
	public abstract Installation getInstallation(String prefix, String dir);
	
}
