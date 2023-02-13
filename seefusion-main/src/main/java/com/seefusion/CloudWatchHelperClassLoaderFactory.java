package com.seefusion;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

public class CloudWatchHelperClassLoaderFactory {

	private static Logger LOG = Logger.getLogger(CloudWatchHelperClassLoaderFactory.class.getName());
	
	@SuppressWarnings("deprecation")
	public ClassLoader getClassLoader() throws IOException {
		File sdkLoc = getSdkLocation();
		// return new ZipContainingJarsClassLoader(CloudWatchHelperClassLoaderFactory.class.getClassLoader(), sdkLoc);
		URL[] urls = new URL[1];
		urls[0] = sdkLoc.toURL();
		LOG.info("SeeFusion AWS SDK found at " + urls[0].toString());
		return new URLClassLoader(urls, getClass().getClassLoader());
	}

	
	private static File getSdkLocation() {
		File seeFusionDir = SeeFusion.getSeeFusionDirectory();
		File sdkLoc = findSdkIn(seeFusionDir);
		if(sdkLoc == null) {
			File f = new File("./src/test/resources");
			if(f != null && f.exists()) {
				sdkLoc = findSdkIn(f);
			}
		}
		if(sdkLoc == null) {
			throw new IllegalStateException("Unable to find SeeFusion's AWS SDK zip (normally seefusion-aws-java-sdk.zip) in SeeFusion's directory (" + seeFusionDir.getPath() + ")");
		}
		return sdkLoc;
	}
	
	static File findSdkIn(File dir) {
		for (File f : dir.listFiles()) {
			if(f.isFile()) {
				if(f.getName().startsWith("seefusion-aws") && f.getName().endsWith(".zip")) {
					return f;
				}
			}
		}
		return null;
	}
}
