package wrappers

import mask.Mask
import java.awt.image.BufferedImage
import java.io.File

class MaskedImageGrabber(videoFile: File, mask: Mask?) : IterableFrameGrabber(videoFile) {
    private var mask: Mask? = mask

    fun setMask(mask: Mask) {
        this.mask = mask
    }

    override fun next(): BufferedImage {
        val image = super.next()
        return if (mask != null) {
            mask!!.apply(image)
        } else {
            image
        }
    }
}
