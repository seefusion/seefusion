package com.seefusion;

import java.util.logging.Logger;

import org.apache.catalina.Container;
import org.apache.catalina.ContainerEvent;
import org.apache.catalina.ContainerListener;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

/**
 * Injects SeeFusion at the start of the servlet FilterChain
 * @author Daryl
 *
 */
public class Tomcat8Valve implements ContainerListener {

	private static final Logger LOG = Logger.getLogger(Tomcat8Valve.class.getName()); 
	
    FilterDef filterDef;
    FilterMap filterMap;
    
	public Tomcat8Valve(Container con) {
		LOG.info("SeeFusion Tomcat8+ Valve Started");
		filterDef = new FilterDef();
		filterDef.setFilterClass("com.seefusion.Filter");
		filterDef.setFilterName("SeeFusion");
		
		filterMap = new FilterMap();
		filterMap.addURLPattern("/*");
		filterMap.setFilterName("SeeFusion");

		addFilterToContainer(con);
		con.addContainerListener(this);
	}

	@Override
	public void containerEvent(ContainerEvent evt) {
		//System.out.println(String.format("****Container Event: %s: %s", evt.getType(), evt.getData()));
		if(evt.getType().equalsIgnoreCase("removeValve") || evt.getType().equalsIgnoreCase("removeChild")) {
			//nop
		} else {
			addFilterToContainer(evt.getContainer());
		}
	}
	
	private void addFilterToContainer(Container con) {
		if(con instanceof StandardContext) {
			StandardContext cx = (StandardContext)con;
			LOG.fine(String.format("Checking app %s", cx.getDisplayName()));
			if(cx.findFilterDef("SeeFusion") == null) {
				LOG.fine(String.format("Adding SeeFusion to app \"%s\"", cx.getDisplayName()));
				cx.addFilterDef(filterDef);
				cx.addFilterMapBefore(filterMap);
				cx.filterStop();
				cx.filterStart();
			}
		}
		else {
			for(Container child : con.findChildren()) {
				if(!hasMeListening(con.findContainerListeners())){
					LOG.fine(String.format("Adding SeeFusion to container %s", child.getName()));
					con.addContainerListener(this);
				}
				addFilterToContainer(child);
			}
		}
	}

	private boolean hasMeListening(ContainerListener[] containerListeners) {
		for(ContainerListener listener : containerListeners) {
			if(listener == this) {
				return true;
			}
		}
		return false;
	}

}
