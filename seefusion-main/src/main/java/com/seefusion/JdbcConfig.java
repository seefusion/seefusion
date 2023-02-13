/**
 * 
 */
package com.seefusion;

import java.util.AbstractList;
import java.util.LinkedList;

/**
 * @author Daryl
 * 
 */
class JdbcConfig {
	private AbstractList<String> hideParametersKeywordList = null;

	private int rowLimit = 0;

	private boolean rowLimitThrowingException = false;
	
	private int notifyCount = 4000;

	private int remindCount = 4000;
	
	private boolean isConvertingBlobToBytes = false;
	
	void addHideParametersKeyword(String keyword) {
		if(this.hideParametersKeywordList==null) {
			this.hideParametersKeywordList = new LinkedList<String>();
		}
		this.hideParametersKeywordList.add(keyword);
	}

	boolean isHideParametersKeywordListEmpty() {
		return this.hideParametersKeywordList.isEmpty();
	}
	
    boolean checkForHideParameters(String sql) {
        // should we hide parameters?
        if(hideParametersKeywordList != null && !hideParametersKeywordList.isEmpty()) {
            String sqlToLower = sql.toLowerCase();
            for(String keyword : hideParametersKeywordList) {
                if(sqlToLower.indexOf(keyword) != -1) {
                    return true;
                }
            }
        }
        return false;
    }
    
	/**
	 * @return Returns the notifyCount.
	 */
	int getNotifyCount() {
		return this.notifyCount;
	}

	/**
	 * @param notifyCount The notifyCount to set.
	 */
	void setNotifyCount(int notifyCount) {
		this.notifyCount = notifyCount;
	}

	/**
	 * @return Returns the remindCount.
	 */
	int getRemindCount() {
		return this.remindCount;
	}

	/**
	 * @param remindCount The remindCount to set.
	 */
	void setRemindCount(int remindCount) {
		this.remindCount = remindCount;
	}

	/**
	 * @return Returns the rowLimit.
	 */
	int getRowLimit() {
		return this.rowLimit;
	}

	/**
	 * @param rowLimit The rowLimit to set.
	 */
	void setRowLimit(int rowLimit) {
		this.rowLimit = rowLimit;
	}

	/**
	 * @return Returns the rowLimitThrowingException.
	 */
	boolean isRowLimitThrowingException() {
		return this.rowLimitThrowingException;
	}

	/**
	 * @param rowLimitThrowingException The rowLimitThrowingException to set.
	 */
	void setRowLimitThrowingException(boolean rowLimitThrowingException) {
		this.rowLimitThrowingException = rowLimitThrowingException;
	}

	/**
	 * @return the isConvertingBlobToBytes
	 */
	public boolean isConvertingBlobToBytes() {
		return this.isConvertingBlobToBytes;
	}

	/**
	 * @param isConvertingBlobToBytes the isConvertingBlobToBytes to set
	 */
	public void setConvertingBlobToBytes(boolean isConvertingBlobToBytes) {
		this.isConvertingBlobToBytes = isConvertingBlobToBytes;
	}
	
	
	
}
