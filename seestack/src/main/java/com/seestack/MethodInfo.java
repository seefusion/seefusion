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
public class MethodInfo implements Info {

	String ref;
	String namePrefix;
	boolean isPrefix = true;
	String description;
	String longDescription;
	ImportanceEnum importance;
	
	/**
	 * @param elem
	 */
	public void loadFromXml(SimpleXml xml, int ref) {
		this.ref = "method" + ref;
		this.namePrefix = xml.getProperty("nameprefix");
		if(this.namePrefix.startsWith("*")) {
			this.isPrefix = false;
			this.namePrefix = this.namePrefix.substring(1) + ":";
		}
		this.description = xml.getProperty("description");
		this.longDescription = xml.getProperty("longdescription");
		this.importance = ImportanceEnum.parseImportance(xml.getProperty("importance")); 
	}

	
	/* (non-Javadoc)
	 * @see com.seestack.Info#toXML()
	 */
	public SimpleXml toXML() {
		SimpleXml ret = new SimpleXml("info");
		ret.setAttribute("type", "method");
		ret.setAttribute("name", ref);
		ret.setProperty("namePrefix", namePrefix);
		ret.setProperty("description", description);
		ret.setProperty("longDescription", longDescription);
		ret.setProperty("importance", importance.toString());
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see com.seestack.Info#toJson()
	 */
	public JSONObject toJson()  {
		JSONObject ret = new JSONObject();
		ret.put("type", "method");
		ret.put("name", ref);
		ret.put("namePrefix", namePrefix);
		ret.put("description", description);
		ret.put("longDescription", longDescription);
		ret.put("importance", importance.toString());
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.seestack.Info#getRef()
	 */
	public String getRef() {
		return ref;
	}

}
