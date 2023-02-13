package com.seefusion;

import static org.junit.Assert.*;
import org.junit.Test;

public class PermTest {

	@Test
	public void testMayI() {
		Perm userPerm = new Perm(Perm.LOGGEDIN);
		Perm reqPerm = new Perm(Perm.LOGGEDIN, Perm.CONFIG);
		assertFalse(userPerm.mayI(reqPerm));
		reqPerm = new Perm(Perm.LOGGEDIN);
		assertTrue(userPerm.mayI(reqPerm));
		Perm noPerm = new Perm(Perm.NONE);
		assertFalse(noPerm.mayI(reqPerm));
	}

	@Test
	public void testWhyCantI() {
		Perm userPerm = new Perm(Perm.LOGGEDIN);
		Perm reqPerm = new Perm(Perm.LOGGEDIN, Perm.CONFIG);
		Perm expected = new Perm(Perm.CONFIG);
		Perm actual = userPerm.whyCantI(reqPerm);
		assertEquals(actual.perm, expected.perm);
	}

	@Test
	public void testHas() {
		Perm userPerm = new Perm(Perm.LOGGEDIN);
		assertTrue(userPerm.has(Perm.LOGGEDIN));
		assertFalse(userPerm.has(Perm.CONFIG));
		assertFalse(userPerm.has(Perm.KILL));
	}

	@Test
	public void testAdd() {
		Perm userPerm = new Perm(Perm.LOGGEDIN);
		assertFalse(userPerm.has(Perm.KILL));
		userPerm.add(Perm.KILL);
		assertTrue(userPerm.has(Perm.KILL));
	}
	
	@Test
	public void testEquals() {
		Perm loggedIn = new Perm(Perm.LOGGEDIN);
		assertFalse(loggedIn.equals(Perm.NONE));
		assertTrue(loggedIn.equals(Perm.LOGGEDIN));
		assertTrue(loggedIn.equals(loggedIn));
	}

}
