/**
 * 
 */
package com.seefusion.installer;

/**
 * @author Daryl
 * 
 */
class InstallWebXml {

	public static final String FILTER_CLASS_V4 = "<filter-class>com.seefusion.SeeFusion</filter-class>";
	public static final String FILTER_CLASS = "<filter-class>com.seefusion.Filter</filter-class>";

	static final String FILTER_XML_V4 = "\r\n<filter>\r\n" + 
			"	<filter-name>SeeFusion</filter-name>\r\n" + 
			"	" + FILTER_CLASS_V4 + "\r\n" + 
			"</filter>\r\n";
	
	static final String FILTER_XML = "\r\n<filter>\r\n" + 
			"	<filter-name>SeeFusion</filter-name>\r\n" + 
			"	" + FILTER_CLASS + "\r\n" + 
			"</filter>\r\n";
	
	static final String FILTER_MAPPING_XML = "\r\n<filter-mapping>\r\n" + 
			"	<filter-name>SeeFusion</filter-name>\r\n" + 
			"	<url-pattern>/*</url-pattern>\r\n" + 
			"</filter-mapping>\r\n";
	
	static boolean isInstalled(String xml) {
		return xml.indexOf(FILTER_XML) != -1
				|| xml.indexOf(FILTER_XML_V4) != -1;
	}
	
	static boolean debugMode=false;
	
	static String uninstall(String xml) throws InstallationException {
		return uninstallFilter(xml);
	}
	
	/**
	 * @return updated XML
	 * @throws InstallationException 
	 */
	static String uninstallFilter(String xml) throws InstallationException {
		// find our <filter-class>
		String filterClass = FILTER_CLASS_V4;
		int filterClassPos = xml.indexOf(FILTER_CLASS_V4);
		if (filterClassPos == -1) {
			filterClass = FILTER_CLASS;
			filterClassPos = xml.indexOf(FILTER_CLASS);
			if (filterClassPos == -1) {
				throw new InstallationException("Uninstall filter failed: cannot find \"<filter-class>com.seefusion.(SeeFusion|Filter)</filter-class>\".");
			}
		}
		// find closest <filter> to it
		String snip = xml.substring(0, filterClassPos);
		int filterStartPos = snip.lastIndexOf("<filter>");
		if (filterStartPos == -1) {
			throw new InstallationException("Uninstall filter failed: cannot find \"<filter>\" that matches SeeFusion filter-class.");
		}
		// is it the same <filter> ?
		int filterClassPos2 = xml.indexOf(filterClass, filterStartPos);
		if (filterClassPos2 != filterClassPos) {
			throw new InstallationException("Uninstall filter failed: \"<filter>\" before SeeFusion \"<filter-class>\" is not SeeFusion's filter.");
		}
		// the filter-class /is/ for this filter. Snip complete
		// <filter>...</filter>
		int filterEndPos = xml.indexOf("</filter>", filterStartPos);
		if (filterEndPos == -1) {
			throw new InstallationException("Uninstall filter failed: Cannot find \"</filter>\" after SeeFusion \"<filter>\".");
		}
		filterEndPos += "</filter>".length();
		String filterTag = xml.substring(filterStartPos, filterEndPos);

		debug("---- filtertag ----");
		debug(filterTag);
		debug("---- /filtertag ----");

		// find filter-name so we can get the correct filter-mapping
		int filterNameTagStartPos = filterTag.indexOf("<filter-name>");
		if (filterNameTagStartPos == -1) {
			throw new InstallationException("Uninstall filter failed: Cannot find \"<filter-name>\" after SeeFusion \"<filter>\".");
		}
		int filterNameTagEndPos = filterTag.indexOf("</filter-name>", filterNameTagStartPos);
		if (filterNameTagEndPos == -1) {
			throw new InstallationException("Uninstall filter failed: Cannot find \"</filter-name>\" after SeeFusion \"<filter>\".");
		}
		String filterName = filterTag.substring(filterNameTagStartPos + "<filter-name>".length(), filterNameTagEndPos);
		debug("Filter-Name is \"" + filterName + "\"");

		// remove filter-mapping
		xml = uninstallFilterMapping(xml, filterName, filterEndPos);

		// remove filter
		xml = xml.substring(0, filterStartPos) + xml.substring(filterEndPos);
		
		return xml;
	}

