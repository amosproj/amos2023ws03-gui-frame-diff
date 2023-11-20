import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class VideoGeneratorImplTest {
    private lateinit var videoGenerator: VideoGeneratorImpl
    private val videoPath = "src/resources/testOutput.mkv"
    private lateinit var exampleImageData1: ByteArray
    private lateinit var exampleImageData2: ByteArray
    private lateinit var exampleImageData3: ByteArray
    private lateinit var exampleImageData4: ByteArray
    private lateinit var exampleImageData5: ByteArray
    private lateinit var exampleImageData6: ByteArray
    private lateinit var exampleImageData7: ByteArray
    private lateinit var exampleImageData8: ByteArray
    private lateinit var exampleImageData9: ByteArray

    @BeforeEach
    fun setUp() {
        videoGenerator = VideoGeneratorImpl(videoPath, 640, 480)
        exampleImageData1 = Files.readAllBytes(Paths.get("src/resources/Screenshot_50.png"))
        exampleImageData2 = Files.readAllBytes(Paths.get("src/resources/Screenshot_51.png"))
        exampleImageData3 = Files.readAllBytes(Paths.get("src/resources/Screenshot_52.png"))
        exampleImageData4 = Files.readAllBytes(Paths.get("src/resources/Screenshot_50.png"))
        exampleImageData5 = Files.readAllBytes(Paths.get("src/resources/Screenshot_51.png"))
        exampleImageData6 = Files.readAllBytes(Paths.get("src/resources/Screenshot_52.png"))
        exampleImageData7 = Files.readAllBytes(Paths.get("src/resources/Screenshot_50.png"))
        exampleImageData8 = Files.readAllBytes(Paths.get("src/resources/Screenshot_51.png"))
        exampleImageData9 = Files.readAllBytes(Paths.get("src/resources/Screenshot_52.png"))
    }

    @AfterEach
    fun cleanUp() {
        File(videoPath).delete()
    }

    @Test
    fun testLoadFrameAndSave() {
        videoGenerator.loadFrame(exampleImageData1)
        videoGenerator.loadFrame(exampleImageData2)
        videoGenerator.loadFrame(exampleImageData3)
        videoGenerator.loadFrame(exampleImageData4)
        videoGenerator.loadFrame(exampleImageData5)
        videoGenerator.loadFrame(exampleImageData6)
        videoGenerator.loadFrame(exampleImageData7)
        videoGenerator.loadFrame(exampleImageData8)
        videoGenerator.loadFrame(exampleImageData9)
        videoGenerator.save()

        assertTrue(Files.exists(Paths.get(videoPath)))
    }
}
