/*
 * Observer.java
 *
 */

package com.seefusion;

/**
 * see also Subject
 * 
 */
public interface Observer<T> {
    
	void update(T object);
    
}
