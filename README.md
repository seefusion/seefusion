# SeeFusion - README

![SeeFusion Logo](logo-seefusion-1.png "SeeFusion Logo")

Copyright (C) 2005-2023 Daryl Banttari

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

## About

SeeFusion provides detailed monitoring and troubleshooting information about Java Servlet apps, including Adobe ColdFusion and Lucee servers. SeeFusion tracks a variety of metrics, including active, recent and slow requests and queries, request and query bottlenecks, memory utlization levels, and more.

Since this information is most useful when a server has appeared to stop responding, SeeFusion is designed to be accessed in an "out-of-band" fashion, by a browser or xml-aware process establishing a connection to a non-standard server port (e.g. port 8999).

In addition, SeeFusion displays information about multiple servers in a convenient "dashboard" view, to allow for easily monitoring the status of large numbers of servers at once. You can also log metrics to a database for later analysis.

SeeFusion collects its data though two methods; first, a Java-standard "servlet filter" tracks the requests that are currently running. Second, a Java DataBase Connectivity (JDBC) driver "wrapper" tracks the queries that are executed by that request. SeeFusion attempts to be as non-intrusive as possible by adding as little processing overhead as possible.

## Downloading

The latest build is at [https://seefusion.s3.amazonaws.com/seefusion-5.6.FINAL.jar](https://seefusion.s3.amazonaws.com/seefusion-5.6.FINAL.jar)

## Adding SeeFusion to your Tomcat-based app

### Use the built in installer

If you're using SeeFusion with Tomcat, seefusion.jar should be placed in the tomcat/lib folder and it can be used to install itself.  Here is an example of it being used in [testpages/Dockerfile](testpages/Dockerfile)
```bash
java -jar /usr/local/tomcat/lib/seefusion5.jar --install tomcat /usr/local/tomcat/conf
```
You can also just download the .jar file and run it via `java -jar seefusion-5*.jar`, and follow the prompts.

### Manual installation

You can either install it as a servlet filter using `com.seefusion.Filter`, but the preferred method is as a Tomcat valve under `<Engine ... name="Catalina">`:

```xml
    <Engine defaultHost="127.0.0.1" name="Catalina">
            <Valve className="com.seefusion.SeeFusionValve"/>
```

## Wrapping datasources to enable database monitoring

In order for SeeFusion to monitor your database access:
* The JDBC Driver class should be changed to `com.seefusion.Driver`
* The "JDBC Url" used by your application needs to be wrapped with `jdbc:seefusion:{...}`. For example,
```
jdbc:macromedia:sqlserver://127.0.0.1:1433; databaseName=SampleDS; SelectMethod=direct; sendStringParametersAsUnicode=false; MaxPooledStatements=1000;
```
becomes
```
jdbc:seefusion:{jdbc:macromedia:sqlserver://127.0.0.1:1433; databaseName=SampleDS; SelectMethod=direct; sendStringParametersAsUnicode=false; MaxPooledStatements=1000;}
```
Username / Password fields should remain unchanged.

[Advanced JDBC driver options](JDBC.md) exist

## Advanced configuration

Other parameters can be set via SeeFusion.xml (in the same directory as SeeFusion5.jar) according to the options in [listed here](CONFIGURATION.md)

## Using SeeFusion to troubleshoot your app

Connect to port 8999 on the monitored server and click around a bit to see what requests are running slow and why

TODO: This could maybe use a bit more info than that

# Contributing

see [CONTRIBUTING.md](CONTRIBUTING.md)
