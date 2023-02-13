/**
 * 
 */
package com.seestack;

import java.util.HashMap;

import com.seefusion.JSONObject;

/**
 * @author Daryl
 *
 */
public class Method {

	String rawLine;
	String methodName;
	String sourceLoc;
	MethodInfo methodInfo;
	PackageInfo packageInfo;
	ImportanceEnum importance = ImportanceEnum.DELEGATE;
	
	/**
	 * @param rawLine raw text of method line in stack ("	at java.lang.Object.wait(Object.java:429)")
	 * @param methodName dotted method name ("java.lang.Object.wait")
	 * @param sourceLoc source location ("Object.java:429")
	 * @param methodInfo info found for method, or null if not annotated
	 */
	public Method(String rawLine, String methodName, String sourceLoc, SeeStackInfo info) {
		this.rawLine = rawLine;
		this.methodName = methodName;
		this.sourceLoc = sourceLoc;
		this.packageInfo = info.findPackageInfo(this);
		if(packageInfo != null) {
			importance = packageInfo.importance;
		}
		// if the package importance is NOTUSER, then we need to tell methodInfos.find() to ignore USER importance
		this.methodInfo = info.findMethodInfo(this, importance==ImportanceEnum.NOTUSER);
		// if the package shows this to be not-user-code, then demote the package importance to MEDIUM
		// this needs to happen AFTER info.methodInfos.find (so we don't fuck up the imporance override)
		if(importance == ImportanceEnum.NOTUSER) {
			importance = ImportanceEnum.MEDIUM;
		}
		// Now we update the importance with whatever the current line actually has (probably to (query.cfc == HIGH))
		if(methodInfo != null && methodInfo.importance.value() > importance.value()) {
			importance = methodInfo.importance;
		}
	}

	/**
	 * @param info 
	 * @param string2 
	 * @param string 
	 * @param rawText2
	 */
	public Method(String rawLine) {
		// raw text, eg "	- waiting on <0x130184d8> (a jrunx.tyrex.tm.impl.TransactionDomainImpl)"
		this.rawLine = rawLine;
		this.methodName = "";
		this.sourceLoc = "";
	}

	public void addInfos(HashMap<String, Info> infos)  {
		if(packageInfo != null) {
			infos.put(packageInfo.getRef(), packageInfo);
		}
		if(methodInfo != null) {
			infos.put(methodInfo.getRef(), methodInfo);
		}
	}

	public JSONObject toJson()  {
		JSONObject ret = new JSONObject();
		ret.put("name", methodName);
		ret.put("sourceLoc", sourceLoc);
		ret.put("importance", importance.toString());
		ret.put("rawLine", rawLine);
		if(packageInfo != null) {
			ret.put("package", packageInfo.namePrefix);
			ret.put("rawLineAfterPackage", rawLine.substring(packageInfo.namePrefix.length(), rawLine.length()));
			ret.put("packageInfo", packageInfo.getRef());
		}
		if(methodInfo != null) {
			ret.put("methodInfo", methodInfo.getRef());
		}
		return ret;
	}

	void addInfo(HashMap<String, Info> infos) {
		if(methodInfo != null) {
			infos.put(methodInfo.getRef(), methodInfo);
		}
		if(packageInfo != null) {
			infos.put(packageInfo.getRef(), packageInfo);
		}
	}
	
	public String getSourceLoc() {
		return sourceLoc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rawLine == null) ? 0 : rawLine.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Method other = (Method) obj;
		if (importance == null) {
			if (other.importance != null)
				return false;
		} else if (!importance.equals(other.importance))
			return false;
		if (methodInfo == null) {
			if (other.methodInfo != null)
				return false;
		} else if (!methodInfo.equals(other.methodInfo))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		if (packageInfo == null) {
			if (other.packageInfo != null)
				return false;
		} else if (!packageInfo.equals(other.packageInfo))
			return false;
		if (rawLine == null) {
			if (other.rawLine != null)
				return false;
		} else if (!rawLine.equals(other.rawLine))
			return false;
		if (sourceLoc == null) {
			if (other.sourceLoc != null)
				return false;
		} else if (!sourceLoc.equals(other.sourceLoc))
			return false;
		return true;
	}

}
