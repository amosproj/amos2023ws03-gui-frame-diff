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
    implementation("org.bytedeco:javacv:1.4.4")
    //implementation("org.bytedeco:javacv-platform:1.4.4")
    testImplementation(kotlin("test"))

    //implementation("org.bytedeco:javacpp:1.4.4")
    //implementation("org.bytedeco:javacpp:1.5.9:android-arm64")
    //implementation("org.bytedeco:javacpp:1.5.9:android-x86_64")

    //implementation("org.bytedeco.javacpp-presets:ffmpeg:4.1-1.4.4")
    //implementation("org.bytedeco.javacpp-presets:ffmpeg:4.1-1.4.4:android-arm64")
    //implementation("org.bytedeco.javacpp-presets:ffmpeg:4.1-1.4.4:android-x86_64")

    //implementation("org.bytedeco.javacpp-presets:opencv:4.0.1-1.4.4")
    //implementation("org.bytedeco.javacpp-presets:ffmpeg:4.0.1-1.4.4")

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
