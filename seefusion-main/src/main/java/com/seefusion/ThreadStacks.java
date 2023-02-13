package com.seefusion;

import java.util.HashMap;

class ThreadStacks extends HashMap<Integer, ThreadStack> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ThreadStacks() {
	}

	public ThreadStacks(JSONArray json) {
		if(json==null) {
			return;
		}
		for (Object o : json) {
			if(o instanceof JSONObject) {
				ThreadStack ts = new ThreadStack((JSONObject) o);
				this.put(ts.hashCode(), ts);
			}
		}
	}

	JSONArray toJson() {
		JSONArray ret = new JSONArray();
		for (ThreadStack stack : this.values()) {
			ret.add(stack.toJson());
		}
		return ret;
	}

	public void add(ThreadStack threadStack) {
		Integer hashCode = threadStack.hashCode();
		if(this.containsKey(hashCode)) {
			this.get(hashCode).count++;
		}
		else {
			this.put(hashCode, threadStack);
		}
	}
	
}
