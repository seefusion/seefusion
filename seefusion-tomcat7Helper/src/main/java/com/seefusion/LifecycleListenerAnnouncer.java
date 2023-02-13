package com.seefusion;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

public class LifecycleListenerAnnouncer implements LifecycleListener {

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    String now() {
        return df.format(new Date());
    }
    
	private void log(String s) {
		System.out.println(now() + " SeeFusion ListenerAnnouncer: " + s);
	}

	@Override
	public void lifecycleEvent(LifecycleEvent arg0) {
		log("ListenerEvent: " + arg0.getType());
		log("toString: " + arg0.toString());
		log("data: " + arg0.getClass().getSimpleName() + " -> "+ arg0.getData());
		log("lifecycle: " + arg0.getLifecycle().getStateName());
	}
}
