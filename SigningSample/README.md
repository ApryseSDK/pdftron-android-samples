# Signing app sample

This sample demonstrates how to a Signing App in Android as described [in this blog](https://apryse.com/blog/android/build-a-signing-app-in-android). 

Creating Form | Signing Form
--- | ---
![Creating Form](https://pdftron.s3.amazonaws.com/custom/websitefiles/android/blog/signing/signing-sample-create.gif) | ![Signing Form](https://pdftron.s3.amazonaws.com/custom/websitefiles/android/blog/signing/signing-the-documnet.gif) 


## Prerequisites
Due to security reasons, `app/google-services.json` is not included in this repository however is required to run this project. You can obtain your own `google-services.json` by creating and registering your app with Firebase as described [here](https://firebase.google.com/docs/android/setup?authuser=0).

## Project structure
```
app/
  src/                        - Project source files and resources.
  build.gradle                - Module level Gradle build file.
build.gradle                  - Project level Gradle build file.
gradle.properties             - Project-wide Gradle settings. Contains Apryse license key and credentials.
```

## License
See [License](./../LICENSE)