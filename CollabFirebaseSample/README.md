# Collab Firebase Sample

This sample creates a real-time document collaboration app as described [in this blog](https://www.pdftron.com/blog/android/build-real-time-collab-with-firebase-1) and UI customized as described [in this blog](https://www.pdftron.com/blog/android/build-real-time-collab-with-firebase-2).

## Prerequisites
Due to security reasons, `app/google-services.json` is not included in this repository however is required to run this project. You can obtain your own `google-services.json` by creating and registering your app with Firebase as described [here](https://firebase.google.com/docs/android/setup?authuser=0).

## Project structure
```
app/
  src/                        - Project source files and resources.
  build.gradle                - Module level Gradle build file.
build.gradle                  - Project level Gradle build file.
gradle.properties             - Project-wide Gradle settings. Contains PDFTron license key and credentials.
```

## License
See [License](./../LICENSE)
