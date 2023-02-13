<cfset things = ormExecuteQuery("FROM thing") />
<cfoutput>#serializeJson(things)#</cfoutput>
