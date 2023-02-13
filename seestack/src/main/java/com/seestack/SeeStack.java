/**
 * 
 */
package com.seestack;

import java.io.IOException;
import com.seefusion.Util;

/**
 * @author Daryl
 * 
 */
public class SeeStack {

	private final SeeStackInfo infos;
	
	public SeeStack(String configDir, String localPackages) throws IOException {
		this.infos = new SeeStackInfo(configDir, Util.splitCommaDelimString(localPackages));
	}
	
	/**
	 * 
	 * @param configDir directory to load SeeStackInfo.xml from
	 * @param localPackages comma/space delim list of packages that identify "my code"
	 * @param stackParser the implementation of IStackParser responsible for feeding traces
	 * @return threads (with counts) from the stack trace
	 * @throws IOException if we can't read SeeStackInfo.xml
	 */
	public Threads process(IStackParser stackParser) {
		return stackParser.parseStack(infos);
	}


}
