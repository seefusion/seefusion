/**
 * 
 */
package com.seefusion;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Daryl
 *
 */
public class LimitedLinkedHashMap<K,V> extends LinkedHashMap<K,V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1261601786326810418L;
	
	
	int maxSize;
	
	public LimitedLinkedHashMap(int size, int maxSize) {
		super(size, (float)0.75, true);
		this.maxSize = maxSize;
	}
	
	protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
		return this.size() > maxSize;
	}
	
}
