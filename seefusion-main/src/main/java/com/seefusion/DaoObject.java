package com.seefusion;

public interface DaoObject {

	JSONObject toJson();

	String getId();
	
	boolean isPersisted();

	void setPersisted(boolean isPersisted);
	
}
