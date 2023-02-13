package com.seefusion;

import static org.junit.Assert.*;

import java.sql.Connection;

import org.junit.Test;

public class DbLoggerConnectionPoolTest {

	@Test
	public void testValidate() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		DbLoggerConnectionPool pool = new DbLoggerConnectionPool("jdbc:derby:memory:test;create=true", null, null);
		Connection c1 = pool.getConnection();
		Connection c2 = pool.getConnection();
		assertNotSame(c1, c2);
		c1.close();
		assertTrue(c1.isValid(100));
		assertTrue(pool.pool.contains(c1));
	}

	@Test
	public void testExpires() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		DbLoggerConnectionPool pool = new DbLoggerConnectionPool("jdbc:derby:memory:test;create=true", null, null);
		Connection c1 = pool.getConnection();
		DbLoggerPooledConnection c = (DbLoggerPooledConnection)c1;
		c.setCreationTick( System.currentTimeMillis() - (60L * 60L * 1000L) );
		c1.close();
		assertFalse(pool.pool.contains(c1));
		Thread.sleep(100);
		assertTrue(c1.isClosed());
	}

}
