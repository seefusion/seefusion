package com.seefusion;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class IPList implements Iterable<String> {
	
	LinkedList<String> ipList;
	
	public IPList(String list) {
		StringTokenizer st = new StringTokenizer(list, SeeFusion.DELIM_LIST);
		ipList = new LinkedList<String>();
		while (st.hasMoreTokens()) {
			ipList.add(st.nextToken());
		}
	}
	
	public IPList() {
		ipList = new LinkedList<String>();
	}

	boolean contains(String ip) {
		for(String thisIP : ipList) {
			if (thisIP.endsWith(".")) {
				if (ip.length() > thisIP.length()
					&& ip.charAt(thisIP.length() - 1) == '.'
					&& ip.substring(0, thisIP.length()).equalsIgnoreCase(thisIP)) {
					return true;
				}
			}
			else {
				if (thisIP.equalsIgnoreCase(ip)) {
					return true;
				}
			}
		}

		return false;
	}

	public String toString() {
		StringBuilder ret = new StringBuilder();
		String delim = "";
		for(String entry : ipList) {
			ret.append(delim).append(entry);
			delim = ",";
		}
		return ret.toString();
	}

	@Override
	public Iterator<String> iterator() {
		return ipList.iterator();
	}

	boolean add(String ip) {
		return ipList.add(ip);
	}

	public JSONArray getJsonArray() {
		JSONArray ret = new JSONArray();
		for (String ip : this.ipList) {
			ret.put(ip);
		}
		return ret;
	}

	public boolean remove(String ip) {
		return ipList.remove(ip);
	}

}
