package com.seefusion;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.Test;

public class JsonDoGetConfigTest {

	@SuppressWarnings({ "PMD.UnusedPrivateField", "unused" })
	private static final Logger LOG = Logger.getLogger(JsonDoGetConfigTest.class.getName());

	@Test
	public void testDoJsonImpl() throws Exception {
		SeeFusion sf = SeeFusion.getInstance();
		HttpTalker talker = new HttpTalker(sf);
		JSONObject ret = JsonDoGetConfig.doJsonImpl(talker);
		// LOG.info("response: " + ret.toString(2));
		assertEquals("true", ret.getJSONArray("sections").getJSONObject(0)
				.getJSONArray("items").getJSONObject(0).getString("value"));
	}

}
