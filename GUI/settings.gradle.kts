pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }
}

rootProject.name = "GUI"

include(":DifferenceGenerator", ":VideoGenerator")
project(":DifferenceGenerator").projectDir = rootProject.projectDir.resolve("../DifferenceGenerator")
project(":VideoGenerator").projectDir = rootProject.projectDir.resolve("../VideoGenerator/library")
