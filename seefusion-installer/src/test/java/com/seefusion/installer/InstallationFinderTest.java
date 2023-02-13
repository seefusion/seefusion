package com.seefusion.installer;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Ignore;
import org.junit.Test;

public class InstallationFinderTest {

	@Test @Ignore
	public void test() throws Exception {
		InstallationFinder test = new InstallationFinder();
		LinkedList<Installation> ret = test.findInstallations();
		assertFalse(ret.isEmpty());
		for(Installation install : ret) {
			System.out.println(install.getDirectory());
			for(Instance instance : install.getInstances()) {
				System.out.println("--->"+instance.getDirectory() + " (" + (instance.isValid() ? "valid" : "INVALID") + ", " + (instance.isInstalled() ? "installed" : "not installed") + ", " + instance.getJarDirectory() + ")");
			}
		}
	}

}
