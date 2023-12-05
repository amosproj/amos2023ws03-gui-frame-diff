package wrappers

import mask.Mask
import java.awt.image.BufferedImage
import java.io.File

/**
 * A frame grabber that applies a mask to the grabbed frames.
 *
 * @param videoFile The video file to grab frames from.
 * @param mask The mask to apply to the grabbed frames.
 */
class MaskedImageGrabber(videoFile: File, var mask: Mask?) : IterableFrameGrabber(videoFile) {
    /**
     * Gets the next frame from the video and applies the mask to it.
     *
     * @return The next frame from the video, masked.
     */
    override fun next(): BufferedImage {
        val image = super.next()
        return if (mask != null) {
            mask!!.apply(image)
        } else {
            image
        }
    }
}
