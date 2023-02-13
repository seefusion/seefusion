/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 * 
 */
class DebugMessage {

	private RequestInfo pi;

	static final int DEBUGTYPE_QUERY = 0;

	static final int DEBUGTYPE_TRACE = 1;

	static final int DEBUGTYPE_PAGEBOUNDARY = 2;

	private int debugType;

	private String title;

	private String content;

	boolean useCachedString = false;

	String toStringCache;

	DebugMessage(RequestInfo pi, int debugType, String title, String content) {
		this.pi = pi;
		this.debugType = debugType;
		this.title = title;
		this.content = content;
	}

	public String toString() {
		if (!useCachedString) {
			StringBuilder buf = new StringBuilder(1000);
			buf.append("<p>");
			if (debugType != DEBUGTYPE_PAGEBOUNDARY) {
				buf.append(pi.getPageURL());
				buf.append('@').append(pi.getElapsedTime()).append("ms:\r\n<pre>");
			}
			buf.append("<b>").append(Util.xmlStringFormat(title)).append("</b>");
			if (debugType != DEBUGTYPE_PAGEBOUNDARY) {
				buf.append("</pre>");
			}
			if (content != null) {
				buf.append("<pre>").append(Util.xmlStringFormat(content)).append("</pre>");
			}
			buf.append("</p>\r\n");
			toStringCache = buf.toString();
			useCachedString = true;
		}
		return toStringCache;
	}

	/**
	 * @return Returns the content.
	 */
	String getContent() {
		return this.content;
	}

	/**
	 * @param content
	 *            The content to set.
	 */
	void setContent(String content) {
		this.content = content;
		useCachedString = false;
	}

	/**
	 * @return Returns the debugType.
	 */
	int getDebugType() {
		return this.debugType;
	}

	/**
	 * @param debugType
	 *            The debugType to set.
	 */
	void setDebugType(int debugType) {
		this.debugType = debugType;
	}

	/**
	 * @return Returns the pi.
	 */
	RequestInfo getPi() {
		return this.pi;
	}

	/**
	 * @param pi
	 *            The pi to set.
	 */
	void setPi(RequestInfo pi) {
		this.pi = pi;
		useCachedString = false;
	}

	/**
	 * @return Returns the title.
	 */
	String getTitle() {
		return this.title;
	}

	/**
	 * @param title
	 *            The title to set.
	 */
	void setTitle(String title) {
		this.title = title;
		useCachedString = false;
	}
}
