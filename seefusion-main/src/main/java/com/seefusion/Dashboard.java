/*
 * Dashboard.java
 *
 * Created on December 11, 2004, 8:49 AM
 */

package com.seefusion;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Daryl
 */
class Dashboard {
    
    SeeFusion sf;
    
    /** Creates a new instance of Dashboard */
    Dashboard(SeeFusion sf) {
        this.sf = sf;
    }
    
    String doGet(List<ServerEntry> dashboardServersList, MessageFormatFactory messageFormats, String stylesheet) {
        StringBuilder ret = new StringBuilder();
        LinkedList<DashboardFetcher> fetchers = new LinkedList<DashboardFetcher>();
        
        //header
        Object[] params = new Object[10];
		params[0] = SeeFusion.getVersion() + " " + SeeFusionMain.COPYRIGHT;
        params[1] = new Date();
		params[2] = "";
        params[3] = stylesheet;
//        messageFormats.format("dashboardserverheader", params, ret);
        messageFormats.format("dashboardheader", null, ret);
        
        if( dashboardServersList == null ) {
            ret = new StringBuilder();
            params[0] = "";
            params[1] = "";
            params[2] = "";
            params[3] = "";
            params[4] = "No entries in the dashboardServer parameter in web.xml.";
            messageFormats.format("dashboarddetailservererror", params, ret);
            messageFormats.format("dashboardfooter", params, ret);
            return ret.toString();
        }
        
        for(ServerEntry curServer : dashboardServersList) {
            fetchers.addLast(DashboardFetcher.getDashboardFetcher(curServer, "/dashboard/singlerow", sf.getHttpPasswordB64Token()));
        }
        
        boolean alt=true;
        String rowStyle="";
        for(DashboardFetcher df : fetchers) {
            df.join();
            if(df.isResultOK()) {
                //assignment in if() is intentional:
                if( alt = !alt ) {
                    rowStyle="class=\"alt\"";
                } else {
                    rowStyle = "";
                }
                ret.append(df.getResult().replaceFirst("#rowstyle#", rowStyle));
                
            } else {
                ret = new StringBuilder();
                params[0] = "class=\"alt\"";
                params[1] = "bgcolor=red";
                params[2] = "<b>DOWN</b>";
                params[3] = df.getServerEntry().getName();
                params[4] = df.getResult();
                messageFormats.format("dashboarddetailservererror", params, ret);
            }
        }
        
        ret = new StringBuilder();
        messageFormats.format("dashboardfooter", params, ret);
        return ret.toString();
    }
    
