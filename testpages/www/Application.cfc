component {
	this.name = "foo";
	this.datasource = "mysql";
	this.datasources["mysql"] = {
        type: 'mysql'
        , host: 'localhost'
        , database: 'seefusion'
        , port: 3306
        , username: 'seefusion'
        , password: 'seefusion'
	}
	this.datasources["mssql"] = {
        class: 'com.seefusion.Driver'
        , connectionString: 'seefusion:{jdbc:jtds:localhost/seefusion};driver=net.sourceforge.jtds.jdbc.Driver'
        , username: 'seefusion'
        , password: 'Seefus1on'
	}
	this.datasources["oracle"] = {
        class: 'oracle.jdbc.driver.OracleDriver'
		, connectionString: 'jdbc:oracle:thin:@localhost:1521:XE'
        , username: 'seefusion'
        , password: 'Seefus1on'
	}
}