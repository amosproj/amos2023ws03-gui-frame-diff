import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.InventoryHtmlReportRenderer
import com.github.jk1.license.render.ReportRenderer

plugins {
    kotlin("jvm") version "1.8.0"
    application
    id("com.github.jk1.dependency-license-report") version "2.5"
}

// needed if imported as a dependency
repositories {
    mavenCentral()
}

group = "amos2023ws03"
version = "1.13.0"

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.bytedeco:javacv-platform:1.5.7")
    implementation(project(path = ":VideoGenerator"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.1")
}

tasks.test {
    useJUnitPlatform { excludeTags("benchmark") }
    systemProperty("gapOpenPenalty", System.getProperty("gapOpenPenalty") ?: "-0.5")
    systemProperty("gapExtensionPenalty", System.getProperty("gapExtensionPenalty") ?: "-0.5")
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

licenseReport {
    val licensesDir = File(projectDir, "../licenses/").absolutePath
    outputDir = "$licensesDir/reports/DifferenceGenerator"
    renderers = arrayOf<ReportRenderer>(InventoryHtmlReportRenderer("report.html", "DifferenceGenerator"))
    filters = arrayOf<DependencyFilter>(LicenseBundleNormalizer())
    excludeBoms = true
    allowedLicensesFile = File(licensesDir, "allowed-licenses.json")
}

tasks.register("downloadAndUnzipTestAssets") {
    val assetPath = "src/test/resources/"
    val zipDestinationPath = assetPath + "lib2Assets.zip"
    val sourceUrl = "ftp://seitzfabian.de/pub/lib2Assets.zip"
    createDir(assetPath)
    download(sourceUrl, zipDestinationPath)
    unzip(zipDestinationPath, assetPath)
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
