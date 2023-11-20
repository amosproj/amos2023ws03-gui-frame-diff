import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.image.BufferedImage

class PixelCountMetricTest {
    @Test
    fun testPixelCountNotNormalized() {
        val pixelCountMetric = PixelCountMetric(normalize = false)

        // Test case 1: Two images with the same content
        val image1 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        val image2 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        assertEquals(0.0, pixelCountMetric.measureDistance(image1, image2))

        // Test case 2: Two images with different content
        val image3 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        val image4 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        // Modify the pixels of image4 to make it different from image3
        image4.setRGB(5, 5, Color.red.rgb)
        assertEquals(1.0, pixelCountMetric.measureDistance(image3, image4))

        // Test case 3: Two images with different content
        val image5 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        val image6 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        // Modify the pixels of image6 to make it different from image5
        image6.setRGB(5, 5, Color.red.rgb)
        image6.setRGB(6, 6, Color.green.rgb)
        image6.setRGB(1, 0, Color.blue.rgb)
        image6.setRGB(9, 8, Color.yellow.rgb)
        assertEquals(4.0, pixelCountMetric.measureDistance(image5, image6))
    }

    @Test
    fun testPixelCountNormalized() {
        val pixelCountMetric = PixelCountMetric(normalize = true)

        // Test case 1: Two images with the same content
        val image1 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        val image2 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        assertEquals(0.0, pixelCountMetric.measureDistance(image1, image2))

        // Test case 2: Two images with different content
        val image3 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        val image4 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        // Modify the pixels of image4 to make it different from image3
        image4.setRGB(5, 5, Color.red.rgb)
        val expectedNormalizedDistance1 = 1.0 / (10.0 * 10.0)
        assertEquals(expectedNormalizedDistance1, pixelCountMetric.measureDistance(image3, image4))

        // Test case 3: Two images with different content
        val image5 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        val image6 = BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        // Modify the pixels of image6 to make it different from image5
        image6.setRGB(5, 5, Color.red.rgb)
        image6.setRGB(6, 6, Color.green.rgb)
        image6.setRGB(1, 0, Color.blue.rgb)
        image6.setRGB(9, 8, Color.yellow.rgb)
        val expectedNormalizedDistance2 = 4.0 / (10.0 * 10.0)
        assertEquals(expectedNormalizedDistance2, pixelCountMetric.measureDistance(image5, image6))
    }
}
