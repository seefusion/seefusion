/*
 * ServerEntry.java
 *
 * Created on August 22, 2004, 7:43 PM
 */

package com.seefusion;

import java.net.InetAddress;
/**
 *
 * @author  TheArchitect
 */
class ServerEntry {
    
    String name;
    InetAddress inetAddress;
    int port;
    
    ServerEntry( String name, InetAddress inetAddress, int port ) {
        this.name = name;
        this.inetAddress = inetAddress;
        this.port = port;
    }
    
    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public java.lang.String getName() {
        return name;
    }
    
    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    /**
     * Getter for property inetAddress.
     * @return Value of property inetAddress.
     */
    public InetAddress getInetAddress() {
        return inetAddress;
    }
    
    /**
     * Setter for property inetAddress.
     * @param inetAddress New value of property inetAddress.
     */
    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }
    
    /**
     * Getter for property port.
     * @return Value of property port.
     */
    public int getPort() {
        return port;
    }
    
    /**
     * Setter for property port.
     * @param port New value of property port.
     */
    public void setPort(int port) {
        this.port = port;
    }

	public String getUrl() {
		return "http://"+name+":"+port+"/";
	}
    
}
