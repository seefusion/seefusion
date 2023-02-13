/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
public class RequestTracker implements Comparable<RequestTracker> {

	private static final String CRLF = "\r\n";
	public Request longestRequest;
	public Request averageRequest;
	
	public String summary;
	public long sumTime = 0;
	public long sumTimeSquared = 0;
	public long count = 0;
	public long averageTime = 0;
		
	RequestTracker(String summary, Request req) {
		this.summary = summary;
		this.longestRequest = req;
		this.averageRequest = req;
		add(req);
	}
	
	void add(Request req) {
		count++;
		sumTime += req.timeToFirstByteMs;
		sumTimeSquared += req.timeToFirstByteMs * req.timeToFirstByteMs;
		averageTime = sumTime / count;
		if(req.timeToFirstByteMs > this.longestRequest.timeToFirstByteMs) {
			this.longestRequest = req;
		}
		if( Math.abs(req.timeToFirstByteMs - averageTime) < Math.abs(this.averageRequest.timeToFirstByteMs - averageTime) ) {
			this.averageRequest = req;
		}
	}
	
	public long getStdDev() {
		long variance = (sumTimeSquared - (sumTime * averageTime)) / count;
		return (long)Math.sqrt(0.0 + variance);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(RequestTracker obj) {
		return (int)(sumTime - obj.sumTime);
	}
	
	public String toString() {
		StringBuilder ret = new StringBuilder(500);
		ret.append("Summary: ").append(summary).append(CRLF);
		ret.append("  Count: ").append(count).append(CRLF);
		ret.append("  Total Time: ").append(sumTime).append(CRLF);
		ret.append("  Longest Time to First Byte: ").append(longestRequest.timeToFirstByteMs).append(CRLF);
		ret.append("  Average Time: ").append(averageTime).append(CRLF);
		ret.append("  Standard Deviation of Time: ").append(getStdDev()).append(CRLF);
		return ret.toString();
	}
	
}
