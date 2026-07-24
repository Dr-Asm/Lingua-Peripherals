#!/bin/sh
# Gradle startup script for POSIX
DIRNAME=$(dirname "$0")
APP_HOME=$(cd "$DIRNAME" && pwd -P)

DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"

if [ -z "$JAVA_HOME" ]; then
    JAVA_EXE=java
else
    JAVA_EXE="$JAVA_HOME/bin/java"
fi

APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec "$JAVA_EXE" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
