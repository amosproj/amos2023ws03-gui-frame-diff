import com.github.jk1.license.filter.DependencyFilter
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.InventoryHtmlReportRenderer
import com.github.jk1.license.render.ReportRenderer

plugins {
    kotlin("jvm") version "1.8.0"
    application
    id("com.github.jk1.dependency-license-report") version "2.5"
}

group = "org.example"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.bytedeco:javacv-platform:1.5.7")
    testImplementation(project(mapOf("path" to ":VideoGenerator")))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}

licenseReport {
    outputDir = "../licenses/reports/DifferenceGenerator"
    renderers = arrayOf<ReportRenderer>(InventoryHtmlReportRenderer("report.html", "DifferenceGenerator"))
    filters = arrayOf<DependencyFilter>(LicenseBundleNormalizer())
    allowedLicensesFile = File("../licenses/allowed-licenses.json")
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
