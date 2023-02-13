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
public class SeeThread {

	private String name;
	private String rawLine;
	private ImportanceEnum importance = ImportanceEnum.DELEGATE;
	private ThreadInfo threadInfo;
	private Methods methods = new Methods();
	private String fingerprint;
	private int count = 0;
	
	public SeeThread(String name, String rawLine, ThreadInfo threadInfo) {
		this.name = name;
		this.rawLine = rawLine;
		this.threadInfo = threadInfo;
	}
	
	public void addMethod(Method m) {
		methods.add(m);
//		get the first method of USER importance and use that as the key for this SeeThread
		if(fingerprint==null && m.importance==ImportanceEnum.USER) {
			fingerprint = m.rawLine;
		}
		if(m.importance.value() > this.getImportance().value()) {
			this.setImportance(m.importance);
		}
	}
	
	void addText(String rawText) {
		methods.add(new Method(rawText));
	}

	/**
	 * @return
	 */
	public JSONObject addInfoAndGetJson(HashMap<String, Info> infos)  {
		if(threadInfo != null) {
			infos.put(threadInfo.getRef(), threadInfo);
		}
		methods.addInfoFrom(infos);
		return toJson();
	}
	
	public JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("name", name);
		ret.put("count", getCount());
		ret.put("importance", getImportance().toString());
		if(threadInfo != null) {
			ret.put("ref", threadInfo.getRef());
		}
		ret.put("methods", methods.toJson());
		return ret;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public ImportanceEnum getImportance() {
		return importance;
	}

	public void setImportance(ImportanceEnum importance) {
		this.importance = importance;
	}

	public void addInfoFrom(HashMap<String, Info> infos) {
		if(threadInfo != null) {
			infos.put(threadInfo.getRef(), threadInfo);
		}
		methods.addInfoFrom(infos);
	}

	public Methods getMethods() {
		return methods;
	}

	public void incrementCount() {
		this.setCount(this.getCount() + 1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getCount();
		result = prime * result
				+ ((fingerprint == null) ? 0 : fingerprint.hashCode());
		result = prime * result
				+ ((importance == null) ? 0 : importance.hashCode());
		result = prime * result + ((methods == null) ? 0 : methods.hashCode());
		// ignore thread name when bucketing
		//result = prime * result + ((name == null) ? 0 : name.hashCode());
		//result = prime * result + ((rawLine == null) ? 0 : rawLine.hashCode());
		result = prime * result
				+ ((threadInfo == null) ? 0 : threadInfo.hashCode());
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
		SeeThread other = (SeeThread) obj;
		if (getCount() != other.getCount())
			return false;
		if (fingerprint == null) {
			if (other.fingerprint != null)
				return false;
		} else if (!fingerprint.equals(other.fingerprint))
			return false;
		if (importance == null) {
			if (other.importance != null)
				return false;
		} else if (!importance.equals(other.importance))
			return false;
		if (methods == null) {
			if (other.methods != null)
				return false;
		} else if (!methods.equals(other.methods))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (rawLine == null) {
			if (other.rawLine != null)
				return false;
		} else if (!rawLine.equals(other.rawLine))
			return false;
		if (threadInfo == null) {
			if (other.threadInfo != null)
				return false;
		} else if (!threadInfo.equals(other.threadInfo))
			return false;
		return true;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}
