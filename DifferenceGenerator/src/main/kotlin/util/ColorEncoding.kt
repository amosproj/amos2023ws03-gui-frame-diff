package util

import algorithms.AlignmentElement
import java.awt.Color

class ColorEncoding {
    companion object {
        val elementToColor =
            mapOf(
                AlignmentElement.MATCH to Color.YELLOW,
                AlignmentElement.INSERTION to Color(0x99cc00),
                AlignmentElement.DELETION to Color.RED,
                AlignmentElement.PERFECT to Color.BLACK,
            )
    }
}
