package com.seefusion;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;


public class SeeFusionHandler extends Handler {

	private static LinkedList<String> log = new LinkedList<String>();

	
    private static HashSet<StringBuilder> captureBuffers = new HashSet<StringBuilder>();

	public SeeFusionHandler() {
		Formatter formatter = new SimpleFormatter();
		setFormatter(formatter);
	}

	@Override
	public void publish(LogRecord record) {
		String msg = getFormatter().format(record);
    	synchronized(captureBuffers) {
            for(StringBuilder curbuf : captureBuffers) {
                curbuf.append(msg);
            }
    	}
		log.addLast(msg);
		if(log.size() > 1000) {
			log.removeFirst();
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
		log.clear();
	}
	
    static synchronized String getLog() {
    	StringBuilder buf = new StringBuilder(3000);
    	String curItem;
		for(Iterator<String> iter = log.iterator(); iter.hasNext();  ) {
			curItem = iter.next();
			buf.append(curItem).append("\r\n");
		}
    	return buf.toString();
	}

    static void startCapture(StringBuilder buf) {
    	synchronized(captureBuffers) {
    		captureBuffers.add(buf);
    	}
    }
    
    static void stopCapture(StringBuilder buf) {
    	synchronized(captureBuffers) {
    		captureBuffers.remove(buf);
        }
    }
    
}
