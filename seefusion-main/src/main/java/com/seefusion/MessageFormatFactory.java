/*
 * MessageFormats.java
 *
 */

package com.seefusion;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Factory for reusing message formats.
 * 
 * @author Daryl
 */
class MessageFormatFactory {

	/**
	 * Properties file containing messages.
	 */
	ResourceBundle resources;

	/**
	 * Map of messagename = MessageFormat
	 */
	HashMap<String, MessageFormat> messageFormats = new HashMap<String, MessageFormat>();

	/**
	 * Creates a new instance of MessageFormats
	 * 
	 * @param resources
	 *            ResourceBundle to make a factory around.
	 */
	public MessageFormatFactory(ResourceBundle resources) {
		this.resources = resources;
	}

	/**
	 * Retrieves message format object from factory
	 * 
	 * @param key
	 *            Name of message to get
	 * @return MessageFormat for that message
	 */
	public MessageFormat getMessageFormat(String key) {
		if (messageFormats.containsKey(key)) {
			return messageFormats.get(key);
		}
		else {
			String msg = resources.getString(key);
			if(msg==null) throw new RuntimeException("no such message: " + key);
			MessageFormat mf = new MessageFormat(msg);
			messageFormats.put(key, mf);
			return mf;
		}
	}

	/**
	 * Get a message from the factory.
	 * 
	 * @param key
	 *            Name of message to fetch
	 * @return Raw message string.
	 */
	public String getMessage(String key) {
		return resources.getString(key);
	}

	/**
	 * Uses message formatter to format the message using the key, object[]
	 * params, and StringBuilder supplied
	 * 
	 * @param key
	 *            Name of message to format with
	 * @param params
	 *            Object[] of params
	 * @param sb
	 *            StringBuilder to append result to
	 * @return Returns same StringBuilder that was passed in
	 */
	public StringBuilder format(String key, Object[] params, StringBuilder sb) {
		if (params == null) {
			sb.append(resources.getString(key));
		}
		else {
			MessageFormat mf = getMessageFormat(key);
			sb.append(mf.format(params));
		}
		return sb;
	}

	/**
	 * Uses message formatter to format the message using the key, object[]
	 * params
	 * 
	 * @param key
	 *            Name of message
	 * @param params
	 *            Object[] params for message
	 * @return Formatted string.
	 */
	public String format(String key, Object[] params) {
		StringBuilder sb = new StringBuilder();
		format(key, params, sb);
		return sb.toString();
	}
}
