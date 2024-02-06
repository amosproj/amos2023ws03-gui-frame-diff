// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: Simon Sasse <simonsasse97@gmail.com>
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import kotlin.experimental.and

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

        val data1 = (a.raster.dataBuffer as DataBufferByte).data
        val data2 = (b.raster.dataBuffer as DataBufferByte).data
        var index = 0

        while (index < height * width * 3) {
            val blue1 = data1[index] and 0xFF.toByte()
            val green1 = data1[index + 1] and 0xFF.toByte()
            val red1 = data1[index + 2] and 0xFF.toByte()

            val blue2 = data2[index] and 0xFF.toByte()
            val green2 = data2[index + 1] and 0xFF.toByte()
            val red2 = data2[index + 2] and 0xFF.toByte()

            if (blue1 != blue2 || green1 != green2 || red1 != red2) {
                count++
            }
            index += 3
        }

        if (!normalize) {
            return count.toDouble()
        }

        return normalizeDistance(count.toDouble(), (width * height).toDouble())
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
