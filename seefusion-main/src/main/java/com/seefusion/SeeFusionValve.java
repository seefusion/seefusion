package com.seefusion;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.valves.ValveBase;

/**
 * Injects SeeFusion at the start of the servlet FilterChain
 * @author Daryl
 *
 */
public class SeeFusionValve extends ValveBase implements Lifecycle, ContainerListener {

	private static final Logger LOG = Logger.getLogger(SeeFusionValve.class.getName());
	
	private ContainerListener valve;
	
	public SeeFusionValve() {
	}
	
	@Override
	public void initInternal() throws LifecycleException {
		String version = ServerInfo.getServerNumber();
		if(version.startsWith("7")) {
			LOG.info("SeeFusionValve: Loading Tomcat7 Valve for Tomcat " + version);
			this.valve = new Tomcat7Valve(getContainer());
		}
		else {
			LOG.info("SeeFusionValve: Loading Tomcat8+ Valve for Tomcat " + version);
			this.valve = new Tomcat8Valve(getContainer());
		}
		super.initInternal();
	}

	@Override
	public void containerEvent(ContainerEvent evt) {
		valve.containerEvent(evt);
	}
	
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		getNext().invoke(request, response);
	}

}
