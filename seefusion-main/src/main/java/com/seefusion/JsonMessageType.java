package com.seefusion;

public enum JsonMessageType {

	SUCCESS ("success")
	,INFO ("info")
	,WARN ("warn")
	,ERROR ("error")
	;
	private String value;

	JsonMessageType(String value) {
		this.value = value;
	}

	String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}
}
