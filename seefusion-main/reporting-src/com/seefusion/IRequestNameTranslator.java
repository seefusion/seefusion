/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
public interface IRequestNameTranslator {

	/**
	 * Gets request name (devoid of params) from the request.
	 * Should only "get interesting" when using SES URLs and Fusebox, or other
	 * frameworks/methodologies where a parameter to the request is more important
	 * than the request name itself.
	 * 
	 * @param req Request to generate request name from
	 * @return translated request name
	 */
	String translateRequestName(String uri);
	
}
