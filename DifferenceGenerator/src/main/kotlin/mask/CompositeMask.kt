package mask

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * A composite mask possibly containing multiple masks to be applied to images.
 */
class CompositeMask : Mask {
    private var maskImage: BufferedImage

    /**
     * Initializes a new instance of the [CompositeMask] class with an image as mask.
     *
     * @param image The image to use as a mask.
     */
    constructor(image: BufferedImage) {
        maskImage = image
    }

    /**
     * Initializes a new instance of the [CompositeMask] class with an image file to load the mask from.
     *
     * @param maskFile The file to load the mask from.
     * @param width The width of the image.
     * @param height The height of the image.
     * @throws Exception if the mask does not have the same dimensions as the videos.
     */
    constructor(maskFile: File, width: Int, height: Int) {
        maskImage = ImageIO.read(maskFile)
        if (maskImage.width != width || maskImage.height != height) {
            throw Exception("Mask must have the same dimensions as the videos")
        }
    }

    /**
     * Draws the mask onto the given image.
     */
    override fun apply(image: BufferedImage): BufferedImage {
        val g2d: Graphics2D = image.createGraphics()
        g2d.drawImage(maskImage, 0, 0, null)
        g2d.dispose()
        return image
    }
}
