package com.seefusion;

class RawXml extends SimpleXml {

	/**
     * 
     */
    private static final long serialVersionUID = 7123719778359737688L;
	private String xml;
	
	RawXml(String xml) {
		super("raw");
		this.xml = xml;
	}
	
	public String toString() {
		return xml;
	}
}
