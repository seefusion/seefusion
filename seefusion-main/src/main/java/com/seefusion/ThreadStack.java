package com.seefusion;

class ThreadStack {
	
	static final char QUOTE = '\"';
	static final char TAB = '\t';
	static final String CRLF = "\r\n";

	String threadName;
	Thread.State threadState;
	ThreadStackElements stack;
	int count = 1;
	
	ThreadStack(Thread thread) {
		this(thread, thread.getStackTrace());
	}
	
	ThreadStack(Thread thread, StackTraceElement[] stack) {
		this.threadName = thread.getName();
		this.threadState = thread.getState();
		this.stack = new ThreadStackElements(stack);
	}
	
	public ThreadStack(String name, String state) {
		this.threadName = name;
		this.threadState = Thread.State.valueOf(state);
	}

	public ThreadStack(JSONObject json) {
		this(json.getString("name"), json.getString("state"));
		if(json.has("count")) {
			count = json.getInt("count");
		}
		if(json.has("stack")) {
			stack = new ThreadStackElements(json.getJSONArray("stack"));
		}
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();

		ret.append('\'').append(threadName).append('\'');

		if(threadState == Thread.State.BLOCKED) {
			ret.append(" waiting for monitor entry");
		}
		else if (threadState == Thread.State.NEW) {
			ret.append(" not started");
		}
		else if (threadState == Thread.State.RUNNABLE) {
			ret.append(" runnable");
		}
		else if (threadState == Thread.State.WAITING) {
			ret.append(" sleeping");
		}
		else if (threadState == Thread.State.TIMED_WAITING) {
			ret.append(" in Object.wait()");
		}
		else if (threadState == Thread.State.TERMINATED) {
			ret.append(" zombie");
		}
		else {
			ret.append(" unknown state");
		}
		ret.append(CRLF);
		if (this.stack == null) {
			ret.append("        at Unknown Location").append(CRLF);
		}
		else if(this.stack.isEmpty()) {
			ret.append("        at Unknown Location").append(CRLF);
		}
		else {
			for(ThreadStackElement elem : stack) {
				ret.append(elem.toString());
				ret.append(CRLF);
			}
		}
		return ret.toString();
	}

	JSONObject toJson() {
		JSONObject ret = new JSONObject();
		ret.put("name", threadName);
		ret.put("state", threadState.toString());
		ret.put("count", count);
		if (this.stack != null) {
			ret.put("stack", stack.toJson());
		}
		return ret;
	}
	
	@Override
	public int hashCode() {
		// ignore name and state; we really want to find identical stacks
		return stack.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return stack.equals(obj);
	}

}

