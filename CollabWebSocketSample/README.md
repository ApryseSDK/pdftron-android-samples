# Collab WebSocket Sample

This sample creates a real-time document collaboration app that will work out-of-box with [WebViewer's WebSocket sample](https://github.com/PDFTron/webviewer-realtime-collaboration-sqlite3-sample).

## Prerequisites
1. Your server is up and running as described [here](https://github.com/PDFTron/webviewer-realtime-collaboration-sqlite3-sample)
2. You have the IP address of the local host running server from step 1
3. Open [WSConnection.kt](./app/src/main/java/com/pdftron/realtimecollaborationws/WSConnection.kt) and replace "MY_LOCAL_HOST_IP" with the IP from step 2


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
