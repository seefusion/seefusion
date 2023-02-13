package com.seefusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.junit.Ignore;
import org.junit.Test;

import mockit.Mocked;
import mockit.StrictExpectations;

public class ProfileTest {

	private static final Logger LOG = Logger.getLogger(ProfileTest.class.getName());

	@Test
	public void testJsonSerialization(@Mocked final ResultSet rs) throws Exception {
		final Profile profile = new Profile("testInstance", "testName", 100L, 1000L);
		profile.notifyStarted();
		profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		assertEquals(5, profile.getSnapshots().size());
		profile.notifyStopped();
		assertEquals(5, profile.getSnapshots().size());
		
		/*
		String id = rs.getString(i++);
		String instanceName = rs.getString(i++);
		String name = rs.getString(i++);
		long startTick = rs.getTimestamp(i++).getTime();
		long scheduledDurationMs = rs.getLong(i++);
		long intervalMs = rs.getLong(i++);
		long actualDurationMs = rs.getLong(i++);
		String threadStacks = rs.getString(i++);
		return new Profile(id, instanceName, name, startTick, scheduledDurationMs, intervalMs, actualDurationMs, threadStacks);
		 */
		new StrictExpectations() {{
			rs.getString(1); result=profile.getId();
			rs.getString(2); result=profile.getInstanceName();
			rs.getString(3); result=profile.getName();
			rs.getTimestamp(4).getTime(); result=profile.getStartTick();
			rs.getLong(5); result=profile.getScheduledDurationMs();
			rs.getLong(6); result=profile.getActualDurationMs();
			rs.getLong(7); result=profile.getIntervalMs();
			rs.getInt(8); result = 5;
			rs.getString(9); result = profile.getThreadStacksJson();
		}};
		
		Profile profile2 = new ProfileDao(null).newInstance(rs);

		assertEquals(profile.getSnapshots().size(), profile2.getSnapshots().size());
		assertEquals(profile.toJson().toString(), profile2.toJson().toString());
		Thread.sleep(1000);
		assertTrue(profile.isComplete());
	}

	@Test
	public void testProfileCounting() throws Exception {
		final Profile profile = new Profile("testInstance", "testName", 100L, 1000L);
		profile.notifyStarted();
		for(int i=0; i < 20; ++i) {
			profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		}
		for(int i=0; i < 30; ++i) {
			profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		}
		for(int i=0; i < 5; ++i) {
			profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		}
		assertEquals(3, profile.getSnapshots().size());
		profile.notifyStopped();
		assertEquals(3, profile.getSnapshots().size());
		LinkedList<Integer> nums = new LinkedList<Integer>();
		nums.add(20);
		nums.add(30);
		nums.add(5);
		for(ThreadStack snapshot : profile.getSnapshots().values()) {
			nums.remove((Object)snapshot.count);
		}
		assertTrue(nums.isEmpty());
		
	}

	@Test
	@Ignore
	/**
	 * used to test the performance ramifications of de-duping snapshots in real
	 * time
	 */
	public void testAddSnapshot() {
		Profile profile = new Profile("instanceName", "name", 100L, 100L * 1000L);
		Thread t = Thread.currentThread();
		long startTick = System.currentTimeMillis();
		for (int i = 0; i < 100 * 1000; i++) {
			profile.addSnapshot(new ThreadStack(t));
		}
		long durationMs = System.currentTimeMillis() - startTick;
		LOG.info("elapsed: " + durationMs + "ms");
		assertEquals(1, profile.getSnapshots().size());
	}

}
