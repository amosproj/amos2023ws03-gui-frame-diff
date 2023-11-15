import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class VideoGeneratorImplTest {
    private lateinit var videoGenerator: VideoGeneratorImpl
    private val videoPath = "src/resources/testOutput.mkv"
    private lateinit var exampleImageData: ByteArray

    @BeforeEach
    fun setUp() {
        videoGenerator = VideoGeneratorImpl(videoPath, 640, 480)
        exampleImageData = Files.readAllBytes(Paths.get("src/resources/Screenshot_50.png"))
    }

    @AfterEach
    fun cleanUp() {
        File(videoPath).delete() // cleanup created test video
    }

    @Test
    fun `Test loadFrame and processFrames`() {
        videoGenerator.loadFrame(exampleImageData)
        videoGenerator.processFrames()

        // after processing frames, expect the video file to be created
        assertTrue(Files.exists(Paths.get(videoPath)))
    }
}