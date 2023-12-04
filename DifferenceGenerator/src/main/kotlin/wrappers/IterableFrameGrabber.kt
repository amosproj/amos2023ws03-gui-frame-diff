package wrappers

import Resettable2DFrameConverter
import org.bytedeco.javacv.FFmpegFrameGrabber
import java.awt.image.BufferedImage
import java.io.File

open class IterableFrameGrabber(videoFile: File) : ResettableIterable<BufferedImage>, FFmpegFrameGrabber(videoFile) {
    var image: BufferedImage? = null
    val converter = Resettable2DFrameConverter()

    init {
        super.start()
        image = converter.getImage(super.grabImage())
    }

    override fun start() {
        // No-op, as we already start the grabber on construct
    }

    override fun hasNext(): Boolean {
        return image != null
    }

    override fun next(): BufferedImage {
        // assume that next() is only called when hasNext is true
        val tempImage = image!!
        image = converter.getImage(super.grabImage())
        return tempImage
    }

    override fun iterator(): Iterator<BufferedImage> {
        return this
    }

    override fun reset() {
        super.stop()
        super.start()
        image = converter.getImage(super.grabImage())
    }

    override fun size(): Int {
        return super.getLengthInVideoFrames()
    }
}