	/**
	 * @return updated xml
	 * @throws InstallationException 
	 */
	static String uninstallFilterMapping(String xml, String filterName, int filterEndPosX) throws InstallationException {
		// find our <filter-name>
		int filterNamePos = xml.indexOf("<filter-name>" + filterName + "</filter-name>", filterEndPosX);
		if (filterNamePos == -1) {
			throw new InstallationException("Uninstall filter-mapping failed: cannot find \"<filter-name>"
					+ filterName
					+ "</filter-name>\" after \"<filter>\".");
		}
		// find closest <filter-mapping> to it
		String snip = xml.substring(0, filterNamePos);
		int filterMappingStartPos = snip.lastIndexOf("<filter-mapping>");
		if (filterMappingStartPos == -1) {
			throw new InstallationException("Uninstall filter failed: cannot find \"<filter-mapping>\" that matches SeeFusion filter-name \""
					+ filterName
					+ "\".");
		}
		// is it the same <filter-mapping> ?
		int filterNamePos2 = xml.indexOf("<filter-name>" + filterName + "</filter-name>", filterMappingStartPos);
		if (filterNamePos2 != filterNamePos) {
			throw new InstallationException("Uninstall filter-mapping failed: \"<filter-mapping>\" before SeeFusion \"<filter-name>\" is not SeeFusion's filter mapping.");
		}

		// the filter-name /is/ for this filter-mapping. Find complete
		// <filter-mapping>...</filter-mapping>
		int filterMappingEndPos = xml.indexOf("</filter-mapping>", filterMappingStartPos);
		if (filterMappingEndPos == -1) {
			throw new InstallationException("Uninstall filter-mapping failed: Cannot find \"</filter-mapping>\" after SeeFusion \"<filter-mapping>\".");
		}
		filterMappingEndPos += "</filter-mapping>".length();
		String filterMappingTag = xml.substring(filterMappingStartPos, filterMappingEndPos);

		debug("---- filterMappingTag ----");
		debug(filterMappingTag);
		debug("---- /filterMappingTag ----");

		// remove filter
		xml = xml.substring(0, filterMappingStartPos) + xml.substring(filterMappingEndPos);
		
		return xml;
	}

	/*
	 * <!ELEMENT web-app (icon?, display-name?, description?,
	 * distributable?, context-param*, filter*, filter-mapping*, listener*,
	 * servlet*, servlet-mapping*, session-config?, mime-mapping*,
	 * welcome-file-list?, error-page*, taglib*, resource-env-ref*,
	 * resource-ref*, security-constraint*, login-config?, security-role*,
	 * env-entry*, ejb-ref*, ejb-local-ref*)>
	 */
	static final String[] filterInsertionTokens = {"</context-param>", "</distributable>", "</description>", "</display-name>", "</icon>", "<web-app>"};
	static final String[] filterMappingInsertionTokens = {"</filter>", "</context-param>", "</distributable>", "</description>", "</display-name>", "</icon>", "<web-app>"};
	
	static String install(String xml) throws InstallationException {
		return installFilter(xml);
	}
	
	static String installFilter(String xml) throws InstallationException {

		int insertPos = findInsertionPos(xml, filterInsertionTokens);		
		if(insertPos == -1) {
			throw new InstallationException("Install filter failed: Cannot find appropriate place to add <filter>.");
		}
		insertPos = adjustForComment(xml, insertPos);

		xml = xml.substring(0, insertPos) + FILTER_XML + xml.substring(insertPos);

		xml = installFilterMapping(xml);

		return xml;
	}

	static String installFilterMapping(String xml) throws InstallationException {
		
		int insertPos = findInsertionPos(xml, filterMappingInsertionTokens);		
		if(insertPos == -1) {
			throw new InstallationException("Install filter-mapping failed: Cannot find appropriate place to add <filter-mapping>.");
		}
		insertPos = adjustForComment(xml, insertPos);
		
		xml = xml.substring(0, insertPos) + FILTER_MAPPING_XML + xml.substring(insertPos);
		
		return xml;
	}
	
	static int adjustForComment(String xml, int pos) {
		String s = xml.substring(0, pos);
		int pos1 = s.lastIndexOf("<!--");
		if(pos1 == -1) return pos;
		int pos2 = xml.indexOf("-->", pos1);
		if(pos2 == -1) return pos;
		if(pos1 < pos && pos2 > pos) {
			pos = pos2+3;
		}
		return pos;
	}
	
	static int findInsertionPos(String xml, String[] places) {
		int pos;
		for(int i=0; i < places.length; i++) {
			if( (pos=xml.lastIndexOf(places[i])) != -1) {
				pos += places[i].length();
				return pos;
			}
		}
		return -1;
	}
	
	static void debug(String s) {
		if (debugMode) {
			System.out.println(s);
		}
	}
}
