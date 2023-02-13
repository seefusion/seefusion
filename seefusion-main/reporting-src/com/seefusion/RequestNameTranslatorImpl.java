/**
 * 
 */
package com.seefusion;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daryl
 *
 */
class RequestNameTranslatorImpl implements IRequestNameTranslator {

	private List<Pattern> patternList;

	public RequestNameTranslatorImpl(List<Pattern> patternList) {
		this.patternList = patternList;
	}
	
	/* (non-Javadoc)
	 * @see com.seefusion.reporting.IRequestNameTranslator#translateRequestName(com.seefusion.reporting.Request)
	 */
	public String translateRequestName(String uri) {
		uri = uri.toLowerCase();
		for(Pattern pattern : patternList) {
			Matcher matcher = pattern.matcher(uri);
			if(matcher.matches()) {
				return matcher.group(1);
			}
		}

		return null;
	}

}
