package com.seefusion;

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

public class MockFilterConfig implements FilterConfig {

	private final Properties params;
	
	public MockFilterConfig() {
		params = new Properties();
		params.setProperty("name", "MockFilter");
	}
	
	@Override
	public String getFilterName() {
		return "MockFilter";
	}

	@Override
	public String getInitParameter(String name) {
		return params.getProperty(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return new MapKeyEnumerator(params); 
	}
	
	public void setInitParameter(String name, String value) {
		params.setProperty(name, value);
	}

	@Override
	public ServletContext getServletContext() {
		// implement if needed
		return null;
	}

}
