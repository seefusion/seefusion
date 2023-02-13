package com.seefusion;

import java.util.ArrayList;
import java.util.Arrays;

public class ThreadStackElements extends ArrayList<ThreadStackElement> {

	private static final long serialVersionUID = 1L;

	public ThreadStackElements(StackTraceElement[] stack) {
		for(StackTraceElement elem : stack) {
			this.add(new ThreadStackElement(elem));
		}
	}

	public ThreadStackElements(JSONArray jsonArray) {
		for(Object elem : jsonArray) {
			if(elem instanceof JSONObject) {
				this.add(new ThreadStackElement((JSONObject)elem));
			}
		}
	}

	public JSONArray toJson() {
		JSONArray ret = new JSONArray();
		for(ThreadStackElement elem : this) {
			ret.add(elem.toJson());
		}
		return ret;
	}

	int hashCode = -1;
	@Override
	public int hashCode() {
		if(hashCode == -1) {
			hashCode = 0;
			for (ThreadStackElement elem : this) {
				int elemHash = elem.hashCode();
				hashCode = 31 * hashCode + elemHash;
			}
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if(o != null && o instanceof Object[]) {
			return Arrays.equals(this.toArray(), (Object[])o);
		}
		return false;
	}
}
