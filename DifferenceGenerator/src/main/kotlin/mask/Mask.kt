// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
package mask

import java.awt.image.BufferedImage

enum class MaskSaveMode {
    PNG,
    JSON,
}

/**
 * An interface for masks to be applied to images.
 */
interface Mask {
    /**
     * Applies the mask to the given image.
     *
     * @param image The image to apply the mask to.
     * @return The masked image.
     */
    fun apply(image: BufferedImage): BufferedImage

    // fun save(outputFile: File, mode: MaskSaveMode?)
}
