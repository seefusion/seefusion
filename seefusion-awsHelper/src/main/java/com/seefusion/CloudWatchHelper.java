package com.seefusion;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

public class CloudWatchHelper implements Observer<Config> {

//	private static final Logger LOG = Logger.getLogger(CloudWatchHelper.class.getName());

	private AmazonCloudWatchClient client;

	CountersHelper helper;

	private CountersHistory historyMinutes;
	
	private String namespace;
	
	public CloudWatchHelper(SeeFusion sf) {
		historyMinutes = sf.getHistoryMinutes();
		sf.getConfig().addObserver(this);
	}

	CloudWatchHelper(boolean isUnitTest) {
	}

	@Override
	/**
	 * Any time the config changes, we want to update the AWS access/secret key
	 */
	public void update(Config config) {
		String cloudwatchEnabled = config.getProperty("cloudwatchEnabled");
		boolean enabled = cloudwatchEnabled != null && Util.parseYesNoParam(cloudwatchEnabled);
		//LOG.info("enabled?" + enabled);
		if(enabled) {
			helper = new CountersHelper(this);
			historyMinutes.addObserver(helper);
			String accessKey = config.getProperty("awsAccessKey");
			String secretKey = config.getProperty("awsSecretKey");
			namespace = config.getProperty("awsNamespace", "com.seefusion");
			if(accessKey == null || secretKey == null) {
				client = new AmazonCloudWatchClient();
			}
			else {
				client = new AmazonCloudWatchClient(new BasicAWSCredentials(accessKey, secretKey));
			}
		}
		else {
			historyMinutes.removeObserver(helper);
			shutdown();
		}
	}

	public void update(Counters counters) {
		if(counters==null) {
			shutdown();
		}
		helper.update(counters);
	}

	public class CountersHelper implements Observer<Counters>, Runnable {
		
		private final Logger LOG = Logger.getLogger(CountersHelper.class.getName());

		boolean isRunning = true;
		Counters latestCounters;
		
		Thread thread;

		private CloudWatchHelper parent;
		
		CountersHelper(CloudWatchHelper parent) {
			this.parent = parent;
			thread = new Thread(this);
			thread.setDaemon(true);
			thread.setName("SeeFusion CloudWatch Statistics Reporter");
			thread.start();
		}
		
		@Override
		public void update(Counters counters) {
			latestCounters = counters;
			synchronized (this) {
				this.notify();
			}
		}

		private PutMetricDataRequest getMetricDataRequest() {
			Date now = new Date();
			List<MetricDatum> data = new LinkedList<MetricDatum>();
			data.add(buildMetricDatum(now, "pages", latestCounters.getPageCount(), StandardUnit.Count));
			data.add(buildMetricDatum(now, "queries", latestCounters.getQueryCount(), StandardUnit.Count));
			data.add(buildMetricDatum(now, "memoryAvailable", latestCounters.getFreeMemory(), StandardUnit.Bytes));
			data.add(buildMetricDatum(now, "memoryUsed", latestCounters.getMaxMemory() - latestCounters.getFreeMemory(), StandardUnit.Bytes));
			data.add(buildMetricDatum(now, "memoryTotal", latestCounters.getMaxMemory(), StandardUnit.Bytes));
			data.add(buildMetricDatum(now, "activeRequests", latestCounters.getActiveRequests(), StandardUnit.Count));
			return new PutMetricDataRequest().withNamespace(namespace).withMetricData(data);
		}

		private MetricDatum buildMetricDatum(Date timestamp, String name, double value, StandardUnit unit) {
			MetricDatum ret = new MetricDatum();
			ret.setTimestamp(timestamp);
			ret.setMetricName(name);
			ret.setValue(value);
			ret.setUnit(unit);
			return ret;
		}

		public void shutdown() {
			isRunning = false;
			synchronized (parent) {
				parent.notify();
			}
		}
		
		@Override
		public void run() {
			while(isRunning) {
				synchronized (this) {
					try {
						this.wait();
					}
					catch (InterruptedException e) {
						Thread.interrupted();
					}
				}
				if(client != null && latestCounters != null) {
					// LOG.info("Reporting stats to CloudWatch...");
					long startTick = System.currentTimeMillis();
					client.putMetricData(getMetricDataRequest());
					long dur = System.currentTimeMillis() - startTick;
					LOG.fine("Reporting stats to CloudWatch took " + dur + "ms");
					synchronized (latestCounters) {
						// used by unit test
						latestCounters.notify();
					}
					latestCounters = null;
				}
			}
		}
	}

	public void shutdown() {
		if(helper != null) {
			helper.shutdown();
		}
		if(historyMinutes != null) {
			historyMinutes.removeObserver(helper);
			historyMinutes = null;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		shutdown();
		super.finalize();
	}

}
