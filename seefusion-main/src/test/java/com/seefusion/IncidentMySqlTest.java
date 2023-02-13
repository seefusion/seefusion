/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 * 
 */
public class IncidentMySqlTest extends IncidentDbTest {
	
	@Override
	String getJdbcUrl() throws ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		return "jdbc:mysql://" + Util.getEnv("MYSQL", "localhost") + "/seefusion";
	}

	@Override
	String getJdbcUser() {
		return "seefusion";
	}

	@Override
	String getJdbcPassword() {
		return "seefusion";
	}

}
