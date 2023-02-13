/*
 * PropertiesFilter.java
 *
 */

package com.seefusion;

import java.util.Map;
import java.util.Properties;

/**
 *
 */
class PropertiesFilterWrapper extends Properties {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 3371683785056894498L;
	Properties p;
    String propertiesPrefix="";
    
    
    /** Creates a new instance of PropertiesFilter */
    public PropertiesFilterWrapper(Properties p, String prefix) {
        this.p=p;
        setPropertiesPrefix(prefix);
    }
    
    
    void setPropertiesPrefix(String propertiesPrefix) {
        if(propertiesPrefix==null) {
            this.propertiesPrefix = "";
        } else {
            this.propertiesPrefix = propertiesPrefix;
        }
    }
    // Override get/setProperty
    public String getProperty(String key) {
        String ret;
        //log("Getting property " + propertiesPrefix + key + " = " + p.getProperty(propertiesPrefix + key));
        ret = p.getProperty(propertiesPrefix + key);
        if(ret==null) {
            ret = p.getProperty(key);
        }
        return ret;
    }
    public String getProperty(String key, String defaultValue) {
        String ret;
        //log("Getting property " + propertiesPrefix + key + " = " + p.getProperty(propertiesPrefix + key));
        ret = p.getProperty(propertiesPrefix + key, defaultValue);
        if(ret==null) {
            ret = p.getProperty(key, defaultValue);
        }
        return ret;
    }

    public Object setProperty(String key, String value) {
        Object ret = p.setProperty(propertiesPrefix + key, value);
        return ret;
    }
    
    public Object remove(String key) {
        Object ret = p.remove(propertiesPrefix + key);
        return ret;
    }
    
    public boolean isEmpty() {
        return p.isEmpty();
    }

    public void load(java.io.InputStream inStream) throws java.io.IOException {
        p.load(inStream);
    }

    public void list(java.io.PrintStream out) {
        p.list(out);
    }

    public void list(java.io.PrintWriter out) {
        p.list(out);
    }

    public void store(java.io.OutputStream out, String header) throws java.io.IOException {
        p.store(out, header);
    }

    @SuppressWarnings("deprecation")
	public void save(java.io.OutputStream out, String header) {
        p.save(out, header);
    }

    public java.util.Enumeration<?> propertyNames() {
        java.util.Enumeration<?> retValue;
        
        retValue = p.propertyNames();
        return retValue;
    }

    public Object remove(Object key) {
        Object retValue;
        
        retValue = p.remove(propertiesPrefix + key);
        return retValue;
    }

    public boolean containsValue(Object value) {
        boolean retValue;
        
        retValue = p.containsValue(propertiesPrefix + value);
        return retValue;
    }

    public Object get(Object key) {
        Object retValue;
        
        retValue = p.get(propertiesPrefix + key);
        return retValue;
    }

    public boolean equals(Object o) {
        boolean retValue;
        
        retValue = p.equals(o);
        return retValue;
    }

    public boolean contains(Object value) {
        boolean retValue;
        
        retValue = p.contains(propertiesPrefix + value);
        return retValue;
    }

    public boolean containsKey(Object key) {
        boolean retValue;
        
        retValue = p.containsKey(propertiesPrefix + key);
        return retValue;
    }

    public java.util.Collection<Object> values() {
        return p.values();
    }

    public String toString() {
        String retValue;
        
        retValue = p.toString();
        return retValue;
    }

    public int size() {
        int retValue;
        
        retValue = p.size();
        return retValue;
    }

    public java.util.Set<Map.Entry<Object, Object>> entrySet() {
        return p.entrySet();
    }

    public Object put(Object key, Object value) {
        Object retValue;
        
        retValue = p.put(propertiesPrefix + key, value);
        return retValue;
    }

    public void clear() {
        p.clear();
    }

    public java.util.Enumeration<Object> keys() {
        java.util.Enumeration<Object> retValue;
        
        retValue = p.keys();
        return retValue;
    }

    public int hashCode() {
        int retValue;
        
        retValue = p.hashCode();
        return retValue;
    }

    public Object clone() {
        Object retValue;
        
        retValue = p.clone();
        return retValue;
    }

    public java.util.Enumeration<Object> elements() {
        java.util.Enumeration<Object> retValue;
        
        retValue = p.elements();
        return retValue;
    }

    public java.util.Set<Object> keySet() {
        java.util.Set<Object> retValue;
        
        retValue = p.keySet();
        return retValue;
    }
    
    
}
