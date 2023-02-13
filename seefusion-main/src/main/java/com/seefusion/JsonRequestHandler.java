/**
 * 
 */
package com.seefusion;

import java.util.HashMap;

/**
 * @author Daryl
 *
 */
abstract class JsonRequestHandler extends HttpRequestHandler {

	static final String JSON_HEADER = ")]}',\n";
	
	private static HashMap<Class<? extends JsonRequestHandler>, ResponseCacheItem> JSONObjectCache = new HashMap<Class<? extends JsonRequestHandler>, ResponseCacheItem>(); 

	@Override
	final String doGet(HttpTalker talker) {
		talker.addHTTPHeader("Content-Type", "application/json");
	    if(this instanceof Cacheable) {
	    	Cacheable cacheable = (Cacheable)this;
	    	ResponseCacheItem cacheItem;
	    	if(JSONObjectCache.containsKey(this.getClass())) {
	    		cacheItem = JSONObjectCache.get(this.getClass());
	    	}
	    	else {
	    		cacheItem = new ResponseCacheItem();
	    		JSONObjectCache.put(this.getClass(), cacheItem);
	    	}
	    	if(cacheItem.isExpired()) {
	    		synchronized(this) {
	    			// double-checked locking.  So sue me, it's just a cache
	    			if(cacheItem.isExpired()) {
	    				String json = doJson(talker).toString();
		    			cacheItem.setCachedResponse(JSON_HEADER + json, cacheable.getCacheableDurationMs());
	    			}
	    		}
	    	}
	    	String response = cacheItem.getCachedResponse();
	    	return response;
	    }
	    JSONObject ret;
		try {
			ret = doJson(talker);
		} catch (ErrorMessage e) {
			ret = new JSONObject();
			ret.put("error", e.getMessage());
		}
		// JSON_HEADER is the angularjs anti-xsrf header 
	    return ret == null ? null : JSON_HEADER + ret.toString();
	}
	
	abstract JSONObject doJson(HttpTalker talker) ; 
	
}
