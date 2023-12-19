import org.bytedeco.ffmpeg.global.avcodec
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.Exception
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow
import kotlin.system.measureTimeMillis

@Tag("benchmark")
class VideoGeneratorBenchmarkTest {
    private lateinit var videoGenerator: VideoGeneratorImpl
    private val videoPath = "src/resources/testOutput.mkv"
    private val inputPath = "example/app/src/androidTest/assets/screen"
    private val testCodecs =
        mapOf<Int, Map<String, String>?>(
            avcodec.AV_CODEC_ID_FFV1 to null,
            avcodec.AV_CODEC_ID_VP9 to mapOf<String, String>("lossless" to "1"),
        )
    private val testSizes = arrayOf(10, 100, 500, 800, 1000)
    private lateinit var testDataSet: ArrayList<File>

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
            testDataSet = getNRandomFrames(inputPath, size)
            for (codec in testCodecs) {
                videoGenerator.codecId = codec.key
                if (codec.value != null) {
                    videoGenerator.codecOptions = codec.value!!
                }
                loadNFrames(size)
                cleanUp()
            }
        }
    }

    @Test
    private fun loadNFrames(n: Int = 100) {
        var combinedFileSize = 0L
        try {
            val time =
                measureTimeMillis {
                    var i = 0
                    while (i < n) {
                        val frame = testDataSet[i]
                        combinedFileSize += frame.length()
                        videoGenerator.loadFrame(frame.readBytes())
                        i++
                    }
                    videoGenerator.save()
                }
            val video = File(videoPath)
            assertTrue(video.exists())
            println("Codec: ${avcodec.avcodec_get_name(videoGenerator.codecId).string}")
            println("Loading $n Files took $time ms (${time / n} ms / frame)")
            println(
                "File size reduced from ${readableFileSize(combinedFileSize)} to ${
                    readableFileSize(
                        video.length(),
                    )
                } (${
                    String.format(
                        "%.1f",
                        video.length().toDouble() / combinedFileSize.toDouble() * 100,
                    )
                }%)",
            )
        } catch (e: Exception) {
            println("Codec: ${avcodec.avcodec_get_name(videoGenerator.codecId).string}")
            println(e.localizedMessage)
            e.printStackTrace()
            assertTrue(false)
        }
    }

    private fun getNRandomFrames(
        path: String,
        n: Int,
    ): ArrayList<File> {
        val testSet = ArrayList<File>()
        val inputDir = File(path)
        for (i in 1..n) {
            inputDir.listFiles()?.random()?.let { testSet.add(it) }
        }
        return testSet
    }

    private fun readableFileSize(size: Long): String {
        if (size <= 0) return "0"
        val units = arrayOf("B", "kB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(
            size / 1024.0.pow(digitGroups.toDouble()),
        ) + " " + units[digitGroups]
    }
}
