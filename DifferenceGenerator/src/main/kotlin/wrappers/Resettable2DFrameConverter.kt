// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
package wrappers

import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.image.BufferedImage

class Resettable2DFrameConverter : Java2DFrameConverter() {
    private fun reset() {
        this.bufferedImage = null
    }

    fun getImage(frame: Frame): BufferedImage {
        val img = super.getBufferedImage(frame)
        this.reset()
        return img
    }

    override fun getFrame(image: BufferedImage): Frame {
        val frame = super.getFrame(image, 1.0)
        this.reset()
        return frame
    }
}
