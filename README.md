# eduid-app-android

eduId app for Android

## Requirements

- [Android SDK](http://developer.android.com/sdk/index.html).
- Android [10.0](http://developer.android.com/tools/revisions/platforms.html#10).
- Latest Android SDK Tools and build tools.
- Android Studio 4.2 and up.
- Add https://github.com/Tiqr/tiqr-app-core-android as submodule:
```
git submodule init
git submodule update
```

### Generate translations

The translations are kept in the file localizations.yaml.
For this, you need to install the node package `localicious` via `yarn` (if you do it with `npm`, it won't work correctly).
See [this](https://blog.picnic.nl/localizing-native-apps-made-easy-with-localicious-5063d02d3511) blog post for more info about localicious.

Once you have it installed, execute the following line from the root of the repository to regerenate the Android resource files based on the 
contents of `localizations.yaml`:

```shell
localicious render localizations.yaml ./ --languages en,nl --outputTypes android -c SHARED,ANDROID &&\
mv android/nl/strings.xml app/src/main/res/values-nl/strings.xml &&\
mv android/en/strings.xml app/src/main/res/values/strings.xml &&\
rm -rf android/
```
