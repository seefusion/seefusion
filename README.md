# SeeFusion

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

## Downloading

The latest build is at [https://seefusion.s3.amazonaws.com/seefusion-5.6.FINAL.jar](https://seefusion.s3.amazonaws.com/seefusion-5.6.FINAL.jar)

## Adding SeeFusion to your Tomcat-based app

### Use the built in installer

If you're using SeeFusion with Tomcat, seefusion.jar should be placed in the tomcat/lib folder and it can be used to install itself.  Here is an example of it being used in [testpages/Dockerfile](testpages/Dockerfile)
```bash
java -jar /usr/local/tomcat/lib/seefusion5.jar --install tomcat /usr/local/tomcat/conf
```

### Manual installation

You can either install it as a servlet filter using `com.seefusion.Filter`, but the preferred method is as a Tomcat valve under `<Engine ... name="Catalina">`:

```xml
    <Engine defaultHost="127.0.0.1" name="Catalina">
            <Valve className="com.seefusion.SeeFusionValve"/>
```

## Using SeeFusion to troubleshoot your app

Connect to port 8999 on the monitored server and click around a bit to see what requests are running slow and why

TODO: This could maybe use a bit more info than that

# Contributing

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