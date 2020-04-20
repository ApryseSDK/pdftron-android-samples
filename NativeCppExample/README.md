# Native CPP Sample

This project allows you to access PDFNet's native C++ library right in your android project.

## Project structure
```
app/
  src/                        - Project source files and resources.
  build.gradle                - Module level Gradle build file.
PDFTron/                      - location of the native lib files
build.gradle                  - Project level Gradle build file.
gradle.properties             - Project-wide Gradle settings. Contains PDFTron license key and credentials.
```

## Instructions
1. Make sure that both the NDK & LLDB tools are installed in your android SDK
2. Open the Project Structure window (File -> Project Structure) and set the NDK location to the default
3. Add headers and platform .so files to the project
    1. Download the latest Android SDK from [here](https://www.pdftron.com/documentation/android/download/android/)
    2. Extract and navigate to `/lib/full/` and extract `pdftron.aar` (7-zip or any archiving software will work)
    3. Copy the `libs` folder in to the `app` folder in the projects root
    4. Copy the folders in the `jni` directory into the `PDFTron` folder in the projects root
    5. Copy the `headers` folder found in the root of the SDK download into the `app` folder in the projects root

After the above steps have been followed, your project structure should look like the following:
```
app/
  src/
  libs/PDFNet.jar
  headers/
    C/
    Common/
    FDF/
    Filters/
    Impl/
    PDF/
    SDF/
  build.gradle
PDFTron/
  arm64-v8a/libPDFNetC.so
  armeabi/libPDFNetC.so
  armeabi-v7a/libPDFNetC.so
  x86/libPDFNetC.so
  x86_64/libPDFNetC.so
build.gradle                  
gradle.properties             
```

4. Clean and run the project and you should see the installed fonts displaying in your simulator/device. Note that you can attach a debugger and step through the C++ code in `cpp/native-lib.cpp`

## Resources
- Access  the C++ API [here](https://www.pdftron.com/api/PDFTronSDK/cpp/index.html)

## License
See [License](./../LICENSE)
