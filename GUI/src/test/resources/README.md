# About the test resources for the GUI

This directory contains test resources for the GUI.

## Resources available in this repo

There are two videos and a mask with which one can run the GUI.

- ```ExampleVideoNew.mov```
- ```ExampleVideoReference.mov```
- ```mask1200x700.png```

## Additional Resources (needed for running the tests)

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
