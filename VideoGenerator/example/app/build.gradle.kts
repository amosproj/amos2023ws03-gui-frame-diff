plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "de.guiframediff.videogeneratorexample"
    compileSdk = 34

    defaultConfig {
        applicationId = "de.guiframediff.videogeneratorexample"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
    packaging {
        resources {
            // prevent native implementation from being packaged
            excludes += "META-INF/native-image/ios*/**"
            excludes += "META-INF/native-image/macos*/**"
            excludes += "META-INF/native-image/linux*/**"
            excludes += "META-INF/native-image/windows*/**"
            excludes += "META-INF/native-image/android-arm*/**"
            excludes += "META-INF/native-image/android-x86*/**"

            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.3.0")

    // only use android-specific implementations
    implementation("org.bytedeco:ffmpeg:6.0-1.5.9:android-arm64")
    implementation("org.bytedeco:ffmpeg:6.0-1.5.9:android-x86_64")
    implementation("org.bytedeco:javacpp:1.5.9:android-arm64")
    implementation("org.bytedeco:javacpp:1.5.9:android-x86_64")

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test:rules:1.4.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation(project(path = ":videogenerator"))
}

tasks.register("downloadAndUnzipTestAssets") {
    val assetPath = "src/androidTest/assets/"
    val zipDestinationPath = assetPath + "screens.zip"
    val sourceUrl = "ftp://seitzfabian.de/pub/screen.zip"
    createDir(assetPath)
    download(sourceUrl, zipDestinationPath)
    unzip(zipDestinationPath, assetPath)

    doLast {
        file(zipDestinationPath).delete()
    }
}

fun download(
    url: String,
    path: String,
) {
    val destFile = File(path)
    ant.invokeMethod("get", mapOf("src" to url, "dest" to destFile))
}

fun unzip(
    source: String,
    dest: String,
) {
    val destFile = File(source)
    ant.invokeMethod("unzip", mapOf("src" to source, "dest" to dest))
}

fun createDir(directoryPath: String) {
    val directory = file(directoryPath)
    if (!directory.exists()) {
        directory.mkdirs()
        println("Directory created at: $directoryPath")
    } else {
        println("Directory already exists at: $directoryPath")
    }
}
