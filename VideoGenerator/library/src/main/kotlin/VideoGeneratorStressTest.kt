import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow
import kotlin.system.measureTimeMillis


class VideoGeneratorStressTest {


    private lateinit var videoGenerator: VideoGeneratorImpl
    private val videoPath = "src/resources/testOutput.mkv"
    private val inputPath = "../example/app/src/androidTest/assets/screen"
    private val testSizes = arrayOf(10, 100, 500, 800, 1000)

    @BeforeEach
    fun setUp() {
        videoGenerator = VideoGeneratorImpl(videoPath)
    }

    @AfterEach
    fun cleanUp() {
        File(videoPath).delete()
        videoGenerator = VideoGeneratorImpl(videoPath)
    }

    @Test
    fun stressTest() {
        for (size in testSizes) {
            loadNFrames(size)
            cleanUp()
        }
    }

    fun loadNFrames(n: Int) {
        var combinedFileSize = 0L
        val time = measureTimeMillis {
            var i = 0
            while (i < n) {
                val randomFrame = getRandomFrame(inputPath)
                if (randomFrame != null) {
                    combinedFileSize += randomFrame.length()
                    videoGenerator.loadFrame(randomFrame.readBytes())
                }
                i++
            }
            videoGenerator.save()
        }
        val video = File(videoPath)
        assertTrue(video.exists())
        println("Loading $n Files took $time ms (${time / n} ms / frame)")
        println(
            "File size reduced from ${readableFileSize(combinedFileSize)} to ${
                readableFileSize(
                    video.length()
                )
            } (${video.length() / combinedFileSize}%)"
        )
    }

    fun getRandomFrame(path: String): File? {
        val inputDir = File(path)
        return inputDir.listFiles()?.random()
    }

    fun readableFileSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(
            size / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups]
    }
}
