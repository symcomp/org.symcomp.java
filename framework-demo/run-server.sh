#!/bin/sh

VERSION="1.6-SNAPSHOT"

mvn assembly:assembly
if [ "$?" != "0" ]; then
	echo "Compilation failed."
	exit 1
fi
java -jar target/frameworkDemo-$VERSION-jar-with-dependencies.jar server 26133
