package com.seefusion;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

class MapKeyEnumerator implements Enumeration<String> {

	private Iterator<?> iter;

	public MapKeyEnumerator(Map<?, ?> params) {
		iter = params.keySet().iterator();
	}

	@Override
	public boolean hasMoreElements() {
		return iter.hasNext();
	}

	@Override
	public String nextElement() {
		return (String) iter.next();
	}

}
