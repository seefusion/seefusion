/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 *
 */
public class QueryTracker implements Comparable<QueryTracker> {

	public Query longestQuery;
	public Query averageQuery;
	
	public String summary;
	public long sumTime = 0;
	public long sumTimeSquared = 0;
	public long count = 0;
	public long averageTime = 0;
	
	QueryTracker(String summary, Query q) {
		this.summary = summary;
		this.longestQuery = q;
		this.averageQuery = q;
		add(q);
	}
	
	void add(Query q) {
		count++;
		sumTime += q.queryTime;
		sumTimeSquared += q.queryTime * q.queryTime;
		averageTime = sumTime / count;
		if(q.queryTime > this.longestQuery.queryTime) {
			this.longestQuery = q;
		}
		if( Math.abs(q.queryTime - averageTime) < Math.abs(this.averageQuery.queryTime - averageTime) ) {
			this.averageQuery = q;
		}
	}
	
	public long getStdDev() {
		long variance = (sumTimeSquared - (sumTime * averageTime)) / count;
		return (long)Math.sqrt(0.0 + variance);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(QueryTracker obj) {
		return (int)(sumTime - obj.sumTime);
	}
	
	public String toString() {
		StringBuilder ret = new StringBuilder(500);
		ret.append("Summary: ").append(summary).append("\r\n");
		ret.append("  Count: ").append(count).append("\r\n");
		ret.append("  Total Time: ").append(sumTime).append("\r\n");
		ret.append("  Longest Time: ").append(longestQuery.queryTime).append("\r\n");
		ret.append("  Average Time: ").append(averageTime).append("\r\n");
		ret.append("  Standard Deviation of Time: ").append(getStdDev()).append("\r\n");
		return ret.toString();
	}
	
}
