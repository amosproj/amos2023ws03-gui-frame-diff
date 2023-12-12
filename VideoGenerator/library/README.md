# Video Generator

## Introduction

This project provides an abstract class for video generators called `AbstractVideoGenerator`. You can extend this class
to create your own video generator. It allows you to generate videos by adding frames and saving the video to an output
path.

## Getting Started

### Prerequisites

Before you get started, make sure you have the following installed:

- Java
- Gradle

### Installation

1. Clone the project repository to your local machine
2. Change your working directory to the project folder
3. Build the project using `./gradlew assemble`
4. Execute unit tests using `./gradlew test`

### Initialization

Your video generator should extend the `AbstractVideoGenerator` class. You can then initialize your video generator.

### License check

To generate a license report in `../licenses/VideoGenerator`, execute `./gradlew generateLicenseReport`.
The gradle task `./gradlew checkLicense` can be used to check if any dependencies with disallowed licenses
are in use. The tasks fails with an error if that is the case.

