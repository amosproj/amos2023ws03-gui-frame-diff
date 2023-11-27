# GUI


## Building instructions

1. **Prerequisites:**
    - Ensure that the following prerequisites are met:
    - install IntelliJ plugin: `Compose Multiplatform IDE Support`
    - Java Development Kit (JDK) installed. JDK 17 is preferred.
      . Gradle Build-Tool installed.
2. **Performing the Build:**
    - `./gradlew build` 
    - Create an application for your OS
       - `./gradlew createDistributable`
       - Executable at `build/compose/binaries/main/app/GUI/`
    - Create an installer for the current operating system
       - `./gradlew packageDistributionForCurrentOS`
       - Installer at `build/compose/binaries/main/{msi|deb|dmg}/`