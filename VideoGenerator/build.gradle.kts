import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.InventoryHtmlReportRenderer
import com.github.jk1.license.render.ReportRenderer

plugins {
    kotlin("jvm")
    application
    id("com.github.jk1.dependency-license-report") version "2.5"
}

group = "amos2023ws03"
version = "1.12.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.junit.jupiter:junit-jupiter:5.10.0")

    implementation("org.bytedeco:javacpp:1.5.9")
    implementation("org.bytedeco:javacv:1.5.9")

    // loads native implementations for all platforms
    implementation("org.bytedeco:ffmpeg-platform:6.0-1.5.9")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform { excludeTags("benchmark") }
}

kotlin {
    jvmToolchain(11)
}

licenseReport {
    val licensesDir = File(projectDir, "../licenses/").absolutePath
    outputDir = "$licensesDir/reports/VideoGenerator"
    renderers = arrayOf<ReportRenderer>(InventoryHtmlReportRenderer("report.html", "VideoGenerator"))
    filters = arrayOf<DependencyFilter>(LicenseBundleNormalizer())
    excludeBoms = true
    allowedLicensesFile = File(licensesDir, "allowed-licenses.json")
}
/*
// commented out, as this is now a library not containing a main function
application {
    mainClass.set("MainKt")
}
*/
