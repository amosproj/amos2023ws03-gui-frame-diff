dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

include(":VideoGenerator")
project(":VideoGenerator").projectDir = File("../VideoGenerator/library")

rootProject.name = "DifferenceGenerator"
