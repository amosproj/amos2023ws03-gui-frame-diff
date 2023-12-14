# Video Generator

## Introduction

This project provides an abstract class for video generators called `AbstractVideoGenerator`. You can extend this class
to create your own video generator. It allows you to generate videos by adding frames and saving the video to an output
path.

### Prerequisites

Before you get started, make sure you have the following installed:

- Java
- Gradle

### Installation

1. Clone the project repository to your local machine
2. Change your working directory to the project folder
3. Build the project using `./gradlew assemble`
4. Execute unit tests using `./gradlew test`

## Benchmark

For the Benchmark the two open and free codecs FFV1 and VP9 were compared.

| Results        	| 10    	| 100   	| 500   	| 800   	| 1000  	| 5000  	| 10000 	|
|----------------	|-------	|-------	|-------	|-------	|-------	|-------	|-------	|
| VP9  ms/frame  	| 148   	| 134   	| 135   	| 162   	| 179   	| 160   	| 155   	|
| FFV1 ms/frame  	| 109   	| 60    	| 56    	| 56    	| 66    	| 65    	| 60    	|
| VP9 file size  	| 69.5% 	| 60.5% 	| 57.3% 	| 56.8% 	| 59.1% 	| 57.3% 	| 56.5% 	|
| FFV1 file size 	| 84.4% 	| 74.7% 	| 76.3% 	| 76.6% 	| 76.2% 	| 75.8% 	| 76.2% 	|

More information about the benchmark results can be found [here](benchmarkOutput.txt).

## Example app

We provide an example Android app that uses the `VideoGenerator` library. You can find it in the `example` folder.
Setup and usage instructions can be found in the [README](example/README.md).
