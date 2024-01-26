# GUI


## Building instructions

1. **Prerequisites:**
    - Ensure that the following prerequisites are met:
    - install IntelliJ plugin: `Compose Multiplatform IDE Support`
    - Java Development Kit (JDK) installed. JDK 17 is preferred.
      . Gradle Build-Tool installed.
2. **Performing the Build:**
    - `./gradlew assemble` 
    - `./gradlew downloadAndUnzipTestAssets` to get some example screenshots
    - Create an application for your OS
       - `./gradlew createDistributable`
       - Executable at `build/compose/binaries/main/app/GUI/`
    - Create an installer for the current operating system
       - `./gradlew packageDistributionForCurrentOS`
       - Installer at `build/compose/binaries/main/{msi|deb|dmg}/`
    - Running without creating an executable
      - `./gradlew :run`
      - There are two videos and a mask included in this Repository that can be used for running the GUI.
      - The videos are `src/test/resources/ExampleVideoNew.mov` and `src/test/resources/ExampleVideoReference.mov`.
      - The mask is `src/test/resources/mask1200x700.png`.

### Environment variables

For convenience reason, the project offers an environment variable to be set to use some defaults
for path values (videos, masks, etc.).
Set `GUI_USE_DEFAULT_PATHS` to `true` to use the default paths before running the GUI.

- Windows: `$env:GUI_USE_DEFAULT_PATHS="true"`
- Shell: `export GUI_USE_DEFAULT_PATHS=true`

## Testing instructions

Tests can be found under `src/test/kotlin/`.
Tests rely on data that is not included in this repository.
To download the test data run `./gradlew downloadAndUnzipTestAssets`.
To run all tests of the GUI use `./gradlew :test`. 
(the colon prevents the tests of lib1 and lib2 to run)
