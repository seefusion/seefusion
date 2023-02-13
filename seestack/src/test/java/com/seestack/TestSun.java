/**
 * 
 */
package com.seestack;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Daryl
 *
 */
public class TestSun {
	
	// the stack trace this used to test against may have contained sensitive data and was removed
	@Test @Ignore
	public void testSeefusion1() throws Exception {
		SeeStack ss = new SeeStack(".", null);
		BufferedReader in = new BufferedReader(new FileReader("./src/test/resources/sun/seefusion1.txt"));
		StackParserSun parser = new StackParserSun(in);
		Threads ret = ss.process(parser);
		assertNotEquals(0, ret.size());
	}

	@Test @Ignore
	public void testSun1() throws Exception {
		String fn = "./src/test/resources/sun/sun1.txt";
		SeeStack ss = new SeeStack(".", "com.test");
		BufferedReader in = new BufferedReader(new FileReader(fn));
		StackParserSun parser = new StackParserSun(in);
		Threads ret = ss.process(parser);
		assertNotEquals(0, ret.size());
	}
	
	@Test @Ignore
	public void testFR1() throws Exception {
		String fn = "./src/test/resources/sun/coldfusion-out.log";
		SeeStack ss = new SeeStack(".", "com.test");
		BufferedReader in = new BufferedReader(new FileReader(fn));
		StackParserSun parser = new StackParserSun(in);
		Threads ret = ss.process(parser);
		assertNotEquals(0, ret.size());
	}
}
