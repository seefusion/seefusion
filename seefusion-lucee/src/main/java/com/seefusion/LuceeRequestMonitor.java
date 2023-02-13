package com.seefusion;

import java.io.IOException;
import java.util.Map;

import lucee.runtime.PageContext;
import lucee.runtime.config.ConfigServer;
import lucee.runtime.config.ConfigWeb;
import lucee.runtime.exp.PageException;
import lucee.runtime.monitor.RequestMonitor;
import lucee.runtime.type.Query;

public class LuceeRequestMonitor implements RequestMonitor {

	private String name;
	private boolean logEnabled;

	@SuppressWarnings("rawtypes")
	@Override
	public Class getClazz() {
		return this.getClass();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public short getType() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init(ConfigServer configServer, String name, boolean logEnabled) {
		this.name = name;
		this.logEnabled = logEnabled;
	}

	@Override
	public boolean isLogEnabled() {
		return logEnabled;
	}

	@Override
	public Query getData(ConfigWeb configWeb, Map<String, Object> arg1) throws PageException {
		return null;
	}

	@Override
	public void log(PageContext pc, boolean isError) throws IOException {
		// called at end of request
		
	}
	
	public void init(PageContext pc) {
		RequestInfo ri = new RequestInfo();
		pc.getHttpServletRequest();
		pc.set
	}

}
