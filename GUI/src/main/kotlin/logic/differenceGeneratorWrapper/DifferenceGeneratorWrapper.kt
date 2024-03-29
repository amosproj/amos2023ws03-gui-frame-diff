// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: Simon Sasse <simonsasse97@gmail.com>
package logic.differenceGeneratorWrapper

import DifferenceGenerator
import PixelCountMetric
import algorithms.AlignmentAlgorithm
import algorithms.AlignmentElement
import algorithms.DivideAndConquerAligner
import algorithms.Gotoh
import androidx.compose.runtime.MutableState
import hashing.VideoFrameHasher
import models.AppState
import java.awt.image.BufferedImage

/**
 * A class that wraps the [DifferenceGenerator] class to be used in the UI.
 *
 * @param state The state of the application.
 */
class DifferenceGeneratorWrapper(state: MutableState<AppState>) {
    private val metric = PixelCountMetric(normalize = true)

    private var gotoh: AlignmentAlgorithm<BufferedImage> =
        Gotoh(metric, state.value.gapOpenPenalty, state.value.gapExtendPenalty)

    private var divideAndConquerAligner: AlignmentAlgorithm<BufferedImage> = DivideAndConquerAligner(gotoh, VideoFrameHasher())

    private var differenceGenerator: DifferenceGenerator =
        DifferenceGenerator(
            videoReferencePath = state.value.videoReferencePath!!,
            videoCurrentPath = state.value.videoCurrentPath!!,
            outputPath = state.value.outputPath!!,
            algorithm = divideAndConquerAligner,
            maskPath = state.value.maskPath,
        )

    /**
     * Triggers the generation of the differences between the two videos and returns the path to the
     * output file.
     *
     * @return The path to the output file.
     */
    fun getDifferences(outPath: String): String {
        // TODO: check if file at outputPath exists and parse differences if given
        differenceGenerator.generateDifference()
        return outPath
    }

    /**
     * Returns the sequence of alignment elements.
     *
     * @return The sequence of alignment elements.
     */
    fun getSequence(): Array<AlignmentElement> {
        // TODO: if diff video exists, read it and return the sequence
        return differenceGenerator.alignment
    }
}
