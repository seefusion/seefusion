/*
 * Driver.java
 *
 * This is the main entry point for the JDBC wrapper system.  Will accept `jdbc:seefusion` URLs
 * and will produce wrapped database Connection objects that SeeFusion can monitor
 */

package com.seefusion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author TheArchitect
 */
public class Driver implements java.sql.Driver {
	
	private static final Logger LOG = Logger.getLogger(Driver.class.getName());
	
	static String CLASSNAME = "Driver";

	static boolean driverFirstUse = true;

	static HashMap<String, java.sql.Driver> loadedDrivers = new HashMap<String, java.sql.Driver>();

	// is this See Fusion or See Java?
	static String BUILDNAME = "SeeF" + "usion";

	protected boolean isTracingAPI = false;
	
	private static Driver instance;

	/** Creates a new instance of Driver */
	public Driver() {
	}

	static {
		try {
			try {
				String version = System.getProperty("java.version");
				if (version.startsWith("1.8")) {
					LOG.info("Registering Java8 Implementation of com.seefusion.Driver for Java version " + version);
					try {
						instance = (Driver) Class.forName("com.seefusion.Driver8").newInstance();
					}
					catch(ClassNotFoundException e) {
						LOG.warning("Unable to load Driver8); falling back to Driver6");
						instance = new Driver();
					}

				}
				else {
					// use highest available driver
					LOG.info("Registering Java8 Implementation of com.seefusion.Driver for Java version " + version);
					try {
						instance = (Driver) Class.forName("com.seefusion.Driver8").newInstance();
					}
					catch(ClassNotFoundException e) {
						LOG.warning("Unable to load Driver8); falling back to Driver6");
						instance = new Driver();
					}

				}
				DriverManager.registerDriver(instance);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static String getWrappedURL(String url) {
		int pos1 = url.indexOf('{');
		int pos2 = url.lastIndexOf('}');
		if (pos2 < pos1 || pos1 == -1) {
			return null;
		}
		else {
			return url.substring(pos1 + 1, pos2);
		}
	}

	/**
	 * Get SeeFusion's properties from the end of the JDBC URL (when explicitly
	 * {wrapping a URL}) or from the properties file when in the format
	 * jdbc:seefusion:<i>instance.dsn</i>
	 */
	static Properties parseUrlProperties(String url) throws SQLException {
		Properties p = new Properties();
		int pos = url.lastIndexOf('}');
		if (pos == -1) {
			throw new SQLException("Unable to parse JDBC URL (unmatched {}?)");
		}
		else {
			// jdbc:seefusion:{jdbc:wrappeddriver:...};seefusionOptions=values;...
			String urlProps = url.substring(pos + 1);
			// log("urlProps="+urlProps);
			StringTokenizer st = new StringTokenizer(urlProps, ";");
			int equalsPos;
			while (st.hasMoreTokens()) {
				String thisToken = st.nextToken();
				String thisName;
				String thisValue;
				equalsPos = thisToken.indexOf('=');
				if (equalsPos == -1) {
					thisName = thisToken;
					thisValue = "true";
				}
				else if (equalsPos == thisToken.length() - 1) {
					thisName = thisToken.substring(0, equalsPos);
					thisValue = "true";
				}
				else {
					thisName = thisToken.substring(0, equalsPos);
					thisValue = thisToken.substring(equalsPos + 1, thisToken.length());
				}
				p.put(thisName.toLowerCase(), thisValue.trim());
				if (thisName.equalsIgnoreCase("driver")) {
					loadDriver(thisValue);
				}
			}
			p.put("url", getWrappedURL(url));

		}
		return p;
	}

	/**
	 * Load driver class, if it's not been loaded already.
	 */
	static void loadDriver(String name) {
		if (!Util.isEmptyString(name) && !loadedDrivers.containsKey(name)) {
			LOG.info("Loading driver " + name);
			try {
				loadedDrivers.put(name, (java.sql.Driver)Class.forName(name).newInstance());
			}
			catch (Exception e) {
				loadedDrivers.put(name, null);
				LOG.log(Level.WARNING, "Exception attempting to load driver: " + name, e);
			}
		}
	}
	
	static java.sql.Driver getDriver(String name) {
		return loadedDrivers.get(name);
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return url.toLowerCase().startsWith("jdbc:seefusion:");
	}

	@Override
	public Connection connect(String url, Properties connectionProperties) throws SQLException {
		if (acceptsURL(url)) {
			// parseURL also loads the driver if specified on url
			Properties urlConfig = parseUrlProperties(url);
			// log("connect("+url+")=true");
			if (driverFirstUse) {
				LOG.info("Loaded and in use.");
				driverFirstUse = false;
			}
			Connection c = null;
			try {
				String wrappedURL = urlConfig.getProperty("url");
				String driverName = urlConfig.getProperty("driver");
				LOG.fine("Creating connection for url: " + url);
				if(driverName != null) {
					if(loadedDrivers.containsKey(driverName)) {
						LOG.fine("Creating connection using directly instantiated driver " + driverName);
						java.sql.Driver driver = loadedDrivers.get(driverName);
						if(driver != null) {
							c = driver.connect(wrappedURL, connectionProperties);
							if(c == null) {
								LOG.warning("Driver " + driverName + " refused to accept the URL \"" + wrappedURL + "\"");
							}
						}
					} else {
						LOG.warning("Unable to find driver instance in hashmap for " + driverName);
					}
				}
				if( c==null ) {
					c = DriverManager.getConnection(wrappedURL, connectionProperties);
				}
			}
			catch (SQLException e) {
				LOG.log(Level.WARNING, "Unable to connect", e);
				throw e;
			}
			catch (Exception e) {
				LOG.log(Level.WARNING, "Unable to connect", e);
			}
			try {
				if (c == null) {
					return null;
				}
				String dialect = urlConfig.getProperty("dialect");
				if(dialect != null) {
					dialect = dialect.toLowerCase();
				}
				String className = c.getClass().getName().toLowerCase();
				SqlDialectMetadata meta = null;
				if(className.startsWith("oracle.jdbc") || "oracle".equals(dialect)) {
					meta = new OracleDialectMetadata();
				}
				else if (className.contains("sqlserver") || "sqlserver".equals(dialect)) {
					meta = new SqlServerDialectMetadata();
				}
				else if (className.contains("mysql") || "mysql".equals(dialect)) {
					meta = new MySQLDialectMetadata();
				}
				Properties connectionMetadata;
				if(meta == null) {
					connectionMetadata= new Properties();
				}
				else {
					connectionMetadata= meta.getMetadata(c);
				}
				if(urlConfig.containsKey("dsn")) {
					connectionMetadata.put("dsn", urlConfig.getProperty("dsn"));
				}
				// if the user explicitly loaded com.seefusion.Driver, redirect to the JRE-appropriate wrap method
				return instance.wrap(c, connectionProperties, urlConfig, connectionMetadata);
			}
			catch (Exception e) {
				e.printStackTrace();
				LOG.log(Level.WARNING, "Unable to connect", e);
				return null;
			}
		}
		else {
			// log("connect("+url+")=false");
			return null;
		}
	}

	protected Connection wrap(Connection c, Properties connectionProperties, Properties sfUrlConfig, Properties connectionMetadata) {
		if(c.getClass().getName().toLowerCase().startsWith("oracle")) {
			return new OracleConnectionImpl(c, connectionProperties, sfUrlConfig, connectionMetadata, isTracingAPI);
		}
		else {
			return new ConnectionImpl(c, connectionProperties, sfUrlConfig, connectionMetadata, isTracingAPI);
		}
	}

	@Override
	public int getMajorVersion() {
		return Integer.parseInt(SeeFusionMain.getVersion().split("\\.")[0]);
	}

	@Override
	public int getMinorVersion() {
		return Integer.parseInt(SeeFusionMain.getVersion().split("\\.")[1]);
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		// log("seefusion.Driver.getPropertyInfo(\""+url+"\")");
		return null;
	}

	@Override
	public boolean jdbcCompliant() {
		return true;
	}
	
	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

}
