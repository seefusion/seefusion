package com.seefusion;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.logging.Logger;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class SSLSocketListener extends SocketListener {

	private static final Logger LOG = Logger.getLogger(SSLSocketListener.class.getName());
	
	SSLSocketListener(SeeFusion sf, String addr) {
		super(sf, addr);
	}

	@Override
    ServerSocket createServerSocket() throws IOException {
    	SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        if( ip.equalsIgnoreCase("all") || ip.equals("*")) {
            LOG.info( "SSL Listening on all IPs, port:" +  port );
            return (SSLServerSocket) sslserversocketfactory.createServerSocket(port, 10);
        } else {
            LOG.info( "SSL Listening on ip " + ip + ":" +  port );
            InetAddress inetAddress = InetAddress.getByName(ip);
            return (SSLServerSocket) sslserversocketfactory.createServerSocket(port, 10, inetAddress);
        }
    }
    
}
