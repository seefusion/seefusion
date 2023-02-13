package com.seefusion;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class SeeDAO<T extends DaoObject> {

	private static final Logger LOG = Logger.getLogger(SeeDAO.class.getName());

	static final String CRLF = "\r\n";
	private DbLoggerConnectionPool pool;
	private final Cache<T> cache;

	SeeDAO(DbLoggerConnectionPool pool, int maxInMemoryCount) {
		cache = new Cache<T>(maxInMemoryCount);
		setConnectionPool(pool);
	}

	void setConnectionPool(DbLoggerConnectionPool pool) {
		this.pool = pool;
		if(pool != null && ensureTableExists()) {
			for (T t : cache.values()) {
				try {
					if(!t.isPersisted()) {
						insert(t);
					}
				}
				catch (SQLException e) {
					LOG.log(Level.SEVERE, "Unable to insert into " + this.getTableName() + " for id=" + t.getId(), e);
				}
			}
			this.pool = pool;
		}
		else {
			this.pool = null;
		}
	}

	abstract String getTableName();

	JSONArray list() throws SQLException {
		if(pool != null) {
			return listFromDb();
		}
		else {
			return listFromMemory();
		}
	}

	protected JSONArray listFromMemory() {
		return listFromMemory(null, null);
	}

	protected JSONArray listFromMemory(String columnName, String value) {
		synchronized (cache) {
			LOG.fine("SeeDAO: checking " + cache.size() + " object(s) in " + this.getClass().getSimpleName()
					+ " memory");
			JSONArray ret = new JSONArray();
			for (T t : cache.values()) {
				JSONObject o = t.toJson();
				if(columnName == null || o.has(columnName) && o.get(columnName).toString().equals(value)) {
					ret.put(t.toJson());
				}
			}
			LOG.fine("SeeDAO: " + this.getClass().getSimpleName() + " found " + ret.length() + " object(s)");
			return ret;
		}
	}

	protected JSONArray listFromDb() throws SQLException {
		return listFromDb(null, null);
	}

	protected JSONArray listFromDb(String columnName, String value) throws SQLException {
		LOG.fine("SeeDAO: listing " + this.getClass().getSimpleName() + " objects from db");
		JSONArray ret = new JSONArray();
		Connection c = getConnection();
		if(c == null) {
			return ret;
		}
		try {
			Statement s;
			ResultSet rs;
			if(columnName == null) {
				s = c.createStatement();
				rs = s.executeQuery(getListStatment());
			}
			else {
				String sql = getListStatment("WHERE " + columnName + " = ?");
				LOG.fine("SeeDAO: " + sql);
				PreparedStatement ps = c.prepareStatement(sql);
				ps.setString(1, value);
				rs = ps.executeQuery();
				s = ps;
			}
			while (rs.next()) {
				ret.put(newInstance(rs).toJson());
			}
			rs.close();
			s.close();
			LOG.fine("SeeDAO: " + ret.length() + " found");
			return ret;
		}
		finally {
			c.close();
		}
	}

	protected abstract T newInstance(ResultSet rs) throws SQLException;

	protected String getLoadStatement() {
		StringBuilder ret = new StringBuilder(300);
		ret.append(getListStatment("where " + getIdColumnName() + "=?"));
		return ret.toString();
	}

	private String getIdColumnName() {
		return getColumns().get(0).getName();
	}

	protected PreparedStatement prepareLoad(Connection c) throws SQLException {
		String stmt = getLoadStatement();
		// Logger.debug("SeeDao::prepareLoad " + stmt);
		return c.prepareStatement(stmt);
	}

	protected String getListStatment() {
		return getListStatment(null);
	}

	protected String getListStatment(String whereClause) {
		DbLoggerDialect dialect = pool.getDialect();
		StringBuilder ret = new StringBuilder(300);
		ret.append("select ");
		ret.append(dialect.top(100)).append(' ');
		String comma = "";
		for (DbLoggerColumn col : getColumns()) {
			ret.append(comma).append(col.getName()).append(CRLF);
			comma = ",";
		}
		ret.append(" from ").append(getTableName()).append(CRLF);
		String s = dialect.rownumLte(100);
		if(whereClause != null) {
			ret.append(whereClause).append(CRLF);
			if(!s.isEmpty()) {
				ret.append("AND ").append(s).append(CRLF);
			}
		}
		else {
			if(!s.isEmpty()) {
				ret.append("WHERE ").append(s).append(CRLF);
			}
		}
		String orderBy = getOrderBy();
		if(orderBy != null) {
			ret.append(CRLF).append("order by ").append(orderBy);
		}
		ret.append(dialect.limit(100));
		return ret.toString();
	}

	protected String getOrderBy() {
		return null;
	}

	protected String getInsertStatement() {
		StringBuilder ret = new StringBuilder(300);
		ret.append("insert into ").append(getTableName()).append(" (\r\n");
		String comma = "";
		StringBuilder placeholders = new StringBuilder();
		for (DbLoggerColumn col : getColumns()) {
			ret.append(comma).append(col.getName()).append("\r\n");
			placeholders.append(comma).append('?');
			comma = ",";
		}
		ret.append(") values (").append(placeholders).append(')');
		return ret.toString();
	}

	protected PreparedStatement prepareInsert(Connection c) throws SQLException {
		return c.prepareStatement(getInsertStatement());
	}

	protected Connection getConnection() throws SQLException {
		if(pool != null) {
			return pool.getConnection();
		}
		else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	boolean insert(DaoObject obj) throws SQLException {
		LOG.fine("SeeDAO: adding " + obj.getClass().getSimpleName() + " " + obj.getId() + " to cache");
		synchronized (cache) {
			cache.put(obj.getId(), (T) obj);
		}
		if(pool != null) {
			return insertToDb((T) obj);
		}
		return true;
	}

	protected boolean insertToDb(T obj) throws SQLException {
		// Logger.debug("SeeDAO::insertToDb Logging ", obj, new Exception());
		if(obj.isPersisted()) {
			return true;
		}
		boolean ret;
		Connection c = getConnection();
		if(c == null) {
			return false;
		}
		try {
			PreparedStatement preparedInsert = prepareInsert(c);
			ret = insert(preparedInsert, obj);
			if(ret) {
				obj.setPersisted(true);
			}
		}
		finally {
			c.close();
		}
		return ret;
	}

	protected boolean insert(PreparedStatement insertStatement, T obj) throws SQLException {
		int i = 1;
		int clobMaxLength = pool.getDialect().getClobMaxLength();
		int varcharMaxLength = pool.getDialect().getVarcharMaxLength(); 
		// Logger.debug("SeeDAO Inserting " + obj.getClass().getName() + " " +
		// obj);
		for (DbLoggerColumn col : getColumns()) {
			String val;
			switch (col.getType()) {
			case VARCHAR:
				val = trunc(getStringValue(obj, col), varcharMaxLength);
				insertStatement.setString(i++, val);
				break;
			case CLOB:
				val = trunc(getStringValue(obj, col), clobMaxLength);
				insertStatement.setString(i++, val);
				// Logger.debug("Column " + col.getName() + " with value " +
				// val);
				break;
			case DATETIME:
				insertStatement.setTimestamp(i++, new Timestamp(getDateValue(obj, col).getTime()));
				break;
			case INT:
				insertStatement.setInt(i++, getIntValue(obj, col));
				break;
			case LONG:
				insertStatement.setLong(i++, getLongValue(obj, col));
				break;
			default:
				throw new IllegalArgumentException("Unknown type: " + col.getType().toString());
			}
		}
		boolean ret = insertStatement.execute();
		insertStatement.close();
		return ret;
	}

	static String trunc(String stringValue, int maxLength) {
		if(stringValue==null || stringValue.length() < maxLength) {
			return stringValue;
		}
		return stringValue.substring(0, maxLength);
	}

	abstract long getLongValue(T obj, DbLoggerColumn col);

	abstract int getIntValue(T obj, DbLoggerColumn col);

	abstract String getStringValue(T obj, DbLoggerColumn col);

	abstract Date getDateValue(T obj, DbLoggerColumn col);

	T getById(String id) throws SQLException {
		if(cache.containsKey(id)) {
			return cache.get(id);
		}
		else if(pool != null) {
			Connection c = getConnection();
			if(c == null)
				return null;
			ResultSet rs = null;
			try {
				PreparedStatement preparedLoad = prepareLoad(c);
				preparedLoad.setString(1, id);
				rs = preparedLoad.executeQuery();
				if(rs.next()) {
					return newInstance(rs);
				}
				else {
					return null;
				}
			}
			finally {
				if(rs != null) {
					rs.close();
				}
				c.close();
			}
		}
		return null;
	}

	boolean deleteById(String id) throws SQLException {
		boolean ret = cache.remove(id) != null;
		if(pool != null) {
			Connection c = getConnection();
			try {
				PreparedStatement preparedDelete = prepareDelete(c);
				preparedDelete.setString(1, id);
				int count = preparedDelete.executeUpdate();
				return count == 1;
			}
			finally {
				c.close();
			}
		}
		return ret;
	}

	protected PreparedStatement prepareDelete(Connection c) throws SQLException {
		return c.prepareStatement(getDeleteStatement());
	}

	protected String getDeleteStatement() {
		return "delete from " + getTableName() + " where " + getIdColumnName() + " = ?";
	}

	protected abstract List<DbLoggerColumn> getColumns();

	protected String getCreateTableDdl() {
		StringBuilder ret = new StringBuilder(300);
		ret.append("CREATE TABLE ").append(getTableName()).append("(\r\n");
		String comma = "";
		for (DbLoggerColumn col : getColumns()) {
			ret.append(comma).append(col.name).append(' ').append(pool.getDialect().getDatatype(col.type))
					.append("\r\n");
			comma = ",";
		}
		ret.append(')');
		return ret.toString();
	}

	protected void createTable() throws SQLException {
		executeUpdate(getCreateTableDdl());
	}

	protected boolean addColumns(String message) throws SQLException {
		boolean added = false;
		for (DbLoggerColumn col : getColumns()) {
			if(message.toLowerCase().contains(col.getName().toLowerCase())) {
				try {
					String sql = "ALTER TABLE " + getTableName() + " ADD " + col.getName() + " "
							+ pool.getDialect().getDatatype(col.getType());
					LOG.info(sql);
					executeUpdate(sql);
					added = true;
				}
				catch (SQLException e) {
					// ignore
				}
			}
		}
		return added;
	}

	protected boolean ensureTableExists() {
		if(pool == null) {
			return false;
		}
		try {
			getById("-1");
			return true;
		}
		catch (SQLException ex) {
			try {
				createTable();
				getById("-1");
			}
			catch (SQLException ex1) {
				try {
					return addColumns(ex.getMessage());
				}
				catch (SQLException e) {
					LOG.log(Level.SEVERE,
							"Unable to create table " + getTableName() + ", logging to database disabled.", ex1);
					return false;
				}
			}
		}
		return true;
	}

	public JSONArray listByColumnValue(String columnName, String value) throws SQLException {
		if(pool != null) {
			return listFromDb(columnName, value);
		}
		else {
			return listFromMemory(columnName, value);
		}
	}

	// used by unit test. Add delay so logging completes in other thread
	void clearCache() {
		cache.clear();
		try {
			Thread.sleep(100);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	void dropTable() throws SQLException {
		executeUpdate("DROP TABLE " + getTableName());
	}

	void executeUpdate(String query) throws SQLException {
		Connection c = getConnection();
		if(c == null) {
			return;
		}
		try {
			Statement s = c.createStatement();
			s.executeUpdate(query);
			s.close();
		}
		finally {
			c.close();
		}
	}

}
