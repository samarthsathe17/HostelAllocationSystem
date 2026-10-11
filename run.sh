#!/bin/bash
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

JAVA_BIN="${JAVA_HOME:+$JAVA_HOME/bin/java}"
if [ -z "$JAVA_BIN" ] || [ ! -x "$JAVA_BIN" ]; then
    if [ -x "/Users/ashasathe/.antigravity-ide/extensions/redhat.java-1.56.0-darwin-arm64/jre/21.0.12.1-macosx-aarch64/bin/java" ]; then
        JAVA_BIN="/Users/ashasathe/.antigravity-ide/extensions/redhat.java-1.56.0-darwin-arm64/jre/21.0.12.1-macosx-aarch64/bin/java"
    else
        JAVA_BIN="java"
    fi
fi

JAVAC_BIN="javac"

CP="$HOME/.m2/repository/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar:$HOME/.m2/repository/com/formdev/flatlaf/3.4/flatlaf-3.4.jar"

echo "Compiling Java 21 classes..."
mkdir -p target/classes
$JAVAC_BIN --release 21 -cp "$CP" -d target/classes $(find src/main/java -name "*.java")

if [ -d "src/main/resources" ]; then
    cp -r src/main/resources/* target/classes/
fi

echo "Starting Backend on port 8080..."
exec "$JAVA_BIN" -cp "target/classes:$CP" com.hostel.Main "$@"
