/*
 * Config.java
 *
 */

package com.seefusion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config implements Observer<FileWatcher>, Subject<Config> {

	private static final Logger LOG = Logger.getLogger(Config.class.getName());
	
	boolean isConfigBackedByFile = false;
	
	File configFile;

	SimpleXml config;

	private String configDir;

	private FileWatcher fw;

	boolean isConfigBackedByFile() {
		return isConfigBackedByFile;
	}

	Config() {
		config = new SimpleXml("config");
	}

	Config(String configDir) {
		File dir = new File(configDir);
		if(dir.isFile()) {
			dir = dir.getParentFile();
		}
		else if (!dir.exists()) {
			dir.mkdirs();
		}
		this.configDir = dir.getAbsolutePath();
		read();
	}

	void read() {
		configFile = new File(configDir + "/seefusion.xml");
		if (configFile.exists()) {
			try {
				config = SimpleXmlParser.parseXml(Util.readFile(configFile));
				if (config == null) {
					config = new SimpleXml("config");
					write();
				}
			}
			catch (IOException e) {
				LOG.log(Level.SEVERE, "Unable to read" + configFile.getAbsolutePath(), e);
			}
			isConfigBackedByFile = true;
		}
		else {
			// attempt to import properties
			File f = OldPropsConfig.findPropertiesFile("seefusion", configDir);
			if(f != null) {
				try {
					Properties props = new Properties();
					FileInputStream in = new FileInputStream(configFile);
					props.load(in);
					in.close();
					for (Map.Entry<Object, Object> entry : props.entrySet()) {
						this.setProperty(entry.getKey().toString(), entry.getValue());
					}
					LOG.info("Imported existsing properties from " + f.getAbsolutePath());
				}
				catch (IOException e) {
					LOG.warning(
							"Unable to import existing properties from " + f.getAbsolutePath() + ": " + e.getMessage());
				}
			}
			// create new xml file
			config = new SimpleXml("config");
			try {
				configFile.createNewFile();
				isConfigBackedByFile = true;
			}
			catch (IOException e) {
				LOG.log(Level.SEVERE, "Unable to create " + configFile.getAbsolutePath(), e);
			}
		}
		if (isConfigBackedByFile) {
			try {
				fw = new FileWatcher(configFile, this, 30000);
			}
			catch (IOException e) {
				LOG.log(Level.SEVERE, "Unable to watch for changes to " + configFile.getAbsolutePath(), e);
			}
		}
	}

	void write() {
		try {
			// fw == null during unit tests
			if(fw != null) {
				Util.writeFile(configFile, config.toString());
				fw.resetLastModified();
			}
		}
		catch (IOException e) {
			LOG.log(Level.SEVERE, "Unable to write config file", e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		if (fw != null) {
			fw.shutdown();
		}
		super.finalize();
	}

	// Implement Observer
	@Override
	public void update(FileWatcher fw) {
		// read config
		read();
		// cascade notify
		subject.notifyObservers(this);
	}

	// Implement Subject
	SubjectImpl<Config> subject = new SubjectImpl<Config>();

	@Override
	public void addObserver(Observer<Config> o) {
		subject.addObserver(o);
	}

	@Override
	public void removeObserver(Observer<Config> o) {
		subject.removeObserver(o);
	}

	static void log(String s) {
		LOG.info(s);
	}

	public String getProperty(String name, String dfault) {
		String ret = config.getProperty(name);
		if (ret == null) ret = dfault;
		return ret;
	}

	public String getProperty(String name) {
		return config.getProperty(name);
	}

	public void setProperty(String name, Object value) {
		Object currentValue = config.getProperty(name);
		if((currentValue==null && value !=  null) || !currentValue.equals(value)) {
			if(value==null) {
				config.remove(name);
			}
			else {
				config.setProperty(name, value);
			}
			write();
		}
	}

	public SimpleXml getXml() {
		return config;
	}

	public String getFilePath() {
		if (configFile == null || !configFile.exists()) {
			return "(unable to create " + configDir + "/seefusion.xml)";
		}
		else {
			return configFile.getAbsolutePath();
		}
	}

	public String getConfigDir() {
	    return configDir;
    }

	public void setXML(SimpleXml xml) {
		config.remove(xml.getTagName());
		config.add(xml);
	}

	public void loadEnvironmentOverrides() {
		System.getenv().forEach((k,v) -> {
			if(k.startsWith("SF_") && !"SF_".equals(k)) {
				String key = k.substring(3);
				LOG.log(Level.INFO, "Set value of " + key + " from environment");
				config.setProperty(key, v);
			}
		});
	}

}
