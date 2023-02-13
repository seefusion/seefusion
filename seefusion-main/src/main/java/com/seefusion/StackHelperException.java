/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
public class StackHelperException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5136095351067577142L;
	
	Exception nestedException;
	
	StackHelperException(Exception e) {
		super(e);
		nestedException = e;
	}
	
	public String toString() {
		return nestedException.toString();
	}
	
}
