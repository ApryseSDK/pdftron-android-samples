# Collab Firebase Sample

This sample creates a real-time document collaboration app as described [in this blog](https://www.pdftron.com/blog/android/build-real-time-collab-with-firebase-1) and UI customized as described [in this blog](https://www.pdftron.com/blog/android/build-real-time-collab-with-firebase-2).

## Prerequisites
Due to security reasons, `app/google-services.json` is not included in this repository however is required to run this project. You can obtain your own `google-services.json` by creating and registering your app with Firebase as described [here](https://firebase.google.com/docs/android/setup?authuser=0).

## Firebase console setup
From the Firebase console click the "Authentication" button on the left panel and then click the "Sign-in Method" tab, just to the right of "Users". From this page click the "Anonymous" button and choose to enable Anonymous login.

Copy the JSON below and paste it in your Firebase Console's Database Rules. From the console click the "Database" button on the left panel and then click the "Rules" tab, just to the right of "Data". This will make sure that trying to modify someone else's annotation isn't allowed.

```javascript
{
  "rules": {
    ".read": "auth != null",

    "annotations": {
      "$annotationId": {
        ".write": "auth.uid === newData.child('authorId').val() || auth.uid === data.child('authorId').val() || auth.uid === newData.child('parentAuthorId').val() || auth.uid === data.child('parentAuthorId').val()"
      }
    },

    "authors": {
      "$authorId": {
        ".write": "auth.uid === $authorId"
      }
    }
  }
}
```


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
