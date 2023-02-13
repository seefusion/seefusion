package com.seefusion;

import static org.junit.Assert.assertTrue;

import java.sql.Date;

import org.junit.Test;

public class CloudWatchHelperTest {

	@Test
	public void testPushMetric() throws InterruptedException {
		// Counters counters = new Counters("", new Date(), "foo", 60, 100, 100000, 1000, 5000, 123456L, 1000000L, 30000L, 3);
		Counters counters = new Counters("", new Date(System.currentTimeMillis()), "foo", 60, 100, 100000L, 1000, 5000L, 123456L, 1000000L, 30000L, 3);
		SeeFusion sf = SeeFusion.getInstance();
		Config config = sf.getConfig();
		config.setProperty("awsAccessKey", "-----------");
		config.setProperty("awsSecretKey", "-----------");
		config.setProperty("cloudwatchEnabled", "true");
		sf.configure();
		long startTick = System.currentTimeMillis();
		CloudWatchHelper test = (CloudWatchHelper)sf.cloudwatchHelper;
		test.update(counters);
 		int tries = 0;
		while(tries <= 100) {
			tries++;
			Thread.sleep(50L);
			if(test.helper.latestCounters == null)
				break;
		}
		// if this succeeds it should take at least 100ms
		assertTrue(tries < 100 && System.currentTimeMillis() > startTick + 100);
	}

}
