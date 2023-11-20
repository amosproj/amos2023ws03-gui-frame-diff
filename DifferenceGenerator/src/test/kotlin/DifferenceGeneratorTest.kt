import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

internal class DifferenceGeneratorTest {
    private val losslessVideo = "src/test/resources/uncompressedWater.mov"
    private val losslessModifiedVideo = "src/test/resources/uncompressedModifiedWater.mov"
    private val compressedVideo = "src/test/resources/compressedVideo.mov"
    private val outputPath = "src/test/resources/output.mov"

    @Test
    fun `test constructor with lossless codec`() {
        DifferenceGenerator(losslessVideo, losslessModifiedVideo, outputPath)
        val outputFile = File(outputPath)

        assertTrue(outputFile.exists())
        assertTrue(outputFile.isFile)
        assertTrue(outputFile.length() > 0)
    }

    @Test
    fun `test constructor with non-lossless codec`() {
        assertThrows(Exception::class.java) {
            DifferenceGenerator(losslessVideo, compressedVideo, outputPath)
        }
    }
}
