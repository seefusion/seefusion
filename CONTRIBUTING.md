# SeeFusion - CONTRIBUTING

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

## Building

* Install Podman or Docker (or compatible containery thingy.)  If you're on Windows, use Windows Subsystem for Linux to clone the repo and/or run these scripts.
* Run `./build.sh`
* Look for `build/libs/seefusion5-[version].jar`

## Did it work?

* Run `./run.sh`
* Access [http://localhost:8888](http://localhost:8888) to see a test page
* Access [http://localhost:8999](http://localhost:8999) to see SeeFusion showing the test page running

## Integration Tests
* If running Ubuntu on WSL, run `sudo update-alternatives --set iptables /usr/sbin/iptables-legacy` [(Source)](https://github.com/containers/podman/issues/14154) to get Docker networking to work
* Start the database server containers that are used for integration tests:
```bash
./mssql-docker.sh
./mysql-docker.sh
./oracle-docker.sh
```
* run `./test.sh`

## Development Principles

SeeFusion's guiding principles:
* Be as thin as possible, both in terms of size and performance impact
* The Hippocratic oath of monitoring tools is: "First, do no harm."  Carefully avoid doing things that might destabilize an application
* Avoid dependencies on external packages whenever possible to avoid potential conflicts with an application's version(s) of the same jar(s)
* Extreme situations are when SeeFusion is needed the most - if the JRE is still responsive at all, try to be available 

An example of these in action: SeeFusion implements its own custom HTTP responder (I hesitate to call it a "web server")
because when a server is experiencing poor performance, requests often can't get through the normal servlet backlog, so running this
as a servlet is not reliable when you need it the most.  Using a third-party general-purpose web server would be much larger than this current implementation,
and could potentially create conflicts if a customer application wants to use [a different version or instance of] that same server, or may cause conflicts with libraries
that web server implementation might in turn depend on.  The very limited scope of this HTTP responder keeps its memory footprint tiny, its availability very high,
and eliminates its potential to conflict with a monitored app.

Another example: if SeeFusion's database logging system falls far behind for any reason, it'll just start throwing away data.  The alternatives (allowing queueing
to consume memory without bound, or blocking the app until the log data can be queued) might impact the monitored application, and are therefore untenable.

# Subsystems

## [SeeFusion.java](seefusion-main/src/main/java/com/seefusion/SeeFusion.java)
This is the primary singleton that creates the other subsystems, and handles on-the-fly reconfiguration.  As the SeeFusion "office manager",
it's collected a lot of functionality over the years that maybe should be refactored off to more special-purpose classes.

## The Web GUI - Backend
The backend for the Web interface starts at [SocketListener](seefusion-main/src/main/java/com/seefusion/SocketListener.java), which accepts raw
TCP connections and creates [HttpTalker](seefusion-main/src/main/java/com/seefusion/HttpTalker.java) instances to read the request and write the
response.  [HttpRequestMap](seefusion-main/src/main/java/com/seefusion/HttpRequestMap.java) associates the request URI to the singleton that
handles the request.  The abstract classes [HttpRequestHandler](seefusion-main/src/main/java/com/seefusion/HttpRequestHandler.java)
and [JsonRequestHandler](seefusion-main/src/main/java/com/seefusion/JsonRequestHandler.java) do most of the busy-work of preparing responses;
the actual singletons that create the response generally only need to worry about producing content.  If a singleton implements 
[Cacheable](seefusion-main/src/main/java/com/seefusion/Cacheable.java), then its response is automatically cached for the duration specified.

Authentication is done by simple password, which causes an [AuthToken](seefusion-main\src\main\java\com\seefusion\AuthToken.java) to be issued and stored
in the [AuthTokenCache](seefusion-main\src\main\java\com\seefusion\AuthTokenCache.java).  Every token is associated with a set of permissions
represented by [Perm](seefusion-main\src\main\java\com\seefusion\Perm.java), and every page has its own Perm.  HttpTalker compares the requestor's
permission to the page's required permission before allowing the request to proceed:
```
if (perm.mayI(reqPerm)) {
    return httpRequestHandler.doGet(this);
}
else {
    authFailHttp(perm, reqPerm, httpRequest.getRequestLine().getPath());
}
```

Someday it might be nice to implement one or more SSO integrations, but anything more complicated than LDAP would probably require integrating
a third-party jar, which I'm hesitant to do (see Development Principles, above.)

HTTPS support is also gated behind using a third-party jar (eg BouncyCastle.)  Java 8's built in crypto libaries are byzantine and have put me off
trying to implenent TLS using them directly.

## The Web GUI - Frontend
The front end is written in AngularJS.  It was subcontracted at the time, and I don't know that much about it, other than it seems to work pretty well.
All front-end code is in `./seefusion-ui`, and currently the artifacts from its `dist` folder are copied into `seefusion-main` at compile-time.  A big
TODO here is to integrate the UI build into the Gradle build process so it can be built and bundled without this hackery.  (Prior to the
open-source release, `seefusion-ui` was in a completely separate repository)

## The Servlet Filter
For Web applications, a JEE servlet [Filter](seefusion-main/src/main/java/com/seefusion/Filter.java) is used to create monitoring wrappers for
requests as the enter the request pipeline.  A [RequestInfo](seefusion-main/src/main/java/com/seefusion/RequestInfo.java) object is created for
every request, and closed at the end of the request, which deregisters it from the SeeFusion.masterRequestList.

For Tomcat installations, it's easier to create a Valve object than configure one or more servlet filters.
[SeeFusionValve](seefusion-main/src/main/java/com/seefusion/SeeFusionValve.java) detects the Tomcat version (the interface changed between versions 7 and 8)
and loads the Valve appropriate to the Tomcat version.  The need to link to different, incompatible versions of Tomcat is why there are different subprojects
for [Tomcat7Valve](seefusion-tomcat7Helper\src\main\java\com\seefusion\Tomcat7Valve.java)
and [Tomcat8Valve](seefusion-tomcat8Helper\src\main\java\com\seefusion\Tomcat8Valve.java)

## The JDBC Driver Wrapper
To monitor active database queries, a JDBC [Driver](seefusion-main\src\main\java\com\seefusion\Driver.java) wrapper is used to wrap the
[Connection](seefusion-main\src\main\java\com\seefusion\ConnectionImpl.java), [Statement](seefusion-main\src\main\java\com\seefusion\StatementImpl.java),
and [ResultSet](seefusion-main\src\main\java\com\seefusion\ResultSetImpl.java) objects that are routinely created during database operations.

## The Database Logger
The subsystem that manages logging long-running pages and queries to a database starts at [DbLogger](seefusion-main\src\main\java\com\seefusion\DbLogger.java)
[SeeDAO](seefusion-main\src\main\java\com\seefusion\SeeDAO.java) is the base class for classes responsible for storing and fetching database objects, and anything
that might be managed by SeeDAO implements [DAOObject](seefusion-main\src\main\java\com\seefusion\DaoObject.java)
by extending [DaoObjectImpl](seefusion-main\src\main\java\com\seefusion\DaoObjectImpl.java)

## The Profiler
