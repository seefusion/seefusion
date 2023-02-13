package com.seefusion;

public class ResponseCacheItem {

	private long cacheExpiresTime = 0;
	private String cachedResponse = null;
	
	boolean isExpired() {
		return System.currentTimeMillis() > cacheExpiresTime;
	}
	void setCachedResponse(String s, long durationMs) {
		this.cachedResponse = s;
		this.cacheExpiresTime = System.currentTimeMillis() + durationMs;
	}
	String getCachedResponse() {
		return cachedResponse;
	}

}
