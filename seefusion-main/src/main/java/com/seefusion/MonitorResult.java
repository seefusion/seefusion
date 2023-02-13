/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
public class MonitorResult {

	public static final int STATUS_OK = 1;
	public static final int STATUS_WARN = 2;
	public static final int STATUS_FAIL = 3;
	
	int status;
	String detail;
	
	public MonitorResult(int status, String detail) {
		this.status = status;
		this.detail = detail;
	}
	
	
	/**
	 * @return Returns the detail.
	 */
	String getDetail() {
		return this.detail;
	}
	/**
	 * @param detail The detail to set.
	 */
	void setDetail(String detail) {
		this.detail = detail;
	}
	/**
	 * @return Returns the status.
	 */
	int getStatus() {
		return this.status;
	}
	/**
	 * @param status The status to set.
	 */
	void setStatus(int status) {
		this.status = status;
	}
	
	
	
}
