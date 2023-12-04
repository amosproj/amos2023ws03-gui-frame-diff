package wrappers

import Resettable2DFrameConverter
import org.bytedeco.javacv.FFmpegFrameGrabber
import java.awt.image.BufferedImage
import java.io.File

open class IterableFrameGrabber(videoFile: File) : ResettableIterable<BufferedImage>, FFmpegFrameGrabber(videoFile) {
    var image: BufferedImage? = null
    private val converter = Resettable2DFrameConverter()

    init {
        super.start()
        getNextImage()
    }

    override fun start() {
        // No-op, as we already start the grabber in constructor
    }

    override fun hasNext(): Boolean {
        return image != null
    }

    override fun next(): BufferedImage {
        // assume that next() is only called when hasNext is true
        val tempImage = image!!
        getNextImage()
        return tempImage
    }

    override fun iterator(): Iterator<BufferedImage> {
        return this
    }

    override fun reset() {
        super.stop()
        super.start()
        getNextImage()
    }

    private fun getNextImage() {
        val frame = super.grabImage()
        image = if (frame == null) null else converter.getImage(frame)
    }

    override fun size(): Int {
        return super.getLengthInVideoFrames()
    }
}
