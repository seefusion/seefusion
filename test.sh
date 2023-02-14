#!/usr/bin/env bash
cd $(dirname $0)
set -ex
if command -v podman &> /dev/null
then
	CONTAINER_MANAGER=podman
else
	CONTAINER_MANAGER=docker
fi
$CONTAINER_MANAGER image list | grep -Eq 'gradle[[:space:]]+jdk8' || $CONTAINER_MANAGER pull gradle:jdk8
rsync -a --delete seefusion-ui/dist/* seefusion-main/src/main/resources/ui/
"$CONTAINER_MANAGER" run --rm -u root -v "$PWD":/home/gradle/project:rw -w /home/gradle/project gradle:jdk8 gradle --no-daemon --warning-mode all clean build "$@"
