// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
package util

import algorithms.AlignmentElement
import java.awt.Color

class ColorEncoding {
    companion object {
        val elementToColor =
            mapOf(
                AlignmentElement.MATCH to Color(255, 239, 115),
                AlignmentElement.INSERTION to Color(0x99cc00),
                AlignmentElement.DELETION to Color(213, 48, 45),
                AlignmentElement.PERFECT to Color.BLACK,
            )
    }
}
