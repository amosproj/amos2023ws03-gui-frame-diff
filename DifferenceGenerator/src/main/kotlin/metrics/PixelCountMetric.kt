import java.awt.image.BufferedImage

/**
 * Implementation of the PixelCountMetric that counts the number of different pixels between two
 * BufferedImages.
 * @param normalize Whether to normalize the distance between 0 and 1.
 */
class PixelCountMetric(private val normalize: Boolean = true) : MetricInterface<BufferedImage> {
    /**
     * Measures the number of different pixels between two BufferedImages.
     *
     * @param a The first BufferedImage.
     * @param b The second BufferedImage.
     * @return The count of different pixels between the two images.
     */
    override fun measureDistance(
        a: BufferedImage,
        b: BufferedImage,
    ): Double {
        var count = 0

        val width = a.width
        val height = a.height

        for (y in 0 until height) {
            for (x in 0 until width) {
                val color1 = a.getRGB(x, y)
                val color2 = b.getRGB(x, y)

                if (color1 != color2) {
                    count++
                }
            }
        }

        if (!normalize) {
            return count.toDouble()
        }

        var normalizedDist = normalizeDistance(count.toDouble(), (width * height).toDouble())
        return normalizedDist
    }

    /**
     * Normalizes a distance between 0 and 1.
     *
     * @param distance The distance to normalize.
     * @param maxDistance The maximum distance possible.
     * @return The normalized distance.
     */
    private fun normalizeDistance(
        distance: Double,
        maxDistance: Double,
    ): Double {
        return distance / maxDistance
    }
}
