/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
public interface MonitorPlugin {
	
	/**
	 * called when plugin first loaded.
	 */
	void init(String args);
	
	MonitorResult doMonitor();
	
	void destroy();
	
}
