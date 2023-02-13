package com.seefusion;

/**
 * Marker iterface; "this response can be cached for up to n sec"
 * 
 * @author Daryl
 *
 */
public interface Cacheable {

	long getCacheableDurationMs();
	
}