    SimpleXml doGetXml(List<ServerEntry> dashboardServersList) {
        LinkedList<DashboardFetcher> fetchers = new LinkedList<DashboardFetcher>();
        
        if( dashboardServersList == null ) {
            return new SimpleXml("status", "No entries in the dashboardServers configuration parameter.");
        }
        
        for(ServerEntry curServer: dashboardServersList) {
            fetchers.addLast(DashboardFetcher.getDashboardFetcher(curServer, "/dashboard/xml", sf.getHttpPasswordB64Token()));
        }
        
        SimpleXml ret = new SimpleXml("dashboard");
        SimpleXml xml;
        for(DashboardFetcher df : fetchers) {
            df.join();
            if(df.isResultOK()) {
            	RawXml result = new RawXml(df.getResult());
            	if(result.toString().indexOf("<dashboardinfo>")==0) {
            		ret.add(result);
            	}
            	else {
            		// invalid response
            		xml = new SimpleXml("dashboardinfo");
            		xml.add(new SimpleXml( "name", df.getServerEntry().getName() ));
            		xml.add(new SimpleXml( "status", "UNKNOWN" ));
            		xml.add(new SimpleXml( "statuscolor", "yellow" ));
            		xml.add(new SimpleXml( "serverurl", "df.getServerEntry().getName()" ));
            		xml.add(new SimpleXml( "numcurrentrequests", "0" ));
            		xml.add(new SimpleXml( "maxcurrentrequests", "0" ));
            		xml.add(new SimpleXml( "pps10s", "0" ));
            		xml.add(new SimpleXml( "pagetime10s", "0" ));
            		xml.add(new SimpleXml( "pps60s", "0" ));
            		xml.add(new SimpleXml( "pagetime60s", "0" ));
            		xml.add(new SimpleXml( "curmemory", "0" ));
            		xml.add(new SimpleXml( "maxmemory", "1" ));
            		xml.add(new SimpleXml( "uptime", "0" ));
            		xml.add(new SimpleXml( "longestpagetime" ));
            		xml.add(new SimpleXml( "totalquerytime" ));
            		xml.add(new SimpleXml( "totalquerycount" ));
            		xml.add(new SimpleXml( "longestquerytime" ));
            		xml.add(new SimpleXml( "longestqueryrows" ));
            		xml.add(new SimpleXml( "requesturi" ));
        			ret.add(xml); 
            	}
            } else {
        		xml = new SimpleXml("dashboardinfo");
        		xml.add(new SimpleXml( "name", df.getServerEntry().getName() ));
        		xml.add(new SimpleXml( "status", "DOWN" ));
        		xml.add(new SimpleXml( "statuscolor", "red" ));
        		xml.add(new SimpleXml( "serverurl", "df.getServerEntry().getName()" ));
        		xml.add(new SimpleXml( "numcurrentrequests", "0" ));
        		xml.add(new SimpleXml( "maxcurrentrequests", "0" ));
        		xml.add(new SimpleXml( "pps10s", "0" ));
        		xml.add(new SimpleXml( "pagetime10s", "0" ));
        		xml.add(new SimpleXml( "pps60s", "0" ));
        		xml.add(new SimpleXml( "pagetime60s", "0" ));
        		xml.add(new SimpleXml( "curmemory", "0" ));
        		xml.add(new SimpleXml( "maxmemory", "1" ));
        		xml.add(new SimpleXml( "uptime", "0" ));
        		xml.add(new SimpleXml( "longestpagetime" ));
        		xml.add(new SimpleXml( "totalquerytime" ));
        		xml.add(new SimpleXml( "totalquerycount" ));
        		xml.add(new SimpleXml( "longestquerytime" ));
        		xml.add(new SimpleXml( "longestqueryrows" ));
        		xml.add(new SimpleXml( "requesturi" ));
    			ret.add(xml);             	
            }
        }
        
        return ret;
    }

	public JSONArray doGetJson(LinkedList<ServerEntry> dashboardServersList, boolean showProblemsOnly) throws JSONException {
        LinkedList<DashboardFetcher> fetchers = new LinkedList<DashboardFetcher>();
        
        if( dashboardServersList == null ) {
        	throw new JSONException("No entries in the dashboardServers configuration parameter.");
        }
        
        for(ServerEntry curServer: dashboardServersList) {
            fetchers.addLast(DashboardFetcher.getDashboardFetcher(curServer, "/dashboard/json", sf.getHttpPasswordB64Token()));
        }
        
        JSONArray ret = new JSONArray();
        for(DashboardFetcher df : fetchers) {
            df.join();
            if(df.isResultOK()) {
            	String resultString = df.getResult();
            	if(resultString.startsWith(JsonRequestHandler.JSON_HEADER)) {
            		resultString = resultString.substring(JsonRequestHandler.JSON_HEADER.length());
            	}
            	JSONObject result = new JSONObject(resultString);
            	if(showProblemsOnly) {
            		// does this work?? evidence says, "no"
            		if(!"ok".equalsIgnoreCase(result.getString("status"))) {
            			ret.add(result);
            		}
            	}
        		else {
            		ret.add(result);
            	}
            }
            else {
            	/*
            	 * {"maxcurrentrequests":10,"qps":0,"avgpagetime":0,"status":"ok","slowqrythreshold":100,"uptime":"15:09:43.5","pps":0
            	 * ,"browserurl":"http://Daryl-HP:8999/","numcurrentrequests":0,"slowpagethreshold":1000,"memory":{"total":329,"used":68}
            	 * ,"name":"Test","statuscolor":"","avgqrytime":0}
            	 */
            	ServerEntry entry = df.getServerEntry();
            	JSONObject o = new JSONObject();
            	o.put("maxcurrentrequests", -1);
            	o.put("qps", -1);
            	o.put("avgpagetime", -1);
            	o.put("status", "UNKNOWN");
            	o.put("uptime", "0:00:00");
            	o.put("pps", -1);
            	o.put("browserurl", entry.getUrl());
            	o.put("numcurrentrequests", -1);
            	o.put("slowpagethreshold", -1);
            	o.put("memory", new JSONObject("{\"total\":\"0\",\"used\":\"0\"}"));
            	o.put("name", entry.getName());
            	o.put("statuscolor", "red");
            	o.put("avgquerytime", -1);
            	ret.add(o);
            }
        }
        
        return ret;
	}
    
    
}
