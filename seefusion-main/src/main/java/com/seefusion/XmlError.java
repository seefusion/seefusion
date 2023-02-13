package com.seefusion;

public class XmlError extends SimpleXml {

	/**
     * 
     */
    private static final long serialVersionUID = 7895486241125993763L;

	XmlError(String msg) {
		super("error", msg);
	}

}
