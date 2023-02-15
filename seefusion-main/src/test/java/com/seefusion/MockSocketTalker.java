package com.seefusion;

import java.util.ResourceBundle;
import java.io.*;
import java.util.stream.Collectors;

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
		// System.out.println(new File(".").getAbsolutePath());
		String configJsonRaw = new BufferedReader(new InputStreamReader(new FileInputStream("./src/main/resources/com/seefusion/config.json"))).lines().collect(Collectors.joining("\n"));
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
