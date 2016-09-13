#!/bin/sh

BREW_DIR="/usr/local/Cellar/android-sdk"

function die() {
    echo $1
    exit 1
}

[[ -d "$ANDROID_HOME" ]] || export ANDROID_HOME="=/usr/local/opt/android-sdk"

if [ ! -d "$ANDROID_HOME" ]; then
    if [ -d "$BREW_DIR" ]; then
        versions=`cd $BREW_DIR; /bin/ls | sort -u | xargs`
        for v in $versions
        do
            export ANDROID_HOME="$BREW_DIR/$v"
        done
    fi
fi

[[ -d "$ANDROID_HOME" ]] || export ANDROID_HOME="=/Applications/Android\ Studio.app/sdk"

[[ -d "$ANDROID_HOME" ]] || die "ANDROID_HOME not set properly"

export GRADLE_OPTS="-Dorg.gradle.daemon=true"
./gradlew clean build $@

if [ $? -eq 0 ]; then

    APKS=`find . -name '*-release*.apk' | xargs`
    for apk in $APKS; do
        echo "Verifying apk : $apk" ;
        jarsigner -verify -verbose $apk
    done
fi
