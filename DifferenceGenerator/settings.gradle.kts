dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
}

rootProject.name = "DifferenceGenerator"

include(":VideoGenerator")
println(rootProject.projectDir)
println(file("../VideoGenerator/library"))
project(":VideoGenerator").projectDir = rootProject.projectDir.resolve("../VideoGenerator/library")
// project(":VideoGenerator").projectDir = file("../VideoGenerator/library")
