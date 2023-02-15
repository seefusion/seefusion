/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 * 
 */
public class IncidentOracleTest extends IncidentDbTest {

	DbLogger dbLogger;
	
	SeeFusion sf;
	
	@Override
	String getJdbcUrl() throws ClassNotFoundException {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		return "jdbc:oracle:thin:@oracle:1521:XE";
	}

	@Override
	String getJdbcUser() {
		return "seefusion";
	}
	
	@Override
	String getJdbcPassword() {
		return "Seefus1on";
	}

}
