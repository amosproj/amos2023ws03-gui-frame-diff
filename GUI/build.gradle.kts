import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.InventoryHtmlReportRenderer
import com.github.jk1.license.render.ReportRenderer
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("com.github.jk1.dependency-license-report") version "2.5"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("org.bytedeco:javacv-platform:1.5.5")
//    implementation("org.bytedeco.ffmpeg:ffmpeg-platform:5.0.3")
//    implementation("org.opencv:opencv:4.5.3")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "GUI"
            packageVersion = "1.0.0"
            includeAllModules = true
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.register<Jar>("createRunnableJar") {
    from(sourceSets.main.get().output)
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    archiveFileName.set("GUI-Runnable.jar")
    destinationDirectory.set(file("$buildDir/libs"))
}

licenseReport {
    outputDir = "../licenses/reports/GUI"
    renderers = arrayOf<ReportRenderer>(InventoryHtmlReportRenderer("report.html", "GUI"))
    filters = arrayOf<DependencyFilter>(LicenseBundleNormalizer())
    allowedLicensesFile = File("../licenses/allowed-licenses.json")
}
