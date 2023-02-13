package com.seefusion;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

public class SeeProfileTest {

	@Test
	public void testCounts() throws Exception {
		final Profile profile = new Profile("testInstance", "testName", 100L, 1000L);
		profile.notifyStarted();
		// these will be 20 threads with two different examples
		for(int i=0; i < 10; ++i) {
			profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		}
		for(int i=0; i < 10; ++i) {
			profile.addSnapshot(new ThreadStack(Thread.currentThread()));
		}
		// each should be a different method fingerprint
		for(int i=0; i < 30; ++i) {
			profile.addSnapshot(getThreadStack2());
		}
		for(int i=0; i < 5; ++i) {
			profile.addSnapshot(getThreadStack3());
		}
		assertEquals(4, profile.getSnapshots().size());
		profile.notifyStopped();
		assertEquals(4, profile.getSnapshots().size());
		LinkedList<Integer> nums = new LinkedList<Integer>();
		nums.add(10);
		nums.add(10);
		nums.add(30);
		nums.add(5);
		for(ThreadStack snapshot : profile.getSnapshots().values()) {
			nums.remove((Object)snapshot.count);
		}
		// check assumptions
		assertTrue("leftover nums: " + nums.toString(), nums.isEmpty());
		
		nums.add(20);
		nums.add(30);
		nums.add(5);
		ProfileAnalysis analysis = new ProfileAnalysis(".", "com.seefusion", profile);
		
		ThreadFingerprintCounts threadCounts = analysis.getThreadCounts();
//		System.out.println(threadCounts.toJson());
		for(ThreadFingerprintCount t : threadCounts.values()) {
//			System.out.println(t.toString());
			int count = t.getCount();
			nums.remove((Object)count);
			if(count==20) {
				assertEquals(2, t.getThreadCounts().size());
			}
		}
		if(!nums.isEmpty()) {
			fail("Did not find nums: " + nums.toString());
		}
		
	}

	private ThreadStack getThreadStack2() {
		StackTraceElement[] foo = Thread.currentThread().getStackTrace();
		return new ThreadStack(Thread.currentThread(), foo);
	}
	
	private ThreadStack getThreadStack3() {
		StackTraceElement[] foo = Thread.currentThread().getStackTrace();
		return new ThreadStack(Thread.currentThread(), foo);
	}
	
}
