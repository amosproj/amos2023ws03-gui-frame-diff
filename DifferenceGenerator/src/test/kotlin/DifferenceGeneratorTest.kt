import algorithms.AlignmentElement
import algorithms.Gotoh
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.awt.image.BufferedImage
import java.io.File

internal class DifferenceGeneratorTest {
    private val losslessVideo = "src/test/resources/uncompressedWater.mov"
    private val losslessModifiedVideo = "src/test/resources/uncompressedModifiedWater.mov"
    private val compressedVideo = "src/test/resources/compressedVideo.mov"
    private val outputPath = "src/test/resources/output.mov"

    private val metric = PixelCountMetric(normalize = true)

    @Test
    fun `test constructor with lossless codec`() {
        val algorithm = Gotoh<BufferedImage>(metric, gapOpenPenalty = -1.0, gapExtensionPenalty = -0.0)
        val g = DifferenceGenerator(losslessVideo, losslessModifiedVideo, outputPath, algorithm)
        val expectedAlignment =
            arrayOf(
                AlignmentElement.MATCH, AlignmentElement.MATCH, AlignmentElement.MATCH,
                AlignmentElement.MATCH, AlignmentElement.MATCH, AlignmentElement.MATCH,
                AlignmentElement.MATCH, AlignmentElement.MATCH, AlignmentElement.MATCH,
            )
        assertArrayEquals(
            g.alignment,
            expectedAlignment,
        )

        val outputFile = File(outputPath)

        assertTrue(outputFile.exists())
        assertTrue(outputFile.isFile)
        assertTrue(outputFile.length() > 0)
    }

    @Test
    fun `test constructor with non-lossless codec`() {
        val algorithm = Gotoh<BufferedImage>(metric, gapOpenPenalty = -1.0, gapExtensionPenalty = -0.0)
        assertThrows(Exception::class.java) {
            DifferenceGenerator(losslessVideo, compressedVideo, outputPath, algorithm)
        }
    }
}
