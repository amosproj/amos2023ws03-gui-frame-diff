# GUI Frame Diff  (AMOS WS 2023)

## Product Vision
"The GUI Frame Diff tool is envisioned as a powerful, intuitive, and efficient solution for comparing sequences of screenshots. Our primary objective is to ensure seamless integration with existing interfaces and structures. 
The user interface, inspired by the functionality of video editing tools, is designed to offer an intuitive and efficient way to utilize the tool's capabilities. A wide range of customizable settings are available directly within the GUI, allowing users to optimize the output of the diff video according to their specific needs.
Beyond its core functionality, the GUI Frame Diff tool is designed with extensibility in mind. It can serve as a foundation for a variety of additional use cases, such as machine learning applications or the creation of tree-like data structures for enhanced data overview. This flexibility makes it a versatile tool that can adapt to the evolving needs of its users."

## Project Mission
The mission of this project is to develop a comprehensive and efficient GUI Frame Diff tool, structured into three synergistic libraries. Library 1 will focus on optimizing storage efficiency. It will combine multiple screenshots from a car's infotainment system into a single, compact video file. The key goal is to significantly reduce storage consumption without compromising the quality and integrity of the visual data. The core functionality of Library 2 is to accurately identify and articulate changes between two video sequences. This includes both frame-level modifications and pixel-level differences within frames. Building upon Library 2, UI-focused Library 3 will provide a user-friendly interface that allows users to effortlessly generate and visualize differences between videos. 

## About

**Library 1**

- It operates on the head unit (Android Auto), necessitating high efficiency.
- It processes a batch of images obtained from a single test run.
- Screenshots, encompassing all different system states, are generated on the head unit.
- A single video file is created from the batch of images received.

**Library 2**

- This library accepts two videos from two test runs as input.
- It calculates the difference between these two videos.
- The resulting image difference is then saved as a video.

**Library 3**
- Is the final GUI, that should look like a video editing tool  
- Can show the two input videos and the frame diff 

## Coding Guidelines 💅

- Use the Kotlin coding style defined here: https://kotlinlang.org/docs/coding-conventions.html
- Use ktlint to ensure a uniform coding style.

### Setup 

Install ktlint: https://pinterest.github.io/ktlint/1.0.1/install/cli/

We have a pre-commit hook that runs ktlint. Please activate it as follows:
1. Run `git config --local core.hooksPath .githooks` in the root directory of the project.
2. Mark the hook as executable: `chmod +x .githooks/pre-commit`

It makes sense to run `ktlint` more often to prevent a lot of formatting errors from piling up.
It is also encouraged to use an on-save formatter as provided by IDEs like IntelliJ, Android Studio and VS Code.

### License Checking

The project uses a [gradle plugin](https://github.com/jk1/Gradle-License-Report) to generate dependency license reports.
The reports are all saved in the `./licenses/reports/<SubprojectName>` directories.
Currently, we are allowing the licenses `MIT` and `Apache 2.0`. This information can be changed
in `./licenses/allowed-licenses.json`.

For `lib1`:
- Run a task that fails if dependencies with non-allowed licenses are found.
- `cd ./VideoGenerator/library && ./gradlew checkLicense`
- Generate a license report in html
- `cd ./VideoGenerator/library && ./gradlew generateLicenseReport`

For `lib2` (analogous for the `gui`):
- Run a task that fails if dependencies with non-allowed licenses are found.
- `cd ./DifferenceGenerator/ && ./gradlew checkLicense`
- Generate a license report in html
- `cd ./DifferenceGenerator/ && ./gradlew generateLicenseReport`


