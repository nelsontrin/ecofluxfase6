#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [[ -z "${JAVA_HOME:-}" ]]; then
  for candidate in /usr/lib/jvm/java-17-openjdk-* /usr/lib/jvm/temurin-17-*; do
    if [[ -x "$candidate/bin/java" ]]; then export JAVA_HOME="$candidate"; break; fi
  done
fi
if [[ -n "${JAVA_HOME:-}" ]]; then export PATH="$JAVA_HOME/bin:$PATH"; fi
mkdir -p "$ROOT/artifacts"
cd "$ROOT"
./mvnw -B clean test 2>&1 | tee "$ROOT/artifacts/test-output.txt"
mkdir -p "$ROOT/artifacts/surefire-reports"
cp -f "$ROOT/target/cucumber-report.html" "$ROOT/target/cucumber-report.json" "$ROOT/artifacts/"
cp -f "$ROOT/target/cucumber-junit.xml" "$ROOT/artifacts/"
cp -f "$ROOT/target/surefire-reports"/*.xml "$ROOT/target/surefire-reports"/*.txt "$ROOT/artifacts/surefire-reports/"
