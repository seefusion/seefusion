/*
 * HistorySecond.java
 *
 * Created on September 17, 2004, 12:24 PM
 */

package com.seefusion;

/**
 *
 * @author  Daryl
 */
class HistorySecond {
    
    public long second;
    public long totalPageTimeMs;
    public int pageCount;
    public long totalQueryTimeMs;
    public int queryCount;
    public double loadAverage;
    
    /** Creates a new instance of HistorySecond */
    public HistorySecond(long second, int pageCount, long totalPageTimeMs, int queryCount, long totalQueryTimeMs, double loadAverage) {
        this.second = second;
        this.pageCount = pageCount;
        this.totalPageTimeMs = totalPageTimeMs;
        this.queryCount = queryCount;
        this.totalQueryTimeMs = totalQueryTimeMs;
        this.loadAverage = loadAverage;
    }
    
    /*
    double avgPageTimeMs = -1;
    public double getAvgPageTimeMs() {
        if(avgPageTimeMs == -1) {
            avgPageTimeMs = (double)totalPageTimeMs / (double)pageCount;
        }
        return avgPageTimeMs;
    }
     */
    
}
