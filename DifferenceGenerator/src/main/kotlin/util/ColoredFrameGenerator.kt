package util

import algorithms.AlignmentElement
import org.bytedeco.javacv.Frame
import wrappers.Resettable2DFrameConverter
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

class ColoredFrameGenerator(val width: Int, val height: Int) {
    val converter = Resettable2DFrameConverter()
    val coloredFrameBuffer = mutableMapOf<AlignmentElement, Frame>()

    /**
     * Initializes a new instance of the class.
     * Caches the colored frames for each [AlignmentElement].
     */
    init {
        for (element in AlignmentElement.values()) {
            coloredFrameBuffer[element] = getColoredFrame(ColorEncoding.elementToColor[element]!!)
        }
    }

    /**
     * Creates a colored Frame given a specific [AlignmentElement].
     *
     * @param element the alignment element to be encoded
     * @return a frame colored in the right encoding
     */
    fun getColoredFrame(element: AlignmentElement): Frame {
        return coloredFrameBuffer[element]!!
    }

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
     * Creates a Buffered Image given a specific [AlignmentElement].
     *
     * @param element the alignment element to be encoded
     * @return a Buffered Image colored in the right encoding
     */
    fun getColoredBufferedImage(
        element: AlignmentElement,
        type: Int = BufferedImage.TYPE_3BYTE_BGR,
    ): BufferedImage {
        return getColoredBufferedImage(ColorEncoding.elementToColor[element]!!, type)
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
