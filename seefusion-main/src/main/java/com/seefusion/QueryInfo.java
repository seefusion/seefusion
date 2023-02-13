/*
 * QueryInfo.java
 *
 */

package com.seefusion;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

/**
 * 
 */
class QueryInfo extends DaoObjectImpl implements Cloneable, DaoObject {
	// simple properties
	final String id;

	String queryText = null;

	long startTick = 0;

	boolean isActive = false;
	boolean rsActive = false;

	long endTick = 0;

	long simpleQueryMs;

	String exceptionText = null;

	boolean killed = false;

	boolean isDebugOutputMode = false;

	String stack;

	private boolean isSimpleQuery = false;

	static final boolean IS_SIMPLE_QUERY = true;

	static final boolean IS_NOT_SIMPLE_QUERY = false;

	// non-simple properties
	LinkedList<String> parameters;

	private StatementImpl statement;

	private ResultSetImpl resultSet;

	Properties sfInfo;

	SeeFusion sf;

	List<String> debugStackTargets;

	RequestInfo pi;

	private String instanceName;

	private int resultCount;

	@Override
	public Object clone() {
		QueryInfo ret = null;
		try {
			ret = (QueryInfo) super.clone();
			ret.parameters = new LinkedList<String>(parameters);
			ret.statement = null;
			ret.sfInfo = sfInfo;
			ret.sf = sf;
			ret.debugStackTargets = this.debugStackTargets;
			ret.pi = pi;
			ret.resultCount = resultCount;
		}
		catch (CloneNotSupportedException e) {
			// should never happen
		}
		return ret;
	}

	long getEndTime() {
		if (endTick == 0) {
			endTick = System.currentTimeMillis();
		}
		return endTick;
	}

	long getBeginTime() {
		return startTick;
	}

	boolean kill() {
		return kill(null);
	}

	boolean kill(String s) {
		killed = true;
		if (isActive) {
			return statement.kill(s);
		}
		return false;
	}

	String getStack() {
		if (stack == null) {
			stack = Util.recordStack(debugStackTargets);
		}
		return stack;
	}

	QueryInfo() {
		this.id = UUID.randomUUID().toString();
	}

	QueryInfo(String id) {
		this.id = id;
	}

	/**
	 * Creates a new instance of QueryInfo
	 * 
	 * @param queryText
	 *            text of the query being run.
	 */
	QueryInfo(StatementImpl statement, RequestInfo pi, List<String> debugStackTargets) {
		this();
		this.statement = statement;
		this.sfInfo = statement.getSfInfo();
		this.debugStackTargets = debugStackTargets;
		this.parameters = new LinkedList<String>();
		this.pi = pi;
	}

	QueryInfo(StatementImpl statement, RequestInfo pi, List<String> debugStackTargets, QueryInfo qi) {
		this();
		this.statement = statement;
		this.queryText = qi.queryText;
		this.sfInfo = qi.sfInfo;
		this.debugStackTargets = debugStackTargets;
		this.parameters = new LinkedList<String>();
		this.pi = pi;
	}

	void setInactive() throws SQLException {
		try {
			// debug("SetInactive()");
			if (this.isActive || this.rsActive) {
				// debug("SetInactive()2");
				if (this.isActive) {
					this.endTick = System.currentTimeMillis();
					this.isActive = false;
				}
				if (pi != null) {
					// debug("SetInactive()3");
					if (this.isSimpleQuery) {
						if (resultSet != null && !resultSet.isActive) {
							// debug("SetInactive()4");
							pi.addQueryTime(this);
							this.rsActive = false;
						}
					}
					else if (resultSet != null) {
						// debug("SetInactive()5");
						pi.addResultSetTime(resultSet);
						this.rsActive = false;
					}
					else {
						// debug("SetInactive()6");
						pi.addQueryTime(this);
						this.rsActive = false;
					}
				}
			}
			if (killed) {
				this.statement.throwSQLException();
			}
		}
		finally {
			removeReferences();
		}
	}

	void removeReferences() {
		this.statement = null;
		this.resultCount = resultSet==null ? 0 : resultSet.sfGetRowCount();
		this.resultSet = null;
	}

	void setActive() throws SQLException {
		// debug("SetActive()");
		if (!isActive) {
			this.exceptionText = null;
			this.resultSet = null;
			this.startTick = System.currentTimeMillis();
			this.endTick = 0;
			this.isActive = true;
			if (killed) {
				this.statement.throwSQLException();
			}
		}
	}

	void setResultSetActive(ResultSetImpl rs) throws SQLException {
		this.resultSet = rs;
		if (killed) {
			this.statement.throwSQLException();
		}
	}

	boolean isActive() {
		return isActive;
	}

	long getElapsedTime() {
		if (resultSet != null) {
			return getQueryElapsedTime() + getResultSetElapsedTime();
		}
		else {
			return getQueryElapsedTime();
		}
	}

	long getResultSetElapsedTime() {
		if (resultSet != null) {
			return resultSet.sfGetElapsedTime();
		}
		else {
			return -1;
		}
	}

