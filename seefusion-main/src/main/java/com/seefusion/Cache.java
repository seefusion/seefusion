package com.seefusion;

import java.util.LinkedHashMap;

public class Cache<T extends DaoObject> extends LinkedHashMap<String, T> {

	private static final long serialVersionUID = 7211801708894641983L;
	private int maxInMemoryCount;
	
	Cache(int maxInMemoryCount) {
		super(maxInMemoryCount+1);
		this.maxInMemoryCount = maxInMemoryCount;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<String, T> eldest) {
		return this.size() > maxInMemoryCount;
	}
	
}
