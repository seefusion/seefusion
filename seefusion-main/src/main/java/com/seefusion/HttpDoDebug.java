package com.seefusion;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

class HttpDoDebug extends HttpRequestHandler implements Debugger {

	/*
	 * handles the doGet for debug output talker.setDebuggerIP(...) should be
	 * called before this method is chained
	 * 
	 * @see com.seefusion.HttpPage#doGet(com.seefusion.HttpTalker)
	 */
	private String debuggerIP;
	
	private OutputStream out;
	
	public String getDebuggerIP(){
		return debuggerIP;
	}

	public String doGet(HttpTalker talker) {
		Properties urlParams = talker.getUrlParams();
		String ip = urlParams.getProperty("ip");
		
		out = talker.getOutputStream();
		
		StringBuilder ret = new StringBuilder(1000);
		ret.append("HTTP/1.0 200 OK\r\ncontent-type: text/html\r\n\r\n");
		ret.append("<html>\r\n<body>\r\n");
		ret.append("<h1>Debugging started...</h1>\r\n");

		if(ip != null && ip.equals("mine")){
			debuggerIP = talker.getRemoteAddr();
		} else {
			debuggerIP = "";
		}
		RequestInfo.registerDebugger(this);
		try {
			out.write(ret.toString().getBytes());
			out.flush();
		} catch(IOException e){
			unRegisterDebugger();
		}
		
		// TODO: put a failsafe in to turn off debuggers after a minute or two in case the user doesn't hit stop or navigates away/etc. 
		
		talker.setAutoFlush(false);
		return null;
	}

	public void debug(DebugMessage dbm) {
		try {
			out.write( ("<div class=\"debug\">" + dbm.toString() + "</div>").getBytes() );
			out.flush();
		} catch (IOException e){
			unRegisterDebugger();
		}
	}
	
	public void unRegistered(){
		// finish the original doGet
		try {
			out.write("<h1>Stopped debugging!</h1>\r\n</body>\r\n</html>".getBytes());
			out.flush();
			out.close();
		} catch(IOException e){
			// don't care
		}
	}
	
	private void unRegisterDebugger(){
		RequestInfo.unRegisterDebugger(this);
	}

	@Override
	Perm getPerm() {
		return new Perm(Perm.LOGGEDIN);
	}
}
