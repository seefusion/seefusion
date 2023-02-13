package com.seefusion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordTest {

	@Test
	public void testBlank() {
		assertEquals("", new Password("").toString());
		assertEquals("", new Password().toString());
		assertEquals("", new Password(null).toString());
	}

	@Test
	public void testHash() {
		Password p = new Password("jjCSpe4F/z0cUpfJap9oC3w1mAotY9JQn0/BlXuADCY=:6305892594403657868");
		assertNotEquals("seefusion", p.toString());
		assertTrue(p.equals("seefusion"));
	}
	
}
