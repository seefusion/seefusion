package com.seefusion;

import java.util.LinkedHashMap;
import java.util.Map;

class SizedHashMap<K,V> extends LinkedHashMap<K,V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1217883655007298565L;
	int maxSize;
	
	public SizedHashMap(int maxSize) {
		this.maxSize = maxSize;
	}
	
	@Override
	protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
		return size() > maxSize;
	}
	
}
