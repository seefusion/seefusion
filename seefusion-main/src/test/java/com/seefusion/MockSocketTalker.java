package com.seefusion;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ResourceBundle;

class MockSocketTalker extends HttpTalker {

	MockSocket mockSocket;
	
	MockSocketTalker(SeeFusion sf) throws IOException {
		this(sf, new MockSocket(), "get / http/1.0");
	}
	
	MockSocketTalker(SeeFusion sf, String req) throws IOException {
		this(sf, new MockSocket(), req);
	}
	
	private MockSocketTalker(SeeFusion sf, MockSocket s, String req) throws IOException {
		super(sf, s);
		this.mockSocket = s;
		this.httpRequest = new HttpRequest(sf, req);
		String configJsonRaw = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("com/seefusion/config.json"))).lines().collect(Collectors.joining("\n"));
		this.configJson = new JSONArray(configJsonRaw);
	}
	
	@Override
	HttpRequest getHttpRequest() {
		return super.getHttpRequest();
	}
	
	@Override
	OutputStream getOutputStream() {
		try {
			return mockSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
