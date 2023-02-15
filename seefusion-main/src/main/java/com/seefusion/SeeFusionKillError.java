/*
 * SeeFusionError.java
 *
 */

package com.seefusion;

/**
 *
 * @author Daryl
 */
public class SeeFusionKillError extends java.lang.Error {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -3187896407465729337L;


	public SeeFusionKillError() {
        super("Request administratively terminated via SeeFusion");
    }
    
    
    /** Creates a new instance of SeeFusionError */
    public SeeFusionKillError(String s) {
        super(s);
    }
    
}
