/*
 * PropertiesFile.java
 *
 */

package com.seefusion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Daryl
 */
class PropertiesFile extends Properties implements Subject<PropertiesFile>, Observer<FileWatcher> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3319678224070186673L;

	private static final Logger LOG = Logger.getLogger(PropertiesFile.class.getName());
	
    Properties defaults;
    FileWatcher watcher;
    File configFile;
    
    /** Creates a new instance of PropertiesFile */
    PropertiesFile(File file) throws IOException {
        super();
        configFile = file;
        loadFromFile();
        watcher = new FileWatcher(file, this, 10000);
    }
    
	PropertiesFile(File file, Properties defaults, Observer<PropertiesFile> o) throws IOException {
        super(defaults);
        configFile = file;
        loadFromFile();
        watcher = new FileWatcher(file, this, 10000);
		watcher.addObserver(this);
        addObserver(o);
    }
    
    void checkFileNow() {
        watcher.checkFileNow();
    }
    
    void loadFromFile() throws IOException {
        FileInputStream in = new FileInputStream(configFile);
        load(in);
        in.close();
    }
    void storeToFile() throws IOException {
        FileOutputStream out = new FileOutputStream(configFile);
        store(out, null);
        out.close();
    }
    
    void checkForUpdate() {
        watcher.checkForUpdate();
    }

    @Override
	public Object setProperty(String key, String value) {
        checkForUpdate();
        Object ret = super.setProperty(key, value);
        try {
            storeToFile();
        } catch(IOException e) {
        	LOG.log(Level.WARNING, "Unable to save updated properties", e);
        }
        return ret;
    }
    
    // Implement Subject
	SubjectImpl<PropertiesFile> subject = new SubjectImpl<PropertiesFile>();
    @Override
	public void addObserver(Observer<PropertiesFile> o) {
    	subject.addObserver(o);
    }
    @Override
	public void removeObserver(Observer<PropertiesFile> o) {
        subject.removeObserver(o);
    }
    
	/* (non-Javadoc)
	 * @see com.seefusion.Observer#update(com.seefusion.Subject)
	 */
	@Override
	public void update(FileWatcher f) {
        try {
			loadFromFile();
		}
		catch (IOException e) {
			LOG.log(Level.WARNING, "Unable to reload properties file " + configFile.getAbsolutePath(), e);
		}
		subject.notifyObservers(this);
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#remove(java.lang.Object)
	 */
	@Override
	public synchronized Object remove(Object key) {
    	Object o = super.remove(key);
    	try {
			storeToFile();
		}
		catch (IOException e) {
			LOG.log(Level.WARNING, "Unable to save updated properties", e);
		}
    	return o;
	}
}
