/*
 * SeeFusionConnector.java
 *
 */

package com.seefusion;

/**
 * This class is designed as a helper for those wanting to implement SeeFusion in an application that's not Servlet.Filter based.
 */
public class SeeFusionConnector {
    
    SeeFusion sf;
    
    /**
     * This constructor exists solely to allow {@link #trace} to be called from any class in the same thread that called {@link #createRequest}.
     * Use {@link #getInstance(java.lang.String)} to actually start a SeeFusion instance.
     */
    public SeeFusionConnector() {
    }
    
    /**
     * Creates a new instance of SeeFusionConnector
     * @param instanceName Name of SeeFusion instance.  If this method called several times with the same instanceName,
     * you will always get the same instance of SeeFusion.
     * @see #shutdown()
     */
    //private SeeFusionConnector(String instanceName) {
    //    this.sf = SeeFusion.getInstance(instanceName);
    //}
    private SeeFusionConnector(String configDir) {
        this.sf = SeeFusion.getInstance(configDir);
    }
    
    public String getInstanceName() { 
        return this.sf.getInstanceName();
    }
    
    /**
     * Creates a new instance of SeeFusionConnector
     * @param instanceName Name of SeeFusion instance.  If this method called several times with the same instanceName,
     * you will always get the same instance of SeeFusion.
     * @see #shutdown
     */
    public static SeeFusionConnector getInstance(String configDir) {
        return new SeeFusionConnector(configDir);
    }

    /**
     * Trace messages appear in the "Completed" column of the server's Active Requests.
     * Trace messages will also appear in debug output.
     * @param message Message to display.
     */
    public void trace(String message) {
        SeeFusion.trace(message);
    }
    
    /**
     * Use this method to create a monitored request.  It should be called once on entry; {@link com.seefusion.SeeFusionRequest#close SeeFusionRequest.close()} <i>must</i> be called at the
     * end of the request (usually in a finally{} block), or the request will appear active indefinitely.
     * <br><br>
     * If the {@link SeeFusionRequest} object returned from this method is no longer available at the end of the request, call {@link SeeFusionRequest#getThisRequest SeeFusionRequest.getThisRequest().close()}
     *
     * @param serverName The name of the server being accessed (ie, the host header).
     * @param requestURI The name of the resource being requested.
     * @param queryString The parameters passed to the resource being requested.
     * @param remoteIP The IP address of the requestor.
     * @see com.seefusion.SeeFusionRequest#close SeeFusionRequest.close()
     * @see com.seefusion.SeeFusionRequest#getThisRequest SeeFusionRequest.getThisRequest()
     */
    public SeeFusionRequest createRequest(String serverName, String requestURI, String queryString, String remoteIP, String method, String pathInfo, boolean isSecure) {
        return new SeeFusionRequest(sf, sf.createRequest(serverName, requestURI, queryString, remoteIP, method, pathInfo, isSecure));
    }
    
    /**
     * Shuts down this SeeFusion instance (closes all listeners and loggers.)  Must be called as many times as getInstance() was.
     * Once the instance has been shut down, another call to getInstance() will restart it.
     * @see #getInstance
     */
    public void shutdown() {
        sf.shutdown();
    }
    
}
