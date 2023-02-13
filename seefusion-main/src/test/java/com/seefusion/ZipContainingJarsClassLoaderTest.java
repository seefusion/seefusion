package com.seefusion;

import static org.junit.Assert.*;
import java.io.File;

import org.junit.Test;

public class ZipContainingJarsClassLoaderTest {

	@Test
	public void testRecursiveLoad() throws Exception {
		ZipContainingJarsClassLoader test = new ZipContainingJarsClassLoader(getClass().getClassLoader(), new File("src/test/resources/testcomponents.zip"));
		Class<?> ret = test.loadClass("net.darylb.test.testcomponent.ComponentImpl");
		assertTrue(ret.newInstance().toString().contains("Hello"));
	}

	
}
