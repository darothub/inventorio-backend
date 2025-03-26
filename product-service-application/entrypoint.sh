#!/bin/sh
set -e

# Find the most recent JAR file, excluding the "plain" JAR
JAR_FILE=$(ls -t build/libs/*.jar | grep -v plain | head -n 1)

# Run the application
exec java -jar "${JAR_FILE}"