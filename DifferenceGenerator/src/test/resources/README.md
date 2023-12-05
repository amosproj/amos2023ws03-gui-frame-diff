# About the test resources for the Difference Generator

This directory contains test resources for the DifferenceGenerator.

## How to get the resources

run the command:
```./gradlew downloadAndUnzipTestAssets```

The test data lies compressed on an ftp server as ```lib2TestAssets.zip```

To add data refer to @fs3itz

## Structure of the resources

All unmodified Screenshots from our Stakeholder are in the directory ```screen```.

```screenModified``` contains again all screenshots but some of them are modified.
Now (5th December 2023) roughly every second screenshot between index 1 and 40 is modified.
Info: draw random images from here if you don't want to test something specific but rather generate
random test data.

In the directory ```screenModifiedOnly``` there are only the modified screenshots containing a 
hint about what was modified in the filename.
Info: Draw images from here to build specific test cases with specific modifications.

In the root directory there are encoded videos named like this:
```{#frames}Screenshots{'Modified' if frame was modified}.mov```
Additionally, there is a video which uses a lossy codec calles ```compressedVideo.mov```

## if tests fail due to test data...

...try pulling the test assets again
```./gradlew downloadAndUnzipTestAssets```
