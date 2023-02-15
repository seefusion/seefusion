#!/usr/bin/env bash
cd $(dirname $0)
set -ex
if command -v podman &> /dev/null
then
	CONTAINER_MANAGER=podman
else
	CONTAINER_MANAGER=docker
fi
$CONTAINER_MANAGER network exists seefusion-test || $CONTAINER_MANAGER create seefusion-test
$CONTAINER_MANAGER image list | grep -Eq 'gradle[[:space:]]+jdk8' || $CONTAINER_MANAGER pull gradle:jdk8
rsync -a --delete seefusion-ui/dist/* seefusion-main/src/main/resources/ui/
mkdir -p .gradle
"$CONTAINER_MANAGER" run --net seefusion-test --rm -u root -v "$PWD":/home/gradle/project:rw -v "$PWD/.gradle":/home/gradle/.gradle -w /home/gradle/project gradle:jdk8 gradle --no-daemon --warning-mode all build "$@"