	long getQueryElapsedTime() {
		if (startTick == 0) {
			return -2;
		}
		else if (endTick == 0) {
			return System.currentTimeMillis() - startTick;
		}
		else {
			return endTick - startTick;
		}
	}

	void addParameter(String parameter) {
		// System.out.println("AddParameter: "+parameter);
		parameters.addLast(parameter);
	}

	/**
	 * Getter for property queryText. Includes parameters and exceptions, if
	 * any.
	 * 
	 * @return Value of property queryText.
	 */
	String getQueryText() {
		// if a statement is created but never used, queryText will be null.
		if (queryText == null) {
			return "(Statement closed without being used for a query.)";
		}
		StringBuilder sb = new StringBuilder(1000);
		sb.append(queryText);
		sb.append(getQueryParamsOnly());
		if (sfInfo == null || sfInfo.isEmpty()) {
			// nop
		}
		else {
			sb.append("\r\n").append(sfInfo.toString());
		}
		if (exceptionText != null) {
			sb.append("\r\n").append(exceptionText);
		}
		return sb.toString();
	}

	void setException(String exceptionText) {
		this.exceptionText = exceptionText;
	}

	/**
	 * Get query text and exception, if any. Does not include parameters.
	 * 
	 * @return query text
	 */
	String getQueryTextOnly() {
		StringBuilder sb = new StringBuilder(1000);
		sb.append(queryText);
		if (sfInfo == null || sfInfo.isEmpty()) {
			// nop
		}
		else {
			sb.append(sfInfo.toString()).append("\r\n");
		}
		if (exceptionText != null) {
			sb.append("\r\n").append(exceptionText);
		}
		return sb.toString();
	}

	String queryParamsOnly;

	String getQueryParamsOnly() {
		if (queryParamsOnly == null) {
			if (parameters == null || parameters.isEmpty()) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			Iterator<String> iter = parameters.iterator();
			while (iter.hasNext()) {
				sb.append("\r\n").append(Util.xmlStringFormat(iter.next()));
			}
			queryParamsOnly = sb.toString();
		}
		return queryParamsOnly;
	}

	boolean hasException() {
		return (exceptionText != null) && (exceptionText.length() > 0);
	}

	void clearParameters() {
		// System.out.println("Parameters Cleared.");
		parameters = new LinkedList<String>();
	}

	/**
	 * Setter for property queryText.
	 * 
	 * @param queryText
	 *            New value of property queryText.
	 */
	void setQueryText(java.lang.String queryText) {
		if (queryText != null) {
			this.queryText = queryText.replace('\t', ' ');
		}
	}

	/**
	 * Getter for property resultCount.
	 * 
	 * @return Value of property resultCount.
	 */
	int getResultCount() {
		if (resultSet != null) {
			return resultSet.sfGetRowCount();
		}
		else {
			return resultCount;
		}
	}

	Properties getSfInfo() {
		return sfInfo;
	}

	/**
	 * @return the isSimpleQuery
	 */
	boolean isSimpleQuery() {
		return this.isSimpleQuery;
	}

	/**
	 * @param isSimpleQuery
	 *            the isSimpleQuery to set
	 */
	void setSimpleQuery(boolean isSimpleQuery) {
		// debug("setSimpleQuery(" + isSimpleQuery + ")");
		this.isSimpleQuery = isSimpleQuery;
	}

	/**
	 * @return the resultSet
	 */
	ResultSetImpl getResultSet() {
		return this.resultSet;
	}

	/**
	 * @param resultSet
	 *            the resultSet to set
	 */
	void setResultSet(ResultSetImpl resultSet) {
		rsActive = true;
		this.resultSet = resultSet;
	}

	@Override
	public JSONObject toJson() {
		JSONObject ret = new JSONObject();
		/*
	static final String ID = "QueryId";
	static final String INSTANCE_NAME = "InstanceName";
	static final String BEGIN_TIME = "BeginTime";
	static final String END_TIME = "EndTime";
	static final String ELAPSED_TIME = "ElapsedTimeMs";
	static final String RESULT_COUNT = "ResultCount";
	static final String QUERY_TEXT = "QueryText";
	static final String QUERY_PARAMS = "QueryParams";
	static final String STACK = "StackTrace";
		 */
		ret.put("id", id);
		ret.put("instancename", instanceName);
		ret.put("begintime", startTick);
		ret.put("endtime", endTick);
		ret.put("elapsedtimems", endTick-startTick);
		ret.put("resultcount", getResultCount());
		ret.put("querytext", getQueryTextOnly());
		ret.put("queryparams", getQueryParamsOnly());
		ret.put("stacktrace", stack);
		return ret;
	}

	@Override
	public String getId() {
		return id;
	}

	void setStartTick(long time) {
		this.startTick = time;
	}

	void setEndTick(long time) {
		this.endTick = time;
	}

	void setInstanceName(String name) {
		this.instanceName = name;
	}

	String getInstanceName() {
		return instanceName;
	}

	public void setResultCount(int count) {
		this.resultCount = count;
	}

	public void setQueryParams(String string) {
		this.queryParamsOnly = string;
	}

	public void setStack(String stack) {
		this.stack = stack;
	}

}
