package com.seefusion;

import static org.junit.Assert.*;

import org.junit.Test;

public class CloudWatchHelperLoaderTest {

	@Test
	public void test() throws Exception {
		ClassLoader test = new CloudWatchHelperClassLoaderFactory().getClassLoader();
		Class<?> c = test.loadClass("com.amazonaws.services.cloudwatch.AmazonCloudWatchClient");
		assertEquals(test, c.getClassLoader());
		c = test.loadClass("com.seefusion.CloudWatchHelper");
		assertEquals(test, c.getClassLoader());
	}
	

	@Test
	public void testPushMetric() throws Exception {
		SeeFusion sf = SeeFusion.getInstance();
		Config config = sf.getConfig();
		config.setProperty("awsAccessKey", "------------");
		config.setProperty("awsSecretKey", "------------");
		config.setProperty("cloudwatchEnabled", "true");
		sf.configure();
		Observer<Config> test = sf.getCloudWatchHelper();
		assertTrue(test.getClass().getName() == "com.seefusion.CloudWatchHelper");
	}
}
