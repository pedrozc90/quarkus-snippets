#!/bin/bash

# Usage: ./test.sh MessageServiceTest

if [ -z "$1" ]; then
  echo "Usage: $0 <SimpleTestClassName>"
  exit 1
fi

CLASS_NAME="$1"

# Run the JUnit test using Maven
./mvnw -Dtest="${CLASS_NAME}" test
