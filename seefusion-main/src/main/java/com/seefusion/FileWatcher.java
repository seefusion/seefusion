/*
 * FileWatcher
 * 
 * Will occasionally (per intervalMs) check to see if a specified file on disk is updated,
 * and notify() any registered observers
 *
 */

package com.seefusion;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 * @author Daryl
 */
class FileWatcher extends SeeTask implements Subject<FileWatcher> {
	
	private static final Logger LOG = Logger.getLogger(FileWatcher.class.getName());
	
    boolean isStarted=false;
    long fileLastModified;
    File file;
    PooledThread thread;
    long intervalMs;
    Properties p = new Properties();
    
	FileWatcher(File file, Observer<FileWatcher> o, long intervalMs) throws IOException {
        setFile(file);
        resetLastModified();
        addObserver(o);
        this.intervalMs = intervalMs;
    }
    
    void start() {
        if(!isStarted) {
            isStarted=true;
            thread = ThreadPool.start(this);
        }
    }
    
    @Override
    void shutdown() {
        isStarted=false;
        synchronized(this) {
            this.notify();
        }
    }
    
    void checkFileNow() {
        synchronized(this) {
            this.notify();
        }
    }
    
    void setFile(File file) throws IOException {
        if(file == null) {
            throw new IllegalArgumentException("File can not be null.");
        } else {
            if(!file.exists()) {
                throw new IOException("File does not exist: " + file.getAbsolutePath());
            } else if (!file.canRead()) {
                throw new IOException("Cannot read file (permissions issue?): " + file.getAbsolutePath());
            } else {
                this.file = file;
            }
        }
    }
    
    void checkForUpdate() {
        if( file.lastModified() != fileLastModified ) {
        	LOG.info(file.getName() + " modified; reloading");
        	notifyObservers();
        }
    }
    
    void resetLastModified() {
    	this.fileLastModified = file.lastModified();
    }
    
    // Implement Runnable
    @Override
	public void run() {
        while(file.canRead() && isStarted) {
            checkForUpdate();
            try {
                synchronized(this) {
                    this.wait(intervalMs);
                }
            } catch(InterruptedException e) {
                //ignore
            }
        }
    }
    
    // Implement Subject
	SubjectImpl<FileWatcher> subject = new SubjectImpl<FileWatcher>();
    @Override
	public void addObserver(Observer<FileWatcher> o) {
    	subject.addObserver(o);
        if(!isStarted) {
            start();
        }
    }
    @Override
	public void removeObserver(Observer<FileWatcher> o) {
        subject.removeObserver(o);
        if(!subject.hasObservers()) {
            shutdown();
        }
    }

	public void notifyObservers() {
        resetLastModified();
    	subject.notifyObservers(this);
    }
    
	@Override
	public String getThreadName() {
	    return "File Watcher - " + file.getName();
    }
}
