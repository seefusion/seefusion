package com.seefusion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

class HttpRequestMap {
	
	Map<String, HttpRequestHandler> httpPages = new HashMap<String, HttpRequestHandler>();
	HttpDoFileServer fileServer;

	static HttpRequestMap instance;
	static String webroot;
	
	static HttpRequestMap getInstance(String webroot) throws IOException {
		if(instance==null || HttpRequestMap.webroot!=webroot) {
			// if this is done concurrently by two threads, there's no harm.
			instance = new HttpRequestMap(webroot);
		}
		return instance;
	}
	
	private HttpRequestMap(String webroot) throws IOException {
		String jarLoc = webroot;
		if(webroot==null) {
			jarLoc = SeeFusion.getJarLocation();
		}
		File f = new File(jarLoc);
		if(f.isFile()) {
			// if we're not serving files off of the local filesystem, then enumerate every file in the jar's /ui/ directory
			// find all files in the jar's /ui folder and make page entries
			ZipInputStream zip=new ZipInputStream(new FileInputStream(f));
			try {
				for(ZipEntry entry=zip.getNextEntry(); entry != null; entry=zip.getNextEntry()) {
			    	if(!entry.isDirectory() && entry.getName().startsWith("ui/")) {
			    		String name = entry.getName().substring(2);
			    		/*
			    		int size = (int) entry.getSize();
			    		if( !name.contains("Localization") ) {
			    			debug("Adding page " + name + " (" + size + " bytes)");
			    		}
			    		 */
						byte[] contents = readZipEntry(zip);
						HttpDoFile doFile = new HttpDoFile(name, contents);
						httpPages.put(name.toLowerCase(), doFile);
						if(name.equals("/index.html")) {
							//debug("Also adding page / alias");
							httpPages.put("/", doFile);
						}
			    	}
				}
			}
			finally {
				zip.close();
			}
		}
		else {
			// we're pulling files off the filesystem, instead of from the jar
			try {
				fileServer = new HttpDoFileServer(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// debugging
		httpPages.put("/debugstart.html", new HttpDoDebug());
		
		// legacy
		httpPages.put("/counters", new HttpDoCounters());
		httpPages.put("/countersjava", new HttpDoCountersJava());
		httpPages.put("/xml", new XmlDoGetServer());
		httpPages.put("/xml/stack", new XmlDoGetStack());

		httpPages.put("/log", new HttpDoLog());
		httpPages.put("/stack", new HttpDoStacktrace());
		httpPages.put("/gc", new HttpDoGC());
		
		httpPages.put("/seestack", new HttpDoGetProfileAnalysis());
		httpPages.put("/seestackraw", new HttpDoGetProfileRaw());
		
		httpPages.put("/dashboard", new XmlDoDashboard());
		httpPages.put("/dashboard/xml", new XmlDoDashboard());
		httpPages.put("/dashboard/xmlall", new XmlDoDashboardAll());
		httpPages.put("/dashboard/singlerow", new HttpDoDashboardSinglerow());

		// pages requiring AuthKill
		httpPages.put("/kill", new HttpDoKill());
		httpPages.put("/killstop", new HttpDoKillstop());

		// json
		httpPages.put("/json/auth", new JsonDoAuth());
		httpPages.put("/json/logout", new JsonDoLogout());
		httpPages.put("/json/kill", new JsonDoKill());
		httpPages.put("/json/serverinfo", new JsonDoServerInfo());
		httpPages.put("/json/gc", new JsonDoInvokeGC());
		httpPages.put("/json/getrequests", new JsonDoRequestList());
		httpPages.put("/json/gethistoryminutes", new JsonDoGetHistoryMinutes());
		httpPages.put("/json/gethistorysnapshot", new JsonDoGetHistorySnapshot());
		httpPages.put("/json/getconfig", new JsonDoGetConfig());
		httpPages.put("/json/saveconfig", new JsonDoSaveConfig());
		httpPages.put("/json/saveconfigs", new JsonDoSaveConfigs());
		httpPages.put("/json/log", new JsonDoLog());
		httpPages.put("/json/about", new JsonDoAbout());
		httpPages.put("/json/counters", new JsonDoCounters());
		httpPages.put("/json/getstack", new JsonDoStack());
		httpPages.put("/json/getprofilerstatus", new JsonDoGetProfilerStatus());
		httpPages.put("/json/getactiveprofile", new JsonDoGetActiveProfileStatus());
		httpPages.put("/json/profilerstart", new JsonDoProfileStart());
		httpPages.put("/json/profilerstop", new JsonDoProfileServerStop());
		httpPages.put("/json/getprofile", new JsonDoGetProfile());
		httpPages.put("/json/deleteprofile", new JsonDoDeleteProfile());
		httpPages.put("/json/getmonitorconfig", new JsonDoGetMonitorConfig());
		httpPages.put("/json/savemonitor", new JsonDoSaveRule());
		httpPages.put("/json/deletemonitor", new JsonDoDeleteRule());
		httpPages.put("/json/getdashboardservers", new JsonDoGetDashboard());
		httpPages.put("/json/incidents", new JsonDoGetIncidents());
		httpPages.put("/json/incident", new JsonDoGetIncident());
		httpPages.put("/json/deleteincident", new JsonDoDeleteIncident());
		httpPages.put("/dashboard/json", new JsonDoDashboard());
		// dos
		httpPages.put("/json/dosgetconfig", new JsonDosGetConfig());
		httpPages.put("/json/dossaveconfig", new JsonDosSaveConfig());
		httpPages.put("/json/dosgetstatus", new JsonDosGetStatus());
		httpPages.put("/json/dosblockremove", new JsonDosBlockRemove());
		httpPages.put("/json/dosexclusionadd", new JsonDosExclusionAdd());
		httpPages.put("/json/dosexclusionremove", new JsonDosExclusionRemove());
		httpPages.put("/json/dosblockadd", new JsonDosBlockAdd());
	}

	private byte[] readZipEntry(ZipInputStream zip) throws IOException {
		int pos;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[16384];
		while( (pos = zip.read(buf)) != -1 ) {
			out.write(buf, 0, pos);
		}
		return out.toByteArray();
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	public HttpRequestHandler getHttpRequest(String pageName) {
		HttpRequestHandler ret = httpPages.get(pageName);
		// debug("HttpRequestMap: handler for " + pageName + " is " + (ret==null ? "null" : ret.getClass().getName()) );
		if(fileServer != null) {
			if(ret==null || ret instanceof HttpDoFile) {
				return fileServer;
			}
		}
		return ret;
	}

	public boolean containsPage(String pageName) {
		// debug("HttpRequestMap: Checking for "+ pageName);
		boolean ret = httpPages.containsKey(pageName);
		if(!ret && fileServer != null && fileServer.containsPage(pageName)) {
			ret = true;
		}
		return ret;
	}

}
