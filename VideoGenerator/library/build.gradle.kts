plugins {
    kotlin("jvm")
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(11)
}

/*
// commented out, as this is now a library not containing a main function
application {
    mainClass.set("MainKt")
}
*/
