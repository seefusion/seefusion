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
public class ThreadInfo implements Info {

	String ref;
	String namePrefix;
	String description;
	ImportanceEnum importance;
	boolean isAppThread;
	
	/**
	 * @param elem
	 */
	void loadFromXml(SimpleXml xml, int ref) {
		this.ref = "ref" + ref;
		this.namePrefix = xml.getProperty("nameprefix");
		this.description = xml.getProperty("description");
		String isAppThread = xml.getProperty("isappthread");
		this.isAppThread = (isAppThread!= null && isAppThread.toLowerCase().equals("true"));
		this.importance = ImportanceEnum.parseImportance(xml.getProperty("importance")); 
	}

	/* (non-Javadoc)
	 * @see com.seestack.Info#toXML()
	 */
	public SimpleXml toXML() {
		SimpleXml ret = new SimpleXml("info");
		ret.setAttribute("type", "thread");
		ret.setAttribute("name", ref);
		ret.setProperty("namePrefix", namePrefix);
		ret.setProperty("description", description);
		ret.setProperty("importance", importance.toString());
		ret.setProperty("isAppThread", Boolean.toString(isAppThread));
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.seestack.Info#toJson()
	 */
	public JSONObject toJson()  {
		JSONObject ret = new JSONObject();
		ret.put("type", "thread");
		ret.put("name", ref);
		ret.put("namePrefix", namePrefix);
		ret.put("description", description);
		ret.put("importance", importance.toString());
		ret.put("isAppThread", Boolean.toString(isAppThread));
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.seestack.Info#getRef()
	 */
	public String getRef() {
		return ref;
	}
	
}
