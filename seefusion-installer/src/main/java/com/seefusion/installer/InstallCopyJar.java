/**
 * 
 */
package com.seefusion.installer;

import java.io.*;

/**
 * @author Daryl
 *
 */
class InstallCopyJar {

	static void uninstall(Instance instance) throws InstallationException {
		File jarFile = new File(instance.getJarDirectory() + "/SeeFusion5.jar");
		if(jarFile.exists()) {
			if( jarFile.delete() ) {
				return;
			} else {
				throw new InstallationException("Unable to delete SeeFusion5.jar at " + jarFile.getAbsolutePath());
			}
		} else {
			throw new InstallationException("Unable to locate SeeFusion5.jar at " + jarFile.getAbsolutePath());
		}
	}
	
	static void install(Instance instance) throws InstallationException {
		File src = Util.getThisJarFile();
		if(src == null) {
			throw new InstallationException("Unable to find this running SeeFusion5.jar; it must be manually copied to " + instance.getJarDirectory());
		}
		File jarFile = new File(instance.getJarDirectory() + "/SeeFusion5.jar");
		if(src.equals(jarFile)) {
			// running in place, no need to copy
			return;
		}
		try {
			Util.copyFile(src, jarFile);
		}
		catch (InstallationException e) {
			throw new InstallationException("Unable to copy SeeFusion5.jar; it must be manually copied to " + instance.getJarDirectory() + " (" + e.toString() + ")"); 
		}		
	}
	
}
