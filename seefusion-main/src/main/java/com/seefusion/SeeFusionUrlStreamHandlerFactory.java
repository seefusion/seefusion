package com.seefusion;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class SeeFusionUrlStreamHandlerFactory implements URLStreamHandlerFactory {

	SeeFusionUrlStreamHandlerFactory() {
	}
	
	@Override
	public URLStreamHandler createURLStreamHandler(String protocol) {
		if("seefusion".equals(protocol)) {
			return new SeeFusionUrlStreamHandler();
		}
		return null;
	}

}
