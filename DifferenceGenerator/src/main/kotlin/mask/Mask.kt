package mask

import java.awt.image.BufferedImage

enum class MaskSaveMode {
    PNG,
    JSON,
}

interface Mask {
    fun apply(image: BufferedImage): BufferedImage

    // fun save(outputFile: File, mode: MaskSaveMode?)
}
