package com.seestack;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Ignore;
import org.junit.Test;

public class StackAnalyzerTest {

	// the stack trace this used to test against may have contained sensitive data and was removed
	@Test @Ignore
	public void testAnalyzeStacks() throws Exception {
		String locals = "com.neteller";
		SeeStack ss = new SeeStack(".", locals);
		BufferedReader in = new BufferedReader(new FileReader("./src/test/resources/sun/sun1.txt"));
		StackParserSun parser = new StackParserSun(in);
		Threads threads = ss.process(parser);
		assertTrue(threads.size() > 0);
		// System.out.println(threads.toJson().toString(2));
	}

}
