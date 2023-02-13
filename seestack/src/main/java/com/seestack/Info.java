/**
 * 
 */
package com.seestack;

import com.seefusion.JSONObject;
import com.seefusion.SimpleXml;

/**
 * @author Daryl
 *
 */
public interface Info {

	public String getRef();
	public SimpleXml toXML();
	public JSONObject toJson() ;
	
}
