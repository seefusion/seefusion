package com.seefusion;

import java.sql.SQLException;
import java.util.Properties;

public interface SqlDialectMetadata {

	Properties getMetadata(java.sql.Connection c) throws SQLException;
	
}
