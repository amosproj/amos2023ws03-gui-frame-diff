package util

import org.bytedeco.javacv.Frame
import wrappers.Resettable2DFrameConverter
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class ColoredFrameGenerator(val width: Int, val height: Int) {
    val converter = Resettable2DFrameConverter()

    /**
     * Creates a Frame with a given color.
     *
     * @param color the color
     * @return a frame colored in the given color
     */
    fun getColoredFrame(color: Color): Frame {
        return converter.getFrame(getColoredBufferedImage(color))
    }

    /**
     * Creates a Buffered Image with a given color.
     *
     * @param color the color
     * @return a Buffered Image colored in the given color
     */
    fun getColoredBufferedImage(
        color: Color,
        type: Int = BufferedImage.TYPE_3BYTE_BGR,
    ): BufferedImage {
        val result = BufferedImage(width, height, type)
        val g2d: Graphics2D = result.createGraphics()
        g2d.paint = color
        g2d.fillRect(0, 0, width, height)
        g2d.dispose()
        return result
    }
}
