#!/bin/bash

echo "---- Building APK ----"
if ./gradlew assembleDebug; then
    echo "Build success!"
else
    echo "Build failed! Aborting."
    exit 1
fi

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ -f "$APK_PATH" ]; then
    echo "Installing APK..."
    adb install -r "$APK_PATH"
else
    echo "APK not found! Build may have failed."
    exit 1
fi
