/*
 * SeeFusionKillSqlException.java
 *
 * Created on February 12, 2005, 11:03 PM
 */

package com.seefusion;

/**
 *
 * @author Daryl
 */
public class SeeFusionKillSQLException extends java.sql.SQLException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -5922392554420726755L;

	/** Creates a new instance of SeeFusionKillSqlException */
    public SeeFusionKillSQLException() {
        super("Request administratively terminated via SeeFusion");
    }
    
    /** Creates a new instance of SeeFusionError */
    public SeeFusionKillSQLException(String s) {
        super(s);
    }
    
}
