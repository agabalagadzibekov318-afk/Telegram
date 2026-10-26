#!/usr/bin/env bash
set -Eeuo pipefail
PKG=org.velungram.messenger.web
mkdir -p smoke-test-logs

save_logs() {
  adb logcat -d -v time > smoke-test-logs/logcat.txt 2>&1 || true
  adb shell dumpsys activity activities > smoke-test-logs/activities.txt 2>&1 || true
  adb shell getprop ro.build.version.release > smoke-test-logs/android-version.txt 2>&1 || true
  adb shell pidof "$PKG" > smoke-test-logs/pid.txt 2>&1 || true
}
trap save_logs EXIT

adb wait-for-device
echo "Testing Android $(adb shell getprop ro.build.version.release)"
# Old emulator package managers sometimes hang during streamed installs.
if ! timeout 250 adb install --no-streaming -r dist/VelunGram.apk; then
  echo "::warning::Install failed on first attempt; restarting ADB and retrying once"
  adb kill-server || true
  adb start-server || true
  adb wait-for-device
  timeout 250 adb install --no-streaming -r dist/VelunGram.apk
fi
adb logcat -c || true
# Explicit launcher alias; provides better diagnostics than monkey's silent failures.
adb shell am start -W -n "$PKG/org.telegram.messenger.DefaultIcon"
sleep 30
save_logs
if ! adb shell pidof "$PKG" >/dev/null 2>&1; then
  echo "::error::App process is not running 30 seconds after launcher start."
  grep -n -A 40 -B 8 -E 'FATAL EXCEPTION|Fatal signal|Process: org.velungram|AndroidRuntime|UnsatisfiedLinkError|ExceptionInInitializerError' smoke-test-logs/logcat.txt | tail -280 || true
  exit 1
fi
echo "VelunGram remained alive for 30 seconds on Android $(adb shell getprop ro.build.version.release)."
