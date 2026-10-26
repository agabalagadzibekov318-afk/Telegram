#!/usr/bin/env bash
set -euo pipefail
PKG=org.velungram.messenger.web
mkdir -p smoke-test-logs
adb wait-for-device
adb install -r dist/VelunGram.apk
adb logcat -c
echo "Launching $PKG on Android $(adb shell getprop ro.build.version.release)"
adb shell monkey -p "$PKG" -c android.intent.category.LAUNCHER 1 || true
sleep 20
adb logcat -d -v time > smoke-test-logs/logcat.txt
adb shell dumpsys activity activities > smoke-test-logs/activities.txt || true
if ! adb shell pidof "$PKG" >/dev/null 2>&1; then
  echo "::error::VelunGram process not running 20 seconds after launch."
  grep -n -A 35 -B 8 -E 'FATAL EXCEPTION|Fatal signal|Process: org.velungram|AndroidRuntime|UnsatisfiedLinkError|ExceptionInInitializerError' smoke-test-logs/logcat.txt | tail -250 || true
  exit 1
fi
if grep -q -E 'FATAL EXCEPTION|Fatal signal' smoke-test-logs/logcat.txt && grep -q 'org.velungram.messenger.web' smoke-test-logs/logcat.txt; then
  echo "::warning::A fatal error appears in logcat; inspect the uploaded log for context."
  grep -n -A 25 -B 5 'FATAL EXCEPTION' smoke-test-logs/logcat.txt | tail -150 || true
fi
echo "VelunGram process remains running after 20 seconds on the emulator."
