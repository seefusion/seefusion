/**
 * 
 */
package com.seefusion;

import java.io.IOException;
import java.util.List;

/**
 * @author Daryl
 * 
 */
public abstract class StackHelper {

	static final String CRLF = "\r\n";

	static StackHelper singleton;

	public static synchronized StackHelper getInstance() {
		if (singleton == null) {
			singleton = new StackHelperJava5();
		}
		return singleton;
	}

	abstract ThreadStacks traceAll();
	abstract ThreadStack traceOne(String targetThreadName);

	/**
	 * Traces all threads except threadNameToSkip, and threadNames only (if not null)
	 * @param threadNameToSkip
	 * @param debugStackTargets
	 * @param filter
	 * @param threadNames
	 * @throws IOException 
	 */
	abstract ThreadStacks traceAllFilteredExcept(String threadNameToSkip, String filter, List<String> threadNames);

	abstract String kill(String threadName);
}
