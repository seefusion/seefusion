package com.seefusion;

import java.util.HashMap;
import java.util.Map;

public class TimedIPList implements Runnable {
	private HashMap<String, Long> ipList;
	private Thread t;
	private boolean running = true;

	public TimedIPList() {
		ipList = new HashMap<String, Long>();
		t = new Thread(this);
		t.setName("SeeFusion Timed IP List Pruner");
		t.setDaemon(true);
		t.start();
	}

	@Override
	public void run() {
		while(running) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// whatev
			}
			HashMap<String, Long> temp;
			synchronized(ipList) {
				temp = new HashMap<String, Long>(ipList);
			}
			long now = System.currentTimeMillis();
			for(Map.Entry<String, Long> entry : temp.entrySet()) {
				if(entry.getValue() < now) {
					synchronized(ipList) {
						ipList.remove(entry.getKey());
					}
				}
			}
		}
	}
	
	void shutdown() {
		running = false;
		t.interrupt();
		try {
			t.wait();
		} catch (InterruptedException e) {
			// whatev
		}
	}

	public boolean contains(String ip) {
		synchronized(ipList) {
			return ipList.containsKey(ip);
		}
	}

	void add(String ip, Long expires) {
		ipList.put(ip, expires);
	}

	JSONArray getJsonArray() {
		JSONArray ret = new JSONArray();
		HashMap<String, Long> temp;
		synchronized(ipList) {
			temp = new HashMap<String, Long>(ipList);
		}
		for(Map.Entry<String, Long> entry : temp.entrySet()) {
			JSONObject item = new JSONObject();
			item.put("ip", entry.getKey());
			item.put("expires", entry.getValue());
			ret.add(item);
		}
		return ret;
	}

	public boolean remove(String ip) {
		synchronized(ipList) {
			return ipList.remove(ip) != null;
		}		
	}
	
}
