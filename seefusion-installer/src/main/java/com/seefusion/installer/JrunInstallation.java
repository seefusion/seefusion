package com.seefusion.installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import com.seefusion.SimpleXml;

public class JrunInstallation extends Installation {

	String dir;
	
	public JrunInstallation(String dir) {
		this.dir = dir;
	}

	@Override
	public boolean isValid(String prefix) {
		File f = new File(prefix + dir + "/lib/jrun.jar");
		return f.exists();
	}

	@Override
	public JrunInstallation clone() {
		return new JrunInstallation(dir);
	}

	@Override
	public String getDirectory() {
		return dir;
	}
	
	@Override
	public List<Instance> getInstances() {
		LinkedList<Instance> ret = new LinkedList<Instance>();
		File f = new File(dir + "/lib/servers.xml");
		String xmlString;
		try (Scanner s = new Scanner(f)) {
			xmlString = s.useDelimiter("\\Z").next();
		} catch (FileNotFoundException e) {
			return ret;
		}
		SimpleXml xml = SimpleXml.load(xmlString);
		for(SimpleXml server : xml) {
			String name = server.get("name").getSimpleValue();
			if(name.equalsIgnoreCase("samples") || name.equalsIgnoreCase("admin")) {
				// ignore
			}
			else {
				String instanceDir = server.get("directory").getSimpleValue();
				instanceDir = instanceDir.replace("{jrun.home}", this.dir);
				//System.out.println("instance " + name + ": " + instanceDir);
				ret.add(new JrunInstance(instanceDir, name));
			}
		}
		return ret;
	}

	@Override
	public Installation getInstallation(String prefix, String dir) {
		return new JrunInstallation(prefix + dir);
	}

	@Override
	public String getName() {
		return "ColdFusion 9 (JRun)";
	}
	
}
