package com.seefusion;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class SeeFusionUrlStreamHandler extends URLStreamHandler {


	public SeeFusionUrlStreamHandler() {
	}

	@Override
	protected URLConnection openConnection(URL u) throws IOException {
		return new SeeFusionUrlConnection(u);
	}

}
