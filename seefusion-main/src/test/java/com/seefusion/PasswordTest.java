package com.seefusion;

import static org.junit.Assert.*;

import org.junit.Test;

public class PasswordTest {

	@Test
	public void testSalt() {
		assertNotSame(new Password("salt").toString(), new Password("salt").toString());
	}
	
	@Test
	public void testEquals() {
		Password test = new Password("blah");
		assertEquals(44, test.password.length());
		assertTrue(new Password("blah").equals("blah"));
		assertTrue(new Password("").equals(""));
		assertTrue(new Password(null).equals(""));
		assertTrue(new Password(null).equals(null));
		assertTrue(new Password("").equals(null));
		
	}

	@Test
	public void testCreateFromToString() {
		Password test = new Password("blah");
		String testString = test.toString();
		//System.out.println(testString);
		assertTrue(testString.length() > 44);
		assertTrue(testString.charAt(44) == ':');
		assertEquals(test.salt, testString.substring(45));
		Password test2 = new Password(test.toString());
		assertNotSame(test.salt, test2.salt);
		assertTrue(test2.equals("blah"));
	}
	
}
