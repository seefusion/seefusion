/**
 * 
 */
package com.seefusion;

import java.io.IOException;

/**
 * @author Daryl
 *
 */
abstract class HttpRequestHandler {

	abstract String doGet(HttpTalker talker) throws IOException;

	abstract Perm getPerm();
	
}
