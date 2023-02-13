/**
 * 
 */
package com.seestack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.seefusion.SimpleXml;
import com.seefusion.SimpleXmlParser;
import com.seefusion.Util;

/**
 * @author Daryl
 *
 */
public class SeeStackInfo {

	private ThreadInfos threadInfos = new ThreadInfos();
	private PackageInfos packageInfos = new PackageInfos();
	private MethodInfos methodInfos = new MethodInfos();
	private final SimpleXml xml;
	private final String[] localPackages;
	
	public SeeStackInfo(String configDir, String[] localPackages) throws IOException {
		// find XML definitions
		String defs = null;
		try {
			defs = Util.readFile(configDir + "/SeeStackInfo.xml");
		}
		catch (IOException e) {
			//System.out.println("Could not find " + configDir + "/SeeStackInfo.xml, trying classloader");
			InputStream ins = getClass().getClassLoader().getResourceAsStream("SeeStackInfo.xml");
			defs = Util.readAll(new InputStreamReader(ins));
		}
		//System.out.println(defs);
		this.xml = SimpleXmlParser.parseXml(defs);
		this.localPackages = localPackages;
		construct();
	}


	
	/**
	 * @param localPackages 
	 * @param doc
	 */
    public SeeStackInfo(SimpleXml xml, String[] localPackages) {
		this.xml = xml;
		this.localPackages = localPackages;
		construct();
	}
    
    private void construct() {
		// load each threadinfo
		int ref = 0;
		for (SimpleXml xxml : xml.get("threadinfos")) {
			ThreadInfo info = new ThreadInfo();
			info.loadFromXml(xxml, ref++);
			threadInfos.add(info);
		}

		// load each packageinfo
		for (SimpleXml xxml : xml.get("packageinfos")) {
			PackageInfo info = new PackageInfo();
			info.loadFromXml(xxml, ref++);
			packageInfos.add(info);
		}
		if(localPackages != null) {
			for(int i=0; i < localPackages.length; ++i) {
				PackageInfo pinfo = new PackageInfo();
				pinfo.description = "Locally Maintained Code";
				pinfo.importance = ImportanceEnum.USER;
				pinfo.namePrefix = localPackages[i];
				pinfo.setRef(ref++);
				packageInfos.add(pinfo);
			}
		}

		// load each methodinfo
		for (SimpleXml xxml : xml.get("methodinfos")) {
			MethodInfo info = new MethodInfo();
			info.loadFromXml(xxml, ref++);
			methodInfos.add(info);
		}    	
    }

	public ThreadInfo findThreadInfo(String threadName) {
		return threadInfos.find(threadName);
	}

	public MethodInfo findMethodInfo(Method method, boolean ignoreUserImportance) {
		return methodInfos.find(method, ignoreUserImportance);
	}

	public PackageInfo findPackageInfo(Method method) {
		return packageInfos.find(method);
	}

}
