package com.seefusion;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SeeDAOTest {

	@Test
	public void testTrunc() {
		assertEquals(null,  SeeDAO.trunc(null, 10));
		assertEquals("test",  SeeDAO.trunc("test", 10));
		assertEquals("areallylon",  SeeDAO.trunc("areallylongstring", 10));
		assertEquals(10, "areallylon".length());
	}

}
