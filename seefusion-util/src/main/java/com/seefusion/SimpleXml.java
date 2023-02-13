package com.seefusion;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class SimpleXml extends LinkedList<SimpleXml> {

	/**
     * 
     */
    private static final long serialVersionUID = -1974920815996799138L;
	private String tagName;
	private String simpleValue;
	private boolean isSimpleValueCDATA = false;
	private LinkedHashMap<String, Object> attributes = new LinkedHashMap<String, Object>();

	public SimpleXml(String tagName) {
		this.tagName = tagName;
	}
	
	public SimpleXml setTagName(String tagName) {
		this.tagName = tagName;
		return this;
	}

	public boolean isSimpleValue() {
		return simpleValue != null;
	}
	
	public String getTagName() {
		return tagName;
	}
	
	public SimpleXml(String tagName, SimpleXml child) {
		this.tagName = tagName;
		this.add(child);
	}
	
	public SimpleXml(String tagName, String msg) {
	    this.tagName = tagName;
	    this.simpleValue = msg == null ? "" : msg;
    }

	public SimpleXml(String tagName, long msg) {
		this.tagName = tagName;
		this.simpleValue = Long.toString(msg);
	}
	
	public SimpleXml(String tagName, int msg) {
		this.tagName = tagName;
		this.simpleValue = Integer.toString(msg);
	}
	
	public SimpleXml(String tagName, float msg) {
		this.tagName = tagName;
		this.simpleValue = Float.toString(msg);
    }

	public SimpleXml(String tagName, boolean msg) {
		this.tagName = tagName;
		this.simpleValue = Boolean.toString(msg);
    }
	
	@Override
	public boolean add(SimpleXml xml) {
		if(xml != null) {
			return super.add(xml);
		}
		return false;
	}
	
	public boolean add(Collection<SimpleXml> xmls) {
		boolean ret = false;
		if(xmls != null) {
			for(SimpleXml xml : xmls) {
				ret = ret || super.add(xml);
			}
		}
		return ret;
	}
	
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		appendTo(ret, 0);
		return ret.toString();
	}

	public static SimpleXml load(String xml) {
		return SimpleXmlParser.parseXml(xml);
	}
	
	private String appendTo(StringBuilder ret, int indentLevel) {
		addIndent(ret, indentLevel);
		ret.append("<").append(tagName);
		for(Map.Entry<String, Object> entry : attributes.entrySet()) {
			ret.append(" ").append(entry.getKey()).append("=\"").append(Util.xmlStringFormat(entry.getValue().toString())).append("\"");
		}
		ret.append(">");
		if(simpleValue != null) {
			if(isSimpleValueCDATA) {
				ret.append("<![CDATA[").append(simpleValue).append("]]>");
			}
			else {
				ret.append(Util.xmlStringFormat(simpleValue));
			}
		}
		else {
			if(this.isEmpty()) {
				if(indentLevel==0) {
					// Flash gets upset if the tag is empty, and will ignore the message.  So, add a stupid child tag if necessary. 
					new SimpleXml("thisSpace", "Intentionally Left Blank.").appendTo(ret, 1);
				}
			}
			else {
				ret.append("\r\n");
				for(SimpleXml response : this) {
					response.appendTo(ret, indentLevel+1);
				}
				addIndent(ret, indentLevel);
			}
		}
		ret.append("</").append(tagName).append(">\r\n");
		return ret.toString();
	}

	private void addIndent(StringBuilder ret, int indentLevel) {
		if(indentLevel==0)
			return;
		for(int i=0; i < indentLevel; i++) {
			ret.append('\t');
		}
	}
	
	public SimpleXml setAttribute(String name, String value) {
		json = null;
	    attributes.put(name, value);
	    return this;
    }
	
	public SimpleXml setSimpleValue(String value) {
		json = null;
		this.simpleValue = value;
		this.isSimpleValueCDATA = false;
		return this;
	}
	
	public SimpleXml setCDATA(String value) {
		json = null;
		this.simpleValue = value;
		this.isSimpleValueCDATA = true;
		return this;
	}
	
	/**
	 * helper method to add a simple subtag
	 * @param tagName
	 * @param tagStringValue
	 */
	public SimpleXml addTag(String tagName, Object tagValue) {
		if(tagValue==null) {
			super.add(new SimpleXml(tagName));
		}
		else if(tagValue instanceof SimpleXml) {
			super.add(new SimpleXml(tagName, (SimpleXml) tagValue));
		}
		else {
			super.add(new SimpleXml(tagName, tagValue.toString()));
		}
		return this;
    }

	/**
	 * returns first subtag matching tagName
	 * @param tagName
	 * @return Xml or null
	 */
	public SimpleXml get(String tagName) {
		for(SimpleXml tag : this) {
			if(tag.getTagName().equalsIgnoreCase(tagName)) {
				return tag;
			}
		}
	    return null;
    }

	/**
	 * returns n'th subtag matching tagName
	 * @param tagName
	 * @param n
	 * @return Xml or null
	 */
	public SimpleXml get(String tagName, int n) {
		int i=0;
		for(SimpleXml tag : this) {
			if(tag.getTagName().equalsIgnoreCase(tagName)) {
				i++;
				if(i==n) {
					return tag;
				}
			}
		}
		return null;
	}

	/**
	 * returns simple value string, or null if not a simple value
	 * @return String or null (if n/a)
	 */
	public String getSimpleValue() {
		return simpleValue;
    }

	public String getString(String name) {
		return attributes.get(name).toString();
    }

	/**
	 * Returns the simple value of the named subtag
	 * This is a helper from when xml was parsed into a simple hashmap 
	 * @param name of the subtag
	 * @return simple string value
	 */
	public String getProperty(String name) {
		SimpleXml subTag = get(name);
		if(subTag==null)
			return null;
		return subTag.getSimpleValue();
    }
	
	public String getProperty(String name, String dfault) {
	    String ret = getProperty(name);
	    if(ret==null) {
	    	ret = dfault;
	    }
	    return ret;
    }

	/**
	 * Sets the simple value of the [first] named subtag
	 * This is a helper from when xml was parsed into a simple hashmap 
	 * @param name of the subtag
	 * @param value of the subtag
	 * @return self (for chaining)
	 */
	public SimpleXml setProperty(String name, Object value) {
		json = null;
		if(value!=null) {
			SimpleXml subTag = get(name);
			if(subTag==null) {
				subTag = new SimpleXml(name);
				add(subTag);
			}
			subTag.setSimpleValue(value.toString());
		}
		return this;
	}

	public SimpleXml setCDATAProperty(String name, String value) {
		json = null;
		SimpleXml subTag = get(name);
		if(subTag==null) {
			subTag = new SimpleXml(name);
			add(subTag);
		}
		subTag.setCDATA(value.toString());
		return this;
	    
    }
	
	JSONObject json = null;
	public JSONObject toJson() {
		if(json == null) {
			JSONObject ret = new JSONObject();
			appendJsonTo(ret);
			json = ret;
		}
		return json;
	}
	
	public void appendJsonTo(JSONObject ret) {
		// attributes
		for(Map.Entry<String, Object> entry : attributes.entrySet()) {
			ret.put(entry.getKey(), entry.getValue().toString());
		}
		// subtags
		for(SimpleXml s : this) {
			if(s.isSimpleValue()) {
				if(ret.has(s.getTagName())) {
					ret.accumulate(s.getTagName(), s.getSimpleValue());
				}
				else {
					ret.put(s.getTagName(), s.getSimpleValue());
				}
			}
			else {
				ret.accumulate(s.getTagName(), s.toJson());
			}
		}
	}

	public boolean remove(String name) {
		boolean found = false;
		for(Iterator<SimpleXml>iter = this.iterator(); iter.hasNext(); ) {
			SimpleXml xml = iter.next();
			if(xml.getTagName().equalsIgnoreCase(name)) {
				found = true;
				iter.remove();
				break;
			}
		}
		return found;
	}

	public SimpleXml addTagCdata(String tagName, String value) {
		if(value==null) {
			super.add(new SimpleXml(tagName));
		}
		else if(value.equals("")) {
			// just add this as a blank, don't waste the CDATA wrapper
			super.add(new SimpleXml(tagName, ""));
		}
		else {
			SimpleXml ret = new SimpleXml(tagName);
			ret.setCDATA(value);
			super.add(ret);
		}
		return this;
	}
	
}
