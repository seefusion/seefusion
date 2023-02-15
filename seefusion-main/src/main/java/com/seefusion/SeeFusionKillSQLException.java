/*
 * SeeFusionKillSqlException.java
 */

package com.seefusion;

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
