<cfscript>
q = new Query();
q.setSQL("WAITFOR DELAY '0:00:30'");
q.execute();
</cfscript>
Done!
