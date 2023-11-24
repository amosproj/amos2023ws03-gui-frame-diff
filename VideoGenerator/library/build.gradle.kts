plugins {
    kotlin("jvm")
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.10.0")

    implementation("org.bytedeco:javacpp:1.5.9")
    implementation("org.bytedeco:javacv:1.5.9")

    // loads native implementations for all platforms
    implementation("org.bytedeco:ffmpeg-platform:6.0-1.5.9")

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
