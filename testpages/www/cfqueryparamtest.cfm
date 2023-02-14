<cfquery>
	create table test(id int, value varchar(100), rowdate datetime)
</cfquery>

<cftry>
	<cfset foo = "foo" />
	<cfquery>
		insert into test (
			id
			,value
			,rowdate
		) values (
			<cfqueryparam value="1" cfsqltype="cf_sql_integer">
			,<cfqueryparam value="#foo#" cfsqltype="cf_sql_varchar">
			,<cfqueryparam value="#foo#" cfsqltype="cf_sql_timestamp" null="#!isDate(foo)#">
		)
	</cfquery>
	<cfquery name="test">
		select * from test
	</cfquery>
	<cfdump var="#test#">
	<cffinally>
		<cfquery>
			drop table test
		</cfquery>
	</cffinally>
</cftry>
