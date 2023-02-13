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
public class PackageInfo implements Info {

	String ref;
	String namePrefix;
	String description;
	ImportanceEnum importance;
	
	/**
	 * @param elem
	 */
	public void loadFromXml(SimpleXml xml, int ref) {
		setRef(ref);
		this.namePrefix = xml.getProperty("nameprefix");
		this.description = xml.getProperty("description");
		this.importance = ImportanceEnum.parseImportance(xml.getProperty("importance")); 
	}

	/* (non-Javadoc)
	 * @see com.seestack.Info#getRef()
	 */
	public String getRef() {
		return ref;
	}

	/* (non-Javadoc)
	 * @see com.seestack.Info#toXML()
	 */
	public SimpleXml toXML() {
		SimpleXml ret = new SimpleXml("info");
		ret.setAttribute("type", "package");
		ret.setAttribute("name", ref);
		ret.setProperty("namePrefix", namePrefix);
		ret.setProperty("description", description);
		ret.setProperty("importance", importance.toString());
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.seestack.Info#toJson()
	 */
	public JSONObject toJson()  {
		JSONObject ret = new JSONObject();
		ret.put("type", "package");
		ret.put("name", ref);
		ret.put("namePrefix", namePrefix);
		ret.put("description", description);
		ret.put("importance", importance.toString());
		return ret;
	}

	/**
	 * @param i
	 */
	public void setRef(int ref) {
		this.ref = "package" + ref; 
	}

}
