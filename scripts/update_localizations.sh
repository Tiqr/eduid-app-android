#!/bin/bash -eu

localicious render localizations.yaml ./ --languages en,nl --outputTypes android -c SHARED,ANDROID
mv android/nl/strings.xml app/src/main/res/values-nl/strings.xml
mv android/en/strings.xml app/src/main/res/values/strings.xml
rm -rf android
