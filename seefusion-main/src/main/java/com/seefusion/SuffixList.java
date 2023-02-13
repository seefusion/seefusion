/**
 * 
 */
package com.seefusion;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author Daryl
 *
 */
class SuffixList {

	Set<String> suffixes;
	static final String DELIM_LIST = SeeFusion.DELIM_LIST;
	
	SuffixList( String list ) {
		setSuffixes(list);
	}
	
	SuffixList() {
		//zzzz
	}
	
	boolean hasMatch(String s) {
		if (suffixes == null) {
			return false;
		}
		for (String curSuffix : suffixes) {
			if (curSuffix.equals(".")) {
				int slashPos = s.lastIndexOf('/');
				if (slashPos == s.length() - 1) {
					return true;
				}
				else if (slashPos == -1) {
					if (s.indexOf('.') == -1) {
						return true;
					}
				}
				else {
					if (s.indexOf('.', slashPos) == -1) {
						return true;
					}
				}
			}
			else if (s.toLowerCase().endsWith(curSuffix)) {
				return true;
			}
		}
		return false;
	}

	void setSuffixes(String suffixList) {
		if (suffixList != null) {
			StringTokenizer st = new StringTokenizer(suffixList, DELIM_LIST);
			if (st.hasMoreTokens()) {
				suffixes = new HashSet<String>();
				while (st.hasMoreTokens()) {
					suffixes.add(st.nextToken().toLowerCase());
				}
			}
		}
	}
}
