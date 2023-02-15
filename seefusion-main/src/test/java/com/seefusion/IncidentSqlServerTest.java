/**
 * 
 */
package com.seefusion;

/**
 * @author Daryl
 * 
 */
public class IncidentSqlServerTest extends IncidentDbTest {

	@Override
	String getJdbcUrl() throws ClassNotFoundException {
		Class.forName("net.sourceforge.jtds.jdbc.Driver");
		return "jdbc:jtds:sqlserver://mssql/seefusion";
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
