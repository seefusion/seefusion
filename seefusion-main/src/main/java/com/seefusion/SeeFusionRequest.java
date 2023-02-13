/*
 * SeeFusionRequest.java
 *
 */

package com.seefusion;

/**
 * Represents a request tracked by SeeFusion.
 */
public class SeeFusionRequest {
    
    RequestInfo pi;
    SeeFusion sf;
    
    /** Creates a new instance of SeeFusionRequest */
    SeeFusionRequest(SeeFusion sf, RequestInfo pi) {
        this.sf = sf;
        this.pi = pi;
    }
    
    /**
     * Turns debug capture on or off.
     * @param onoff If true, debug capture is enabled.  If false, debug capture is paused.
     */
    public void setDebugState(boolean onoff) {
        pi.setDebugPage(onoff);
    }
    
    /**
     * Reports whether debug capture is on or off.
     * @return true if debug is on for this page.
     */
    public boolean getDebugState() {
        return pi.isDebugPage();
    }

    /**
     * Returns the debug output captured from this request (so far).  Does not reset the buffer.
     * @return String containing HTML-formatted debug output.
     */
    public String getDebugOutput() {
        return pi.getRawDebugOutput();
    }
    
    /**
     * Finds the SeeFusionRequest active for this thread.
     * @return The SeeFusionRequest for this thread, or null if no active request exists for this thread.
     */
    public static SeeFusionRequest getThisRequest() {
        RequestInfo thisPi = RequestInfo.getCurrent();
        if(thisPi==null) {
            return null;
        } else {
            return new SeeFusionRequest(thisPi.getSeeFusion(), thisPi);
        }
    }
    
    /**
     * Adds tracing information about this request to debug output and/or the "Completed" column of the server monitoring page.
     * @param message Message describing this trace event.
     */
    public String trace(String message) {
    	DebugMessage dbm = pi.trace(message);
    	if(dbm == null) {
    		return null;
    	} else {
    		return dbm.toString();
    	}
    }
    
    /**
     * Adds tracing information about an exception.
     * @param message Message describing this trace event.
     */
    public String traceException(Throwable t) {
    	DebugMessage dbm = pi.traceException(t);
    	if(dbm == null) {
    		return null;
    	} else {
    		return dbm.toString();
    	}
    }
    
    /**
     * Marks a request as completed.  This method MUST be called at the end of a request created with SeeFusion.createRequest().
     *   (The HTTP filter calls this automatically.)
     */
    public void close() {
        pi.close();
    }
    
    /**
     * Returns true if close() has not been called on this request.
     * @return returns true if close() has not been called on this request.
     */
    public boolean isActive() {
        return pi.isActive();
    }
    
    /* public Action createAction(String actionName) {
        return new ActionInfo(actionName, sf.getDebugStackTargets(), true);
    } */
    
    /**
     * Returns the number of currently active requests seen by the current SeeFusion instance.
     * @return number of active requests.
     */
    public int getActiveRequestCount() {
    	return sf.getMasterRequestList().getCurrentRequestCount();
    }
    
    /**
     * Allows an app to set its request name when the URI isn't helpful (eg FuseBox / MachII apps)
     */
    public void setRequestName(String name) {
    	if(!name.startsWith("/")) {
    		pi.setRequestURI("/" + name);
    	} else {
    		pi.setRequestURI(name);
    	}
    	pi.setPathInfo(null);
    }
    
}
