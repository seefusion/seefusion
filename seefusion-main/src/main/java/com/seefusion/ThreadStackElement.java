package com.seefusion;

public class ThreadStackElement {

	private final String className;
	private final String methodName;
	private final int lineNumber;
	private final String fileName;
	private final boolean isNativeMethod;
	
	public ThreadStackElement(JSONObject elem) {
		this.className = elem.getString("className");
		this.methodName = elem.has("methodName") ? elem.getString("methodName") : null;
		this.lineNumber = elem.getInt("lineNumber");
		this.fileName = elem.has("fileName") ? elem.getString("fileName") : null;
		this.isNativeMethod = elem.getBoolean("isNativeMethod");
	}

	public ThreadStackElement(StackTraceElement elem) {
		this.className = elem.getClassName();
		this.methodName = elem.getMethodName() == null ? null : elem.getMethodName();
		this.lineNumber = elem.getLineNumber();
		this.fileName = elem.getFileName() == null ? null : elem.getFileName();
		this.isNativeMethod = elem.isNativeMethod();
	}

	JSONObject toJson() {
		JSONObject jsonElem = new JSONObject();
		jsonElem.put("className", className);
		jsonElem.put("methodName", getMethodName());
		jsonElem.put("lineNumber", getLineNumber());
		jsonElem.put("fileName", getFileName());
		jsonElem.put("isNativeMethod", isNativeMethod());
		return jsonElem;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isNativeMethod() {
		return isNativeMethod;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder(200);
		ret.append(className)
		.append('.')
		.append(methodName);
		if(isNativeMethod()) {
			ret.append("(Native Method)");
		}
		else {
			ret.append('(')
				.append(fileName)
				.append(':')
				.append(lineNumber)
				.append(')');
		}
		return ret.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + (isNativeMethod ? 1231 : 1237);
		result = prime * result + lineNumber;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ThreadStackElement other = (ThreadStackElement) obj;
		if(className == null) {
			if(other.className != null)
				return false;
		}
		else if(!className.equals(other.className))
			return false;
		if(fileName == null) {
			if(other.fileName != null)
				return false;
		}
		else if(!fileName.equals(other.fileName))
			return false;
		if(isNativeMethod != other.isNativeMethod)
			return false;
		if(lineNumber != other.lineNumber)
			return false;
		if(methodName == null) {
			if(other.methodName != null)
				return false;
		}
		else if(!methodName.equals(other.methodName))
			return false;
		return true;
	}
	

}
