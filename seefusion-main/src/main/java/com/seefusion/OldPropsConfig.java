/*
 * Config.java
 *
 */

package com.seefusion;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Daryl
 */
class OldPropsConfig extends Properties implements Observer<PropertiesFile> {

	private static final Logger LOG = Logger.getLogger(OldPropsConfig.class.getName());
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -2920125127630998310L;
	
	Properties props = null;
    boolean isConfigBackedByFile = false;
    File propertiesFile;
    
    boolean isConfigBackedByFile() {
    	return isConfigBackedByFile;
    }

    
    /** Creates a new instance of Config */
    OldPropsConfig(Properties defaultProps) {
        construct(defaultProps, null, null);
    }
    OldPropsConfig(Properties defaultProps, String instanceName, String propsDir) {
        construct(defaultProps, instanceName, propsDir);
    }
    
    void construct(Properties props, String instanceName, String propsDir) {
        File f = findPropertiesFile(instanceName, propsDir);
        if(f==null) {
            this.props = props;
        } else {
            try {
                this.props = new PropertiesFile(f, props, this);
                //pf.list(System.out);
                isConfigBackedByFile=true;
            } catch(IOException e) {
                LOG.log(Level.WARNING, "Unable to read properties file: ", e);
                this.props = props;
            }
        }
        if(this.props==null) {
            this.props=new Properties();
        }
    }
    
    static java.util.HashSet<String> foundPropertiesFiles = new java.util.HashSet<String>();
    static File findPropertiesFile(String instanceName, String propsDir) {
        //log("findPropertiesFile");
        //new Throwable().printStackTrace();
        // try to locate properties file for this instance
        String propsLoc=null;
        java.net.URL propsLocURL=null;
        if(instanceName!=null && !instanceName.equalsIgnoreCase("seefusion")) {
            propsLocURL = OldPropsConfig.class.getResource("/seefusion." + instanceName + ".properties");
        }
        if(propsLocURL==null) {
            propsLocURL = OldPropsConfig.class.getResource("/seefusion.properties");
            if(propsLocURL==null) {
                // try relative to server root
            	File fPropsDir = new File(propsDir);
            	if(fPropsDir.exists()) {
            		File propsFile = new File(fPropsDir, "seefusion.properties");
            		if(!propsFile.exists()) {
            			try {
							propsFile.createNewFile();
						} catch (IOException e) {
							LOG.log(Level.WARNING, "Unable to create seefusion.properties in " + fPropsDir.getPath(), e);
						}
            		}
            		propsLoc = propsFile.getPath();
            		
            	}
            	else {
            		LOG.log(Level.WARNING, "./WEB-INF/ does not exist(?!)" + new File(".").getAbsolutePath());
            	}
            } else {
                propsLoc = propsLocURL.getPath();
            }
        } else {
            propsLoc = propsLocURL.getPath();
        }
        
        File f = null;
        if(propsLoc == null) {
            if(instanceName == null) {
            	LOG.log(Level.WARNING, "Cannot locate seefusion.properties.");
            } else {
            	LOG.log(Level.WARNING, "Cannot load seefusion." + instanceName + ".properties or seefusion.properties from the classpath, nor create ./WEB-INF/seefusion.properties.");
            }
        } else {
            try{
                f = new File(propsLoc);
                String path = f.getPath();
                if(!foundPropertiesFiles.contains(path)) {
                    foundPropertiesFiles.add(path);
                    LOG.info("Found properties file at "+path);
                }
            } catch(Exception e) {
            	LOG.log(Level.WARNING, "Error reading properties from file "+f.getPath(), e);
            }
        }
        //log("findPropertiesFile Returning " + f.toString());
        return f;
    }
    
    String getConfigFilePath() {
        if(propertiesFile != null) {
            return propertiesFile.getAbsolutePath();
        } else {
            return null;
        }
    }
    
    // Override methods of Properties
    @Override
	public String getProperty(String key) {
        return props.getProperty(key);
    }
    
    @Override
	public Object setProperty(String key, String value) {
        return props.setProperty(key, value);
    }
    
    public Object remove(String key) {
        return props.remove(key);
    }
    
    @Override
	public boolean isEmpty() {
        return props.isEmpty();
    }
    
    @Override
	public java.util.Enumeration<Object> keys() {
        return props.keys();
    }
    
    @Override
	public String toString() {
        StringBuilder sb = new StringBuilder(props.size() * 100);
        sb.append('{');
        String comma="";
        for(Map.Entry<Object, Object> e : props.entrySet()) {
            sb.append(comma).append(e.getKey()).append('=').append(e.getValue());
            comma=";";
        }
        sb.append('}');
        return sb.toString();
    }

	@Override
	public void update(PropertiesFile object) {
		// noop
	}
    

}
