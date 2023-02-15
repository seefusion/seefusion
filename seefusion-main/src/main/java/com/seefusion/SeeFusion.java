/*
 * SeeFusion.java
 *
 */

package com.seefusion;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SeeFusion implements Observer<Config> {

	static {
		Logger.getLogger("com.seefusion").addHandler(new SeeFusionHandler());
	}
	private static final Logger LOG = Logger.getLogger(SeeFusion.class.getName());
	
	static final int COUNTERS_HISTORY_SIZE = 120;
	
	ClassLoader j2eeClassLoader;

	static final String COPYRIGHT = SeeFusionMain.COPYRIGHT;

	static final String DELIM_LIST = ", \t\n\r\f";
	
	private static SeeFusion instance;

	boolean debugEnabled = false;

	SuffixList debugSuffixList;

	SuffixList debugNeverSuffixList;

	private IPList ipWhiteList = new IPList();

	CountersHistory historyMinutes;

	// Per-Filter-Object instance/configuration variables
	LinkedList<SocketListener> httpListeners = new LinkedList<SocketListener>();

	static final int DEFAULT_PORT = 8999;

	final DbLogger dbLogger = new DbLogger(this);

	boolean dbLoggerEnabled = false;

	static String hostName;

	Observer<Config> cloudwatchHelper;
	
	DbLogger getDbLogger() {
		return this.dbLogger;
	}

	SmtpSender smtpSender = null;

	// it's a single threaded process that asks for this, no reason to
	// synchronize
	SmtpSender getSmtpSender() {
		if (smtpSender == null) {
			smtpSender = new SmtpSender(smtpServer, smtpUsername, smtpPassword);
		}
		return smtpSender;
	}

	int maxRequests = 15;

	long dbLoggerThreshold = 0;

	java.sql.Driver dbLoggerDriver; 
	
	private long dbLoggerQueryThreshold = 0;

	String browserURL = null;

	boolean isRunning = false;

	boolean clickableURLs = false;

	boolean displayThreadNames = false;

	String clickableIPURL = null;

	LinkedList<ServerEntry> dashboardServersList = null;

	int dashboardMinSlowRequests = 1;

	String instanceName = null;

	Password httpPassword = new Password();

	Password httpKillPassword = new Password();

	Password httpConfigPassword = new Password();

	private String smtpServer = "";
	private String smtpUsername = "";
	private String smtpPassword = "";

	// set during init
	String jreString;

	private boolean isGloballyEnabled = true;

	private RequestList masterRequestList;
	
	String webroot;

	static SeeFusion getInstance() {
		String loc = getJarLocation();
		if(loc==null) {
			loc = "./WEB-INF/";
		}
		return getInstance(loc);
	}

	@SuppressWarnings("PMD.DoubleCheckedLocking")
	static SeeFusion getInstance(String configDir) {
		if(instance==null) {
			synchronized (SeeFusion.class) {
				// double checked locking.  So sue me.
				if(instance==null) {
					instance = new SeeFusion(configDir);
				}
            }
		}
		return instance;
	}
	
	RequestList getMasterRequestList() {
		return masterRequestList;
	}

	Password getHttpPassword() {
		return httpPassword;
	}

	Password getHttpKillPassword() {
		return httpKillPassword;
	}

	Password getHttpConfigPassword() {
		return httpConfigPassword;
	}

	void setHttpPassword(String s) {
		httpPassword = new Password(s);
		httpPasswordB64Token = null;
	}

	void setHttpKillPassword(String s) {
		httpKillPassword = new Password(s);
	}

	void setHttpConfigPassword(String s) {
		httpConfigPassword = new Password(s);
	}

	boolean isLogQueryExceptions = false;

	boolean isConfigFound = false;

	boolean isDisplayRelativeTimes = true;

	boolean isDisplayRelativeTimes() {
		return isDisplayRelativeTimes;
	}

	void setDisplayRelativeTimes(boolean tf) {
		isDisplayRelativeTimes = tf;
	}

	private static final long startTick = System.currentTimeMillis();
	static long getUptime() {
		return System.currentTimeMillis() - startTick;
	}

	static final String AUTH_HEADER_NAME = "X-SeeFusion-Auth";

	ResourceBundle resources = ResourceBundle.getBundle("com.seefusion.SeeFusionMessages");

	MessageFormatFactory messageFormatFactory = new MessageFormatFactory(resources);

	String getBrowserURL() {
		return browserURL;
	}

	int getDashboardMinSlowRequests() {
		return dashboardMinSlowRequests;
	}

	boolean hasPassword() {
		return !(getHttpConfigPassword().equals("")
				&& getHttpKillPassword().equals("")
				&& getHttpPassword().equals(""));
	}
	
	Perm getPerm(String password) {
		Perm userPerm = new Perm();
		if(!hasPassword()) {
			userPerm.add(Perm.CONFIG);
			userPerm.add(Perm.KILL);
			userPerm.add(Perm.LOGGEDIN);
		}
		else if (getHttpConfigPassword().equals(password)) {
			userPerm.add(Perm.CONFIG);
			userPerm.add(Perm.KILL);
			userPerm.add(Perm.LOGGEDIN);
		}
		else if (getHttpKillPassword().equals(password)) {
			userPerm.add(Perm.KILL);
			userPerm.add(Perm.LOGGEDIN);
		}
		else if (getHttpPassword().equals(password)) {
			userPerm.add(Perm.LOGGEDIN);
		}
		LOG.fine("userPermFlags: " + userPerm);
		return userPerm;
	}
	
	String httpPasswordB64Token = null;

	
	
	/**
	 * Used by dashboard fetcher
	 */
	String getHttpPasswordB64Token() {
		if (httpPassword == null) {
			return null;
		}
		if (httpPasswordB64Token == null) {
			String token = ":" + httpPassword;
			httpPasswordB64Token = Base64.encodeBytes(token.getBytes());
		}
		return httpPasswordB64Token;
	}

	void setDashboardMinSlowRequests(int dashboardMinSlowRequests) {
		this.dashboardMinSlowRequests = dashboardMinSlowRequests;
	}

	ResourceBundle getResources() {
		return resources;
	}

	MessageFormatFactory getMessageFormatFactory() {
		return messageFormatFactory;
	}

	LinkedList<String> RequestNameTranslators = new LinkedList<String>();
	
	private boolean isTestCase;;

	public static String getVersion() {
		return SeeFusionMain.getVersion();
	}

	void createRequest(RequestInfo pi) {
		masterRequestList.createRequest(pi);
	}

	RequestInfo createRequest(String serverName, String requestURI, String queryString, String remoteIP, String method, String pathInfo, boolean isSecure) {
		// is there already an active request; that is, are we entering the same
		// filter multiple times per request?
		RequestInfo ri = RequestInfo.getCurrent();
		if (ri == null) {
			ri = new RequestInfo(this, serverName, requestURI, queryString, remoteIP, method, pathInfo, isSecure);
			createRequest(ri);
		}
		return ri;
	}

	void releaseRequest(RequestInfo pi) {
		if (historyMinutes != null) {  // unit test?
			historyMinutes.incrementPageCounter(pi.getElapsedTime());
		}
		if (masterRequestList != null) { // unit test?
			masterRequestList.releaseRequest(pi);
		}
		if (dbLogger != null
				&& dbLoggerThreshold > 0
				&& pi.getElapsedTime() >= dbLoggerThreshold) {
			dbLogger.log(pi);
		}
	}

	void setDbLoggerThreshold(long ms) {
		dbLoggerThreshold = ms;
	}

	long getDbLoggerThreshold() {
		return dbLoggerThreshold;
	}

	void setDbLoggerQueryThreshold(long ms) {
		dbLoggerQueryThreshold = ms;
	}

	long getDbLoggerQueryThreshold() {
		return dbLoggerQueryThreshold;
	}

	Config config = null;

	public Config getConfig() {
		if(config==null) {
			// called from unit tests, fake it
			config = new Config();
		}
		return config;
	}
	
	SeeFusion(){
	}

	SeeFusion(boolean isTestCase) {
		historyMinutes = new CountersHistory(COUNTERS_HISTORY_SIZE, this);
		masterRequestList = new RequestList();
		this.isTestCase = isTestCase;
	}
	
	static String jarLocation = null;
	static String getJarLocation() {
		if(jarLocation == null) {
			URL sfLoc = SeeFusion.class.getClassLoader().getResource("com/seefusion/SeeFusion.class");
			if (sfLoc == null) {
				throw new RuntimeException("Unable to determine location of jar!");
			}
			String jarLoc = sfLoc.toString();
			int pos = jarLoc.indexOf('!');
			if (pos != -1) {
				jarLoc = jarLoc.substring(0, pos);
			}
			else {
				pos = jarLoc.indexOf("com/seefusion/SeeFusion.class");
				if (pos != -1) {
					jarLoc = jarLoc.substring(0, pos);
				}
			}
			jarLoc = jarLoc.replace("jar:", "");
			jarLocation = jarLoc.replace("file:", "");
		}
		return jarLocation;
	}
	
	static File getSeeFusionDirectory() {
		File loc = new File(SeeFusion.getJarLocation());
		return loc.isDirectory() ? loc : loc.getParentFile();
	}

	
	private SeeFusion(String configDir) {
		LOG.info("SeeFusion starting up...");
		
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			hostName = "(Unknown)";
		}
			
		isRunning = true;
		config = new Config(configDir);
		isConfigFound = config.isConfigBackedByFile();
		LOG.info("Config at " + config.getFilePath());
		config.loadEnvironmentOverrides();
		// config.props.list(System.out);
		config.addObserver(this);
		LOG.info("SeeFusion loaded from: " + getJarLocation());
		LOG.info("SeeFusion " + getVersion() + " " + SeeFusionMain.COPYRIGHT);
		jreString = System.getProperty("java.vm.name")
				+ " "
				+ System.getProperty("java.version")
				+ " ("
				+ System.getProperty("os.name")
				+ " "
				+ System.getProperty("os.version")
				+ ")";
		LOG.info("JRE is " + jreString);

		// create objects that have daemon threads here
		historyMinutes = new CountersHistory(COUNTERS_HISTORY_SIZE, this);
		masterRequestList = new RequestList();

		try {
			configure();
		}
		catch (ConfigException e) {
			LOG.severe(e.getMessage());
		}

		LOG.info("SeeFusion " + getVersion() + " initialized.");
	}

	/**
	 * used in tests
	 */
	boolean isRunning() {
		return isRunning;
	}

	Properties prevValues = new Properties();

	String getNewValue(String key) {
		return getNewValue(key, null);
	}

	String getNewValue(String key, String deflt) {
		String sval = config.getProperty(key);
		if (sval == null) {
			if(deflt != null) {
				config.setProperty(key, deflt);
			}
			return deflt;
		}
		if (sval.equals(prevValues.getProperty(key))) {
			return null;
		}
		else {
			prevValues.setProperty(key, sval);
			return sval;
		}
	}

	private ActiveMonitoringRules activeMonitoringRules;

	ActiveMonitoringRules getActiveMonitoringRules() {
		return activeMonitoringRules;
	}

	void configure() throws ConfigException {
		String sval;
		try {
			if (config == null) {
				throw new IllegalStateException("Cannot configure() before config is available.");
			}

			try {
				if ((sval = getNewValue("enabled", "true")) != null) {
					setGloballyEnabled(Util.parseYesNoParam(sval));
					LOG.info("SeeFusion enabled: " + isGloballyEnabled());
				}
			}
			catch (NumberFormatException e) {
				LOG.info("Unrecognized boolean value for 'enabled' (allowed options are ON, OFF, TRUE, FALSE, YES, NO, 1, 0)");
				setGloballyEnabled(true);
			}

			if ((sval = getNewValue("name", "{hostname}")) != null) {
				sval = insertHostname(sval);
				LOG.info("Instance name: " + sval);
				this.instanceName = sval;
			}

			if ((sval = getNewValue("listeners", "all:8999")) != null) {
				setListeners(sval);
			}

			if ((sval = getNewValue("maxRequests", "50")) != null) {
				try {
					maxRequests = Integer.parseInt(sval);
					LOG.info("maxRequests set to " + sval);
				}
				catch (NumberFormatException e) {
					LOG.info("Unable to parse value to int for maxRequests.");
				}
			}

			if ((sval = getNewValue("historySize", "100")) != null) {
				try {
					masterRequestList.setHistorySize(Integer.parseInt(sval));
					LOG.info("historySize set to " + sval);
				}
				catch (NumberFormatException e) {
					LOG.info("Unable to parse value to int for historySize.");
				}
			}

			if ((sval = getNewValue("slowHistorySize", "100")) != null) {
				try {
					masterRequestList.setSlowHistorySize(Integer.parseInt(sval));
					LOG.info("slowHistorySize set to " + sval);
				}
				catch (NumberFormatException e) {
					LOG.info("Unable to parse value to int for slowHistorySize.");
				}
			}

			if ((sval = getNewValue("slowPageThreshold", "5000")) != null) {
				try {
					masterRequestList.setSlowPageThreshold(Integer.parseInt(sval));
					// default dbLoggerThreshold to slowPageThreshold
					setDbLoggerThreshold(Integer.parseInt(sval));
					LOG.info("slowPageThreshold set to " + sval);
				}
				catch (NumberFormatException e) {
					LOG.info("Unable to parse value to int for slowPageThreshold.");
				}
			}
			if ((sval = getNewValue("slowQueryThreshold", "1000")) != null) {
				try {
					masterRequestList.setSlowQueryThreshold(Integer.parseInt(sval));
					// default dbLoggerThreshold to slowQueryThreshold
					setDbLoggerThreshold(Integer.parseInt(sval));
					LOG.info("slowQueryThreshold set to " + sval);
				}
				catch (NumberFormatException e) {
					LOG.info("Unable to parse value to int for slowQueryThreshold.");
				}
			}

			if ((sval = getNewValue("historyIgnoreSuffixList")) != null) {
				if (sval == null || sval.equals("")) {
					masterRequestList.setHistoryIgnoreSuffixList(null);
					LOG.info("historyIgnoreSuffixList set to Ignore.");
				}
				else {
					masterRequestList.setHistoryIgnoreSuffixList(new SuffixList(sval));
					LOG.info("historyIgnoreSuffixList set to: " + sval);
				}
			}

			if ((sval = getNewValue("dashboardMinSlowRequests", "5")) != null) {
				try {
					setDashboardMinSlowRequests(Integer.parseInt(sval));
					LOG.info("dashboardMinSlowRequests set to " + sval);
				}
				catch (NumberFormatException e) {
					LOG.info("Unable to parse value to int for dashboardMinSlowRequests.");
				}
			}

			if ((sval = getNewValue("devwebroot")) != null) {
				webroot=sval;
				LOG.info("dev webroot set to " + sval);
			}

			int port;
			if(httpListeners.isEmpty()) {
				port = 8999;
			}
			else {
				port = httpListeners.getFirst().port;
			}
			if ((sval = getNewValue("browserURL", "http://" + this.instanceName + ":" + port + "/")) != null) {
				browserURL = sval;
				LOG.info("browserURL set to " + browserURL);
			}

			if (browserURL == null || browserURL.equals("")) {
				String hostname;
				try {
					hostname = InetAddress.getLocalHost().getHostName();
					browserURL = "http://" + hostname + ":" + port + "/";
				} catch (UnknownHostException e) {
					browserURL = "http://" + this.instanceName + ":" + port + "/";
				}
				LOG.info("browserURL set to " + browserURL);
			}

			if ((sval = getNewValue("httpPassword")) != null) {
				LOG.info("httpPassword set.");
				setHttpPassword(sval);
			}

			if ((sval = getNewValue("httpKillPassword")) != null) {
				LOG.info("httpKillPassword set.");
				setHttpKillPassword(sval);
			}
			else if(httpPassword != null && !httpPassword.isBlank()) {
				httpKillPassword = httpPassword;
			}

			if ((sval = getNewValue("httpConfigPassword")) != null) {
				LOG.info("httpConfigPassword set.");
				setHttpConfigPassword(sval);
			}
			else if(httpKillPassword != null && !httpKillPassword.isBlank()) {
				httpConfigPassword = httpKillPassword;
			}
			else if(httpPassword != null && !httpPassword.isBlank()) {
				httpConfigPassword = httpPassword;
			}

			if ((sval = getNewValue("clickableIPURL")) != null) {
				LOG.info("clickableIPURL set to " + sval);
				clickableIPURL = sval;
			}

			if ((sval = getNewValue("ipWhitelist")) != null) {
				ipWhiteList = new IPList(sval);
				LOG.info("ipWhitelist set.");
			}

			if ((sval = getNewValue("dashboardServers")) != null) {
				setDashboardServers(sval);
			}

			// dblogger (re)init:
			try {
				if ((sval = getNewValue("displayRelativeTimes")) != null) {
					setDisplayRelativeTimes(Util.parseYesNoParam(sval));
					LOG.info("displayRelativeTimes: " + Boolean.toString(isDisplayRelativeTimes()));
				}
			}
			catch (NumberFormatException e) {
				LOG.info("Unrecognized boolean value for dbLoggerEnabled (allowed options are ON, OFF, TRUE, FALSE, YES, NO, 1, 0)");
				dbLoggerEnabled = false;
			}

			try {
				if ((sval = getNewValue("clickableURLs", "true")) != null) {
					setClickableURLs(Util.parseYesNoParam(sval));
					LOG.info("Clickable URLs: " + isClickableURLs());
				}
			}
			catch (NumberFormatException e) {
				LOG.info("Unrecognized boolean value for clickableURLs (allowed options are ON, OFF, TRUE, FALSE, YES, NO, 1, 0)");
				setClickableURLs(false);
			}

			try {
				if ((sval = getNewValue("displayThreadNames")) != null) {
					setDisplayThreadNames(Util.parseYesNoParam(sval));
					LOG.info("Display Thread Names: " + isDisplayThreadNames());
				}
			}
			catch (NumberFormatException e) {
				LOG.info("Unrecognized boolean value for displayThreadNames (allowed options are ON, OFF, TRUE, FALSE, YES, NO, 1, 0)");
				setDisplayThreadNames(false);
			}

			try {
				if ((sval = getNewValue("debugEnabled")) != null) {
					setDebugEnabled(Util.parseYesNoParam(sval));
					LOG.info("Debug enabled: " + isDebugEnabled());
				}
			}
			catch (NumberFormatException e) {
				LOG.info("Unrecognized boolean value for 'debugEnabled' (allowed options are ON, OFF, TRUE, FALSE, YES, NO, 1, 0)");
				setDebugEnabled(true);
			}
			if ((sval = getNewValue("debugIPs")) != null) {
				setDebugIPs(sval);
				LOG.info("debugIPs set to: " + sval);
			}
			if ((sval = getNewValue("smtpServer")) != null) {
				setSmtpServer(sval);
				LOG.info("SMTP Server set to: " + sval);
			}
			if ((sval = getNewValue("smtpUsername")) != null) {
				setSmtpUsername(sval);
				LOG.info("SMTP Server username set to: " + sval);
			}
			if ((sval = getNewValue("smtpPassword")) != null) {
				setSmtpPassword(sval);
				LOG.info("SMTP Server password set.");
			}
			if ((sval = getNewValue("debugSuffixList")) != null) {
				debugSuffixList = new SuffixList(sval);
				LOG.info("debugSuffixList set to: " + sval);
			}
			if ((sval = getNewValue("debugNeverSuffixList")) != null) {
				debugNeverSuffixList = new SuffixList(sval);
				LOG.info("debugNeverSuffixList set to: " + sval);
			}
			if ((sval = getNewValue("debugDebug")) != null) {
				setDebugDebug(sval);
				LOG.info("debugDebug set to: " + sval);
			}

			if ((sval = getNewValue("debugStackTargets")) != null) {
				setDebugStackTargets(sval);
				LOG.info("debugStackTargets set to: " + sval);
			}

			if((sval = getNewValue("cloudwatchEnabled", "false")) != null) {
				if(Util.parseYesNoParam(sval)) {
					try {
						cloudwatchHelper = getCloudWatchHelper();
						cloudwatchHelper.update(config);
						LOG.log(Level.INFO, "CloudWatch integration enabled");
					}
					catch(Throwable e) {
						LOG.log(Level.WARNING, "Could not load CloudWatch helper", e);
					}
				}
				else {
					if(cloudwatchHelper != null) {
						// trigger shutdown
						cloudwatchHelper.update(null);
						cloudwatchHelper = null;
					}
				}
			}
			
			
			if ((sval = getNewValue("frameworkAction")) != null) {
				Util.setFrameworkActionParam(sval);
				LOG.info("frameworkAction set to: " + sval);
			}

			if ((sval = getNewValue("jdbcDrivers")) != null) {
				if (sval.length() > 0) {
					String[] aDrivers = sval.split("[" + DELIM_LIST + "]+");
					for (int i = 0; i < aDrivers.length; i++) {
						Driver.loadDriver(aDrivers[i]);
					}
				}
			}

			// dbLogger stuff follows
			boolean dbLogggerWasEnabled = dbLoggerEnabled;
			// dblogger (re)init:
			try {
				if ((sval = getNewValue("dbLoggerEnabled")) != null) {
					dbLoggerEnabled = Util.parseYesNoParam(sval);
					LOG.info("dbLoggerEnabled: " + dbLoggerEnabled);
				}
			}
			catch (NumberFormatException e) {
				LOG.info("Unrecognized boolean value for dbLoggerEnabled (allowed options are ON, OFF, TRUE, FALSE, YES, NO, 1, 0)");
				dbLoggerEnabled = false;
			}
			dbLoggerEnabled = dbLoggerEnabled && isGloballyEnabled && !isTestCase;
			if (!dbLoggerEnabled) {
				dbLogger.setConnectionPool(null);
				setCountersLogInterval(0);
			}
			else {
				// always 'reload' dbLogger if it had been stopped
				boolean reloadDbLogger = !dbLogggerWasEnabled;
				if ((sval = getNewValue("dbDriver")) != null) {
					if (!Util.isEmptyString(sval)) {
						reloadDbLogger = true;
						String driverClassName = DbLogger.DRIVERS.containsKey(sval) ? DbLogger.DRIVERS.get(sval) : sval;
						Driver.loadDriver(driverClassName);
						dbLoggerDriver = Driver.getDriver(driverClassName);
					}
				}

				if ((sval = getNewValue("dbLogThreshold")) != null) {
					try {
						setDbLoggerThreshold(Integer.parseInt(sval));
						LOG.info("dbLogThreshold set to " + sval);
					}
					catch (NumberFormatException e) {
						LOG.info("Unable to parse value to int for dbLoggerThreshold.");
					}
				}
				if ((sval = getNewValue("dbLogQueryThreshold")) != null) {
					try {
						setDbLoggerQueryThreshold(Integer.parseInt(sval));
						LOG.info("dbLogQueryThreshold set to " + sval);
					}
					catch (NumberFormatException e) {
						LOG.info("Unable to parse value to int for dbLogQueryThreshold.");
					}
				}
				if ((sval = getNewValue("dbLogQueryExceptions")) != null) {
					setLogQueryExceptions(sval);
				}
				// check for changes
				if ((sval = getNewValue("dbUserName")) != null) {
					reloadDbLogger = true;
				}
				if ((sval = getNewValue("dbPassword")) != null) {
					reloadDbLogger = true;
				}
				if ((sval = getNewValue("dbType")) != null) {
					reloadDbLogger = true;
				}
				if ((sval = getNewValue("dbJdbcUrl")) != null) {
					reloadDbLogger = true;
				}
				if ((sval = getNewValue("dbLogCountersInterval")) != null) {
					reloadDbLogger = true;
				}

				if (reloadDbLogger) {
					reloadDbLogger(config);
				}
			}
			if (activeMonitoringRules == null) {
				activeMonitoringRules = new ActiveMonitoringRules(this);
				masterRequestList.addObserver(activeMonitoringRules);
			}
			activeMonitoringRules.loadFromXML(config);
			dosManager.loadFromXML(config.getXml());
		}
		catch (RuntimeException e) {
			LOG.log(Level.SEVERE, "Unchecked exception caught during configuration", e);
		}
	}

	@SuppressWarnings("unchecked")
	Observer<Config> getCloudWatchHelper() throws IOException, ClassNotFoundException, NoSuchMethodException,
			InstantiationException, IllegalAccessException, InvocationTargetException {
		if(cloudwatchHelper==null) {
			Class<Observer<Config>> cloudwatchHelperClass;
			// first try classpath
			try {
				cloudwatchHelperClass = (Class<Observer<Config>>)Class.forName("com.seefusion.CloudWatchHelper");
			}
			catch(ClassNotFoundException e) {
				ClassLoader loader = new CloudWatchHelperClassLoaderFactory().getClassLoader();
				cloudwatchHelperClass = (Class<Observer<Config>>) loader.loadClass("com.seefusion.CloudWatchHelper");
			}
			Constructor<Observer<Config>> c = cloudwatchHelperClass.getConstructor(SeeFusion.class);
			Object[] args = new Object[1];
			args[0] = this;
			cloudwatchHelper = c.newInstance(args);
		}
		return cloudwatchHelper;
	}

	private String insertHostname(String sval) {
		if (sval.indexOf("{hostname}") != -1) {
			try {
				String hostname = InetAddress.getLocalHost().getHostName();
				int pos = hostname.indexOf('.');
				if (pos > 0) {
					hostname = hostname.substring(0, pos);
				}
				sval = sval.replaceAll("\\{hostname\\}", hostname);
			}
			catch (UnknownHostException e) {
				sval = "SeeFusion";
			}
		}
		return sval;
	}

	private void reloadDbLogger(Config config) {
		String sval;
		// get all values (whether changed or not)
		String dbLoggerUserName = config.getProperty("dbUserName");
		String dbLoggerPassword = config.getProperty("dbPassword");
		String dbLoggerJdbcUrl = config.getProperty("dbJdbcUrl");
		if (dbLoggerUserName == null) {
			dbLoggerUserName = "";
		}
		if (dbLoggerPassword == null) {
			dbLoggerPassword = "";
		}
		if (dbLoggerJdbcUrl == null || dbLoggerJdbcUrl.length() == 0) {
			LOG.info("dbJdbcUrl not set, unable to start logger.");
			return;
		}

		if(dbLoggerConnectionPool != null) {
			dbLoggerConnectionPool.close();
		}
		try {
			dbLoggerConnectionPool = new DbLoggerConnectionPool(dbLoggerJdbcUrl, dbLoggerUserName, dbLoggerPassword);
			dbLoggerConnectionPool.validate();
			dbLogger.setConnectionPool(dbLoggerConnectionPool);
		}
		catch(SQLException e) {
			LOG.log(Level.WARNING, "Unable to connect dbLogger to database", e);
			return;
		}
		
		// dbLogCountersInterval
		sval = config.getProperty("dbLogCountersInterval");
		if (sval != null && sval.length() != 0) {
			try {
				setCountersLogInterval(Integer.parseInt(sval));
				LOG.info("dbLogCountersInterval set to: " + sval);
			}
			catch (NumberFormatException e) {
				LOG.warning("Unable to parse value to int for dbLogCountersInterval.");
			}
		}
		else {
			setCountersLogInterval(0);
		}
		if (dbLogger.isDatabaseOk()) {
			LOG.info("Database logging enabled.");
		}
		else {
			LOG.warning("Database logging is NOT enabled.");
		}
	}

	CountersLogger countersLogger;

	/**
	 * Sets the interval and, if necessary, starts the logger thread
	 * 
	 * @param countersLogInterval
	 *            The countersLogInterval to set.
	 */
	void setCountersLogInterval(int countersLogInterval) {

		if (countersLogger == null) {
			countersLogger = new CountersLogger(this);
		}
		countersLogger.setIntervalSeconds(countersLogInterval);
	}

	List<String> debugStackTargets = null;

	void setDebugStackTargets(String s) {
		debugStackTargets = null;
		if (s != null) {
			StringTokenizer st = new StringTokenizer(s, DELIM_LIST);
			if (st.hasMoreTokens()) {
				debugStackTargets = new LinkedList<String>();
				while (st.hasMoreTokens()) {
					debugStackTargets.add(st.nextToken().toLowerCase());
				}
			}
		}
	}

	List<String> getDebugStackTargets() {
		return debugStackTargets;
	}

	private String listeners;
	void setListeners(String listeners) {
		if(!listeners.equalsIgnoreCase(this.listeners)) {
			this.listeners = listeners;
			for (SocketListener httpListener : httpListeners) {
				httpListener.shutdown();
			}
			httpListeners.clear();
			if(!isTestCase) {
				String listener;
				for (StringTokenizer st = new StringTokenizer(listeners, DELIM_LIST); st.hasMoreTokens();) {
					listener = st.nextToken();
					SocketListener httpListener = new SocketListener(this, listener);
					httpListeners.add(httpListener);
				}
			}
		}
	}

	String getInstanceName() {
		return instanceName;
	}

	void shutdown() {
		/* remove until we figure out how to handle repeated invocations
		LOG.info("Shutdown");

		this.isRunning = false;
		instanceName = null;
		for (SocketListener httpListener : httpListeners) {
			httpListener.destroy();
		}
		if (countersLogger != null) {
			countersLogger.setIntervalSeconds(0);
		}
		if (masterRequestList != null) {
			masterRequestList.shutdown();
		}
		dbLogger.shutdown();
		config.write();
		SeeFusion.instance = null;
		*/
	}

	List<ServerEntry> getDashboardServersList() {
		return dashboardServersList;
	}

	void setDashboardServers(String dashboardServers) {
		dashboardServersList = null;
		if (dashboardServers == null || dashboardServers.length() == 0) {
			// nop
		}
		else {
			String addr = "(unknown)";
			try {
				// loop through server list
				StringTokenizer st = new StringTokenizer(dashboardServers, DELIM_LIST);
				String ip;
				int port, colonPos;
				while (st.hasMoreTokens()) {
					addr = st.nextToken();
					if ((colonPos = addr.indexOf(':')) != -1) {
						ip = addr.substring(0, colonPos);
						port = Integer.parseInt(addr.substring(colonPos + 1, addr.length()));
					}
					else {
						ip = addr;
						port = DEFAULT_PORT;
					}
					try {
						InetAddress thisAddress = InetAddress.getByName(ip);
						addDashboardServer(new ServerEntry(addr, thisAddress, port));
					}
					catch (UnknownHostException e) {
						LOG.log(Level.WARNING, "Unable to add server " + addr + " to dashboardServers list", e);
					}
				}
			}
			catch (NumberFormatException e) {
				LOG.info("Unable to add server " + addr + " to list, cannot convert port number to int.");
			}
		}
	}

	HashSet<String> duplicateServers;
	void addDashboardServer(ServerEntry e) {
		if (dashboardServersList == null) {
			dashboardServersList = new LinkedList<ServerEntry>();
			duplicateServers = new HashSet<String>();
		}
		if (duplicateServers.contains(e.inetAddress.getHostAddress())) {
			LOG.info("Adding dashboarded server (2/2) at " + e.inetAddress.getHostAddress());
			dashboardServersList.addLast(e);
			duplicateServers.remove(e.inetAddress.getHostAddress());
		}
		else {
			LOG.info("Adding dashboarded server at " + e.inetAddress.getHostAddress());
			dashboardServersList.addLast(e);
			duplicateServers.add(e.inetAddress.getHostAddress());
		}
	}

	boolean isDebugDebug;

	void setDebugDebug(String tf) {
		if (tf != null) {
			try {
				isDebugDebug = Util.parseYesNoParam(tf);
			}
			catch (NumberFormatException e) {
				LOG.info("debugDebug: Unrecognized boolean value (allowed options are ON, OFF, TRUE, FALSE, YES, NO, 1, 0): "
						+ tf);
			}
		}
	}

	boolean isDebugDebug() {
		return isDebugDebug;
	}

	Set<String> debugIPs;

	boolean isDebugAll;

	void setDebugIPs(String debugIPlist) {
		isDebugAll = false;
		if (debugIPlist == null) {
			debugIPs = null;
		}
		else {
			StringTokenizer st = new StringTokenizer(debugIPlist, DELIM_LIST);
			if (!st.hasMoreTokens()) {
				debugIPs = null;
			}
			else {
				debugIPs = new HashSet<String>();
				String token;
				synchronized (debugIPs) {
					while (st.hasMoreTokens()) {
						token = st.nextToken();
						if (token.equalsIgnoreCase("all")) {
							isDebugAll = true;
							break;
						}
						else {
							debugIPs.add(token);
						}
					}
				}
			}
		}
	}

	boolean isDebugIP(String ip) {
		return isDebugAll || (debugIPs != null && debugIPs.contains(ip));
	}

	/**
	 * @return Returns whether we should log all queries with SQLExceptions.
	 */
	boolean isLogQueryExceptions() {
		return this.isLogQueryExceptions;
	}

	/**
	 * @param isLogQueryExceptions
	 *            Returns whether we should log all queries with SQLExceptions.
	 */
	void setLogQueryExceptions(boolean isLogQueryExceptions) {
		this.isLogQueryExceptions = isLogQueryExceptions;
	}

	void setLogQueryExceptions(String tf) {
		if (tf != null) {
			try {
				isLogQueryExceptions = Util.parseYesNoParam(tf);
				LOG.info("dbLogQueryExceptions set to " + tf);
			}
			catch (NumberFormatException e) {
				LOG.info("dbLogQueryExceptions: Unrecognized boolean value (allowed options are ON, OFF, TRUE, FALSE, YES, NO, 1, 0): "
						+ tf);
			}
		}
	}

	long getFreeMemory() {
		MemoryUsage usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		return usage.getCommitted() - usage.getUsed();
	}

	long getTotalMemory() {
		MemoryUsage usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		return usage.getCommitted();
	}

	float getAvailableMemoryPct() {
		return (float) 100.0 * (getFreeMemory()) / getTotalMemory();
	}
	
	static double getCurrentLoadAverage() {
		return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
	}

	// Implement Observer:
	@Override
	public void update(Config cfg) {
		try {
			configure();
		}
		catch (ConfigException e) {
			LOG.severe(e.getMessage());
		}
	}

	private DosManager dosManager = new DosManager(this);

	private AuthTokenCache authTokenCache = new AuthTokenCache(120);

	private Profiler profiler = new Profiler();

	private DbLoggerConnectionPool dbLoggerConnectionPool;


	public static void setRequestName(String s) {
		SeeFusionRequest sfr =SeeFusionRequest.getThisRequest();;
		if (sfr != null) {
			sfr.setRequestName(s);
		}
	}

	// Logging helper methods:
	public static String trace(String s) {
		SeeFusionRequest sfr =SeeFusionRequest.getThisRequest();;
		if (sfr != null) {
			return sfr.trace(s);
		}
		return null;
	}

	/**
	 * @return Returns the isGloballyEnabled.
	 */
	boolean isGloballyEnabled() {
		return this.isGloballyEnabled;
	}

	/**
	 * @param isGloballyEnabled
	 *            The isGloballyEnabled to set.
	 */
	void setGloballyEnabled(boolean isGloballyEnabled) {
		this.isGloballyEnabled = isGloballyEnabled;
	}

	/**
	 * @return Returns the clickableURLs.
	 */
	boolean isClickableURLs() {
		return this.clickableURLs;
	}

	/**
	 * @param clickableURLs
	 *            The clickableURLs to set.
	 */
	void setClickableURLs(boolean clickableURLs) {
		this.clickableURLs = clickableURLs;
	}

	/**
	 * @return Returns the j2eeClassLoader.
	 */
	ClassLoader getJ2eeClassLoader() {
		return this.j2eeClassLoader;
	}

	/**
	 * @param classLoader
	 *            The j2eeClassLoader to set.
	 */
	void setJ2eeClassLoader(ClassLoader classLoader) {
		this.j2eeClassLoader = classLoader;
	}

	/**
	 * @return Returns the maxRequests.
	 */
	int getMaxRequests() {
		return this.maxRequests;
	}

	/**
	 * @param maxRequests
	 *            The maxRequests to set.
	 */
	void setMaxRequests(int maxRequests) {
		this.maxRequests = maxRequests;
	}

	/**
	 * @return Returns the debugEnabled.
	 */
	boolean isDebugEnabled() {
		return this.debugEnabled;
	}

	/**
	 * @param debugEnabled
	 *            The debugEnabled to set.
	 */
	void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}

	/**
	 * @return the jreString
	 */
	String getJreString() {
		return this.jreString;
	}

	/**
	 * @return the displayThreadNames
	 */
	boolean isDisplayThreadNames() {
		return this.displayThreadNames;
	}

	/**
	 * @param displayThreadNames the displayThreadNames to set
	 */
	void setDisplayThreadNames(boolean displayThreadNames) {
		this.displayThreadNames = displayThreadNames;
	}

	List<String> getActiveThreadNames() {
		return masterRequestList.getActiveThreadNames();
	}
	
	String getSmtpServer() {
		return smtpServer;
	}

	void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	String getSmtpServerUser() {
		return smtpUsername;
	}

	void setSmtpUsername(String smtpServerUser) {
		this.smtpUsername = smtpServerUser;
	}

	String getSmtpServerPassword() {
		return smtpPassword;
	}

	void setSmtpPassword(String smtpServerPassword) {
		this.smtpPassword = smtpServerPassword;
	}

	IPList getIpWhitelist() {
		return ipWhiteList;
	}

	DosManager getDosManager() {
		return dosManager;
	}

	// public because cloudwatchHelper uses it
	public CountersHistory getHistoryMinutes() {
		return historyMinutes;
	}

	public String getWebroot() {
		return webroot;
	}

	AuthTokenCache getAuthTokenCache() {
		return authTokenCache;
	}

	Profiler getProfiler() {
		return profiler;
	}

	DbLoggerConnectionPool getDbLoggerConnectionPool() {
		return dbLoggerConnectionPool;
	}

}
