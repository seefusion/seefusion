<cfloop from="1" to="5" index="i">
	<cfoutput>Loop #i#</cfoutput>
	<cfflush>
	<cfset createObject("java", "java.lang.Thread").sleep(1000)>
</cfloop>