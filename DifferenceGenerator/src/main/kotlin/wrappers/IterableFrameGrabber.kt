package wrappers

import org.bytedeco.javacv.FFmpegFrameGrabber
import java.awt.image.BufferedImage
import java.io.File

/**
 * A frame grabber that can be iterated over and set back to its starting position.
 *
 * @param videoFile The video file to grab frames from.
 */
open class IterableFrameGrabber(videoFile: File) : ResettableIterable<BufferedImage>, FFmpegFrameGrabber(videoFile) {
    var image: BufferedImage? = null
    private val converter = Resettable2DFrameConverter()

    /**
     * Initializes a new instance of the class.
     * Start the frame grabber and grab the first image.
     */
    init {
        super.start()
        getNextImage()
    }

    /**
     * Override without action to prevent the user from calling super.start(), as starting
     * and stopping is handled inside the class.
     */
    override fun start() {
        // No-op, as we already start the grabber in constructor
    }

    /**
     * Check if there is a next image. As the next image resides in the member variable,
     * we simply have to check if it is not null.
     */
    override fun hasNext(): Boolean {
        return image != null
    }

    /**
     * Returns the next image.
     *
     * This makes use of the already loaded image.
     * Also, the next image is loaded before the current one is returned.
     *
     * @return The next image.
     */
    override fun next(): BufferedImage {
        // assume that next() is only called when hasNext is true
        val tempImage = image!!
        getNextImage()
        return tempImage
    }

    override fun iterator(): Iterator<BufferedImage> {
        return this
    }

    /**
     * Resets the frame grabber to its starting position.
     *
     * This is done by stopping and starting the grabber again.
     * Also, the next image is loaded.
     */
    override fun reset() {
        super.stop()
        super.start()
        getNextImage()
    }

    /**
     * Grabs the next image from the video file.
     *
     * This is done by grabbing the next frame and converting the resulting
     * [org.bytedeco.javacv.Frame] to a [BufferedImage]. If the frame is null,
     * the image is set to null.
     */
    private fun getNextImage() {
        val frame = super.grabImage()
        image = if (frame == null) null else converter.getImage(frame)
    }

    override fun size(): Int {
        return super.getLengthInVideoFrames()
    }
}
