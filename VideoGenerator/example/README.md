# Example App

The example app is used to try out the library and its functionalities.

### Prerequisites

Before you get started, make sure you have the following installed:

- Java
- Gradle

### Installation

1. Clone the project repository to your local machine
2. Open Android Studio and select the directory `./VideoGenerator/example/` as project
3. Now, `gradle` should load the build scripts and show the modules `app` (example app) and `videogenerator` (library)
4. You can now work on both the library and the example simultaneously.

If you're using something else than kotlin 1.9.0, you have to change the
```
android {
    ...
 
    composeOptions {
        kotlinCompilerExtensionVersion = "XXX"
    }
}
```

where you find the correct version number for `compose` (here)[https://developer.android.com/jetpack/androidx/releases/compose-kotlin].

For the proper setup of the example project, these settings were necessary:
`example/settings.gradle`:
```
rootProject.name = "example"
include(":app")

include(":videogenerator")
project(":videogenerator").projectDir = File("../")
```

`example/app/build.gradle.kt`:
```
dependencies {
    ...
    
    implementation(project(path = ":videogenerator"))
}
```

## Download Test Files
`./gradlew downloadAndUnzipTestAssets` will download and unzip test files from a given URL
the URL and destination File can be set in `example/app/build.gradle.kts

## Run the Example App and Tests

1. Start an emulator or connect a device, e.g. `emulator -avd Pixel_3a_API_30_x86 -wipe-data -no-snapshot-load`
    - to check for available AVDs execute `emulator -list-avds`
2. Run `./gradlew assembleDebug assembleDebugAndroidTest` to create debug and test APKs
3. Install the debug APK on the emulator or device, e.g. `adb install -r app/build/outputs/apk/debug/app-debug.apk`
4. Run the app on the emulator or device, e.g. `adb shell am start -n "com.example.videogenerator/com.example.videogenerator.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER`
5. Run the instrumented tests via `./gradlew connectedAndroidTest`
