package mask

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class CompositeMask : Mask {
    var maskImage: BufferedImage

    constructor(image: BufferedImage) {
        maskImage = image
    }

    constructor(maskFile: File, width: Int, height: Int) {
        maskImage = ImageIO.read(maskFile)
        if (maskImage.width != width || maskImage.height != height) {
            throw Exception("Mask must have the same dimensions as the videos")
        }
    }

    override fun apply(image: BufferedImage): BufferedImage {
        val g2d: Graphics2D = image.createGraphics()
        g2d.drawImage(maskImage, 0, 0, null)
        g2d.dispose()
        return image
    }
}
