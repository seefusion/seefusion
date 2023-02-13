/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
interface Debugger {

	void debug(DebugMessage message);
	
	String getDebuggerIP();

	void unRegistered();
	
}
