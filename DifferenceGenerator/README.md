# AbstractDifferenceGenerator Class

The `AbstractDifferenceGenerator` class is an abstract class
for generating differences between two videos. 
This class allows you to extend it to create your own 
difference generator by implementing the required methods. 


## Getting Started

### Prerequisites

Before you get started, make sure you have the following installed:

- Java
- Gradle

### Installation

1. Clone the project repository to your local machine
2. Change your working directory to the project folder
3. `./gradlew downloadAndUnzipTestAssets` to get some example screenshots
4. Build the project using `./gradlew assemble`
5. Run the project using `./gradlew run`

### Initialization

Your difference generator should extend the `AbstractDifferenceGenerator` class. You can then initialize your difference generator.

### Testing

Tests can be found under `src/test/kotlin/` and resources under `src/test/resources/`.
To run all tests use `./gradlew test`.
