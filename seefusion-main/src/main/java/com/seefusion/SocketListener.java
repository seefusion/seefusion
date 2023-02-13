/*
 * HttpListener.java
 *
 * Created on August 22, 2004, 7:39 PM
 */

package com.seefusion;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author  TheArchitect
 */
class SocketListener extends SeeTask {
	
	private static final Logger LOG = Logger.getLogger(SocketListener.class.getName());
    
    SeeFusion sf;
    ServerSocket serverSocket;
    PooledThread thread;
    String ip;
    int port;
    boolean running = true;
        
    void destroy() {
        shutdown();
    }
    
    void shutdown() {
        running = false;
        try {
            if(serverSocket != null) {
                serverSocket.close();
                thread.join();
            }
        } catch(IOException e) {
        	LOG.log(Level.WARNING, "Exception on socket", e);
        } catch(InterruptedException e) {
        	LOG.log(Level.WARNING, "Exception on socket", e);
        }
    }
    
    SocketListener(SeeFusion sf, String addr) {
        //log("HttpListener.initListener("+sf.getInstanceName()+":"+ sf.toString() + ")");
        this.sf = sf;
        int colonPos;
        
        try {
            if( (colonPos = addr.indexOf(':')) != -1 ) {
                ip = addr.substring(0, colonPos);
                port = Integer.parseInt(addr.substring(colonPos+1, addr.length()));
            } else {
                ip = addr;
                port = SeeFusion.DEFAULT_PORT;
            }
            serverSocket = createServerSocket();
            // ServerSocket bound, start thread
            thread = ThreadPool.start(this);
        } catch(UnknownHostException e) {
            LOG.log(Level.WARNING, "Unable to start listener on ip "+ip, e);
        } catch(IOException e) {
            LOG.log(Level.WARNING, "Unable to start listener on ip "+ip, e);
        }
    }
    
    ServerSocket createServerSocket() throws IOException {
        if( ip.equalsIgnoreCase("all") || ip.equals("*")) {
            LOG.info( "Listening on all IPs, port:" +  port );
            return new ServerSocket(port, 10);
        } else {
            LOG.info( "Listening on ip " + ip + ":" +  port );
            InetAddress inetAddress = InetAddress.getByName(ip);
            return new ServerSocket(port, 10, inetAddress);
        }
    }
    
    public String getThreadName() {
    	return "HTTP Listener, port " + port;
    }
    
    public void run() {
        Socket s;
        while(running) {
            try {
                s = serverSocket.accept();
                new HttpTalker(sf, s);
            } catch(IOException e) {
                if( running ) {
                    LOG.log(Level.WARNING, "Exception caught from listener on "+ip+":"+port, e);
                } else {
                    LOG.info("Shutting down listener on "+ip+":"+port);
                }
            }
        }
    }
        
}
