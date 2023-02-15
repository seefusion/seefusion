# SeeFusion

SeeFusion
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

## Adding SeeFusion to your Tomcat-based app

See `localtest/Dockerfile` for an example.

## Using SeeFusion to troubleshoot your app

TODO


# Contributing

## Building

* Install Podman or Docker (or compatible containery thingy)
* Run `./build.sh`
* Look for `build/libs/seefusion5-[version].jar`

## Integration Tests
* If running Ubuntu on WSL, run `sudo update-alternatives --set iptables /usr/sbin/iptables-legacy` [Source](https://github.com/containers/podman/issues/14154)


## Did it work?

* Run `./run.sh`
* Access [http://localhost:8888](http://localhost:8888) to see a test page
* Access [http://localhost:8999](http://localhost:8999) to see SeeFusion showing the test page running
