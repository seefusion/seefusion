/**
 * 
 */
package com.seestack;

import java.util.LinkedList;

/**
 * @author Daryl
 *
 */
public class MethodInfos extends LinkedList<MethodInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	MethodInfos() {
		super();
	}
	
	public MethodInfo find(Method method, boolean ignoreUserImportance) {
		for(MethodInfo info : this) {
			if(ignoreUserImportance && info.importance == ImportanceEnum.USER) {
				//nop
			}
			else {
				if(!info.isPrefix) {
					if(method.sourceLoc.indexOf(info.namePrefix) != -1) {
						return info;
					}
				}
				else if(method.methodName.startsWith(info.namePrefix)) {
					return info;
				}
			}
		}
		return null;
	}
	
}
