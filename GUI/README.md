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

## Testing instructions

Tests can be found under `src/test/kotlin/`.
To run all tests of the GUI use `./gradlew :test`. 
(the colon prevents the tests of lib1 and lib2 to run)
