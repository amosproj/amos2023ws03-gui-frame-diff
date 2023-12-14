# About the test resources for the GUI

This directory contains test resources for the GUI.

## How to get the resources

run the command:
```./gradlew downloadAndUnzipTestAssets```

The test data lies compressed on an ftp server as ```lib2TestAssets.zip```

To add data refer to @fs3itz

## Structure of the resources

Th resources contain two uncompressed video files that can be used for testing and serve as the
default value for the file selection buttons in the GUI.

## if tests fail due to test data...

...try pulling the test assets again
```./gradlew downloadAndUnzipTestAssets```
