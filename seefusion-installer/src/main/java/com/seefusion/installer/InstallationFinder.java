package com.seefusion.installer;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class InstallationFinder {

	public Installation[] possibleInstallPaths = {
			new Cf9StandaloneInstallation("/ColdFusion9")
			,new Cf9StandaloneInstallation("/opt/coldfusion9")
			,new Cf9StandaloneInstallation("/ColdFusion8")
			,new Cf9StandaloneInstallation("/opt/coldfusion8")
			,new JrunInstallation("/jrun4")
			,new JrunInstallation("/opt/jrun4")
			,new TomcatInstallation("/tomcat7")
			,new TomcatInstallation("/tomcat8")
			,new TomcatInstallation("/opt/tomcat7")
			,new TomcatInstallation("/opt/tomcat8")
			,new Cf10Installation("/coldfusion10")
			,new Cf10Installation("/coldfusion11")
			,new Cf10Installation("/coldfusion12")
			,new Cf10Installation("/ColdFusion2016")
			,new Cf10Installation("/opt/coldfusion10")
			,new Cf10Installation("/opt/coldfusion11")
			,new Cf10Installation("/opt/coldfusion12")
			,new Cf10Installation("/opt/ColdFusion2016")
			,new Cf10Installation("/usr/local/tomcat7")
	};
	
	public LinkedList<Installation> findInstallations() throws IOException {
		LinkedList<Installation> ret = new LinkedList<Installation>();
		File[] roots = File.listRoots();
		for(File rootFile : roots) {
			if(rootFile.canRead()) {
				String root = rootFile.getCanonicalPath();
				for(Installation possibleInstallation : possibleInstallPaths) {
					if(possibleInstallation.isValid(root)) {
						ret.add(possibleInstallation.getInstallation(root, possibleInstallation.getDirectory()));
					}
				}
			}
		}
		return ret;
	}
	
}
