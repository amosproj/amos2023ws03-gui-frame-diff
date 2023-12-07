dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
}

include(":VideoGenerator")
project(":VideoGenerator").projectDir = File("../VideoGenerator/library")

rootProject.name = "DifferenceGenerator"
