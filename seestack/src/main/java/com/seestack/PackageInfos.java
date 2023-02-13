/**
 * 
 */
package com.seestack;

import java.util.LinkedList;

/**
 * @author Daryl
 *
 */
public class PackageInfos extends LinkedList<PackageInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PackageInfos() {
		super();
	}
	
	public PackageInfo find(Method method) {
		for(PackageInfo info : this) {
			if(method.methodName.startsWith(info.namePrefix)) {
				return info;
			}
		}
		return null;
	}
	
}
