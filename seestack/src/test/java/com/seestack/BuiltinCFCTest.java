package com.seestack;

import org.junit.Test;
import static org.junit.Assert.*;

import com.seefusion.SimpleXml;
import com.seefusion.SimpleXmlParser;
import com.seefusion.Util;

public class BuiltinCFCTest {

	@Test
	public void test() throws Exception {
		String defs = Util.readFile("./src/main/resources/SeeStackInfo.xml");
		SimpleXml doc = SimpleXmlParser.parseXml(defs);
		SeeStackInfo info = new SeeStackInfo(doc, new String[0]);
		
		SeeThread curThread = new SeeThread("jrpp-1", "thread jrpp-1", info.findThreadInfo("jrpp-1"));
		
		StackTraceElement e;
		e = new StackTraceElement("com.adobe.coldfusion.query", "execute", "query.cfc", 0);
		curThread.addMethod(new Method(e.toString(), e.getClassName() + "." + e.getMethodName(), e.getFileName() + ":" + e.getLineNumber(), info));
		assertEquals(curThread.getImportance(), ImportanceEnum.MEDIUM);
		e = new StackTraceElement("d:\\www\\somesite\\mydao.cfc", "getSomething", "mydao.cfc", 10);
		curThread.addMethod(new Method(e.toString(), e.getClassName() + "." + e.getMethodName(), e.getFileName() + ":" + e.getLineNumber(), info));
		assertEquals(curThread.getImportance(), ImportanceEnum.USER);
	}
	
}
