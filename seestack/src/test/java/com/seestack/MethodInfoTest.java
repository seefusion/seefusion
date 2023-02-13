package com.seestack;

import org.junit.Test;
import static org.junit.Assert.*;

public class MethodInfoTest {

	@Test
	public void testMethodInfo1() throws Exception {
		SeeStackInfo info = new SeeStackInfo(".", null);
		String rawLine = "coldfusion.runtime.TemplateClassLoader.findClass(TemplateClassLoader.java:476)";
		String method = "coldfusion.runtime.TemplateClassLoader.findClass";
		Method m = new Method(rawLine, method, "TemplateClassLoader.java:476", info);
		assertEquals(ImportanceEnum.HIGH, m.importance);
	}
	
}
