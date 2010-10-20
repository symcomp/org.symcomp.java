#!/bin/sh

VERSION="1.6-SNAPSHOT"
DEFAULTHOST=localhost
DEFAULTPORT=26133
DEFAULTCLIENT=addition

mvn assembly:assembly
if [ "$?" != "0" ]; then
	echo "Compilation failed."
	exit 1
fi

if [ "x$1x" == "xx" ]; then WHICHCL=$DEFAULTCLIENT; else WHICHCL=$1; fi
if [ "x$2x" == "xx" ]; then HOST=$DEFAULTHOST; else HOST=$2; fi
if [ "x$3x" == "xx" ]; then PORT=$DEFAULTPORT; else PORT=$3; fi

java -jar target/frameworkDemo-$VERSION-jar-with-dependencies.jar client $WHICHCL $HOST $PORT
