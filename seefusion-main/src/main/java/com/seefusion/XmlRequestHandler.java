/**
 * 
 */
package com.seefusion;

import java.util.HashMap;

/**
 * @author Daryl
 *
 */
abstract class XmlRequestHandler extends HttpRequestHandler {

	private static HashMap<Class<? extends XmlRequestHandler>, ResponseCacheItem> ObjectCache = new HashMap<Class<? extends XmlRequestHandler>, ResponseCacheItem>(); 

	private static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n";
	
	@Override
	final String doGet(HttpTalker talker) {
		talker.addHTTPHeader("Content-Type", "application/xml");
	    if(this instanceof Cacheable) {
	    	Cacheable cacheable = (Cacheable)this;
	    	ResponseCacheItem cacheItem;
	    	if(ObjectCache.containsKey(this.getClass())) {
	    		cacheItem = ObjectCache.get(this.getClass());
	    	}
	    	else {
	    		cacheItem = new ResponseCacheItem();
	    		ObjectCache.put(this.getClass(), cacheItem);
	    	}
	    	if(cacheItem.isExpired()) {
	    		synchronized(this) {
	    			// double-checked locking.  So sue me, it's just a cache
	    			if(cacheItem.isExpired()) {
	    				String content = XML_HEADER + doXml(talker).toString();
		    			cacheItem.setCachedResponse(content, cacheable.getCacheableDurationMs());
	    			}
	    		}
	    	}
	    	return cacheItem.getCachedResponse();
	    }
	    SimpleXml ret;
		try {
			ret = doXml(talker);
		} catch (ErrorMessage e) {
			ret = new SimpleXml("error");
			ret.setCDATA(e.getMessage());
		}
		String content = ret == null ? null : XML_HEADER + ret.toString();
	    return content;

	}
	
	abstract SimpleXml doXml(HttpTalker talker) ; 
	
}
