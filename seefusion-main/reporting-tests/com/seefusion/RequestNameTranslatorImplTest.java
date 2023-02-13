package com.seefusion;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

public class RequestNameTranslatorImplTest {

	@Test
	public void testTranslateRequestName() {
		List<Pattern> patternList = new LinkedList<Pattern>();
		patternList.add(Pattern.compile(".*[&/?](fuseaction[=/][^&/]+).*", Pattern.CASE_INSENSITIVE )); //fusebox
		patternList.add(Pattern.compile(".*[&/?](event[=/][^&/]+).*", Pattern.CASE_INSENSITIVE )); // mach ii
		patternList.add(Pattern.compile(".*//([^?]+\\.cf[mc]).*", Pattern.CASE_INSENSITIVE )); // generic cf
		patternList.add(Pattern.compile(".*//([^?]+\\.js[fp]).*", Pattern.CASE_INSENSITIVE )); // generic java
		patternList.add(Pattern.compile(".*//([^?]+).*", Pattern.CASE_INSENSITIVE )); // generic page
		RequestNameTranslatorImpl obj = new RequestNameTranslatorImpl(patternList);
		assertEquals("fuseaction=home.home", obj.translateRequestName("http://seefusion.com/index.cfm?fuseaction=home.home"));
		assertEquals("fuseaction/home.home", obj.translateRequestName("http://seefusion.com/index.cfm/fuseaction/home.home/"));
		assertEquals("fuseaction/home.home", obj.translateRequestName("http://seefusion.com/foo/index.cfm/fuseaction/home.home"));
		assertEquals("fuseaction=home.home", obj.translateRequestName("http://seefusion.com/index.cfm?xyz&fuseaction=home.home&bah"));
		assertEquals("seefusion.com/foo/home.cfm", obj.translateRequestName("http://seefusion.com/foo/home.cfm"));
		assertEquals("seefusion.com/foo/home.cfm", obj.translateRequestName("http://seefusion.com/foo/home.cfm?"));
		assertEquals("seefusion.com/foo/home.cfm", obj.translateRequestName("http://seefusion.com/foo/home.cfm/sesurl/foo"));
		assertEquals("event=sayhello", obj.translateRequestName("http://localhost/HelloMachII/index.cfm?event=sayHello"));
	}

}
