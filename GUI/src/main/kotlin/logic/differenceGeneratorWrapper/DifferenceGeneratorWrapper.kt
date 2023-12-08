package logic.differenceGeneratorWrapper

import DifferenceGenerator
import PixelCountMetric
import algorithms.AlignmentAlgorithm
import algorithms.DivideAndConquerAligner
import algorithms.Gotoh
import hashing.VideoFrameHasher
import java.awt.image.BufferedImage
import java.io.File

/**
 * A class that wraps the [DifferenceGenerator] class to be used in the UI.
 *
 * @param video1Path The path to the first video.
 * @param video2Path The path to the second video.
 * @param outputPath The path to the output file.
 * @param gapOpenPenalty The gap open penalty for the alignment algorithm.
 * @param gapExtensionPenalty The gap extension penalty for the alignment algorithm.
 * @param maskPath The path to the mask file.
 */
class DifferenceGeneratorWrapper(
    video1Path: String,
    video2Path: String,
    private val outputPath: String,
    gapOpenPenalty: Double = -0.5,
    gapExtensionPenalty: Double = -0.0,
    maskPath: String? = null,
) {
    private val metric = PixelCountMetric(normalize = true)

    private var gotoh: AlignmentAlgorithm<BufferedImage> =
        Gotoh(metric, gapOpenPenalty, gapExtensionPenalty)

    private var divideAndConquerAligner: AlignmentAlgorithm<BufferedImage> = DivideAndConquerAligner(gotoh, VideoFrameHasher())

    private var differenceGenerator: DifferenceGenerator =
        DifferenceGenerator(
            video1Path = video1Path,
            video2Path = video2Path,
            outputPath = outputPath,
            algorithm = divideAndConquerAligner,
            maskPath = maskPath,
        )

    /**
     * Triggers the generation of the differences between the two videos and returns the path to the
     * output file. If output file already exists, it is not generated again.
     *
     * @return The path to the output file.
     */
    fun getDifferences(): String {
        // check if file at outputPath exists and is not empty
        val outputFile = File(outputPath)
        if (outputFile.exists() && outputFile.isFile && outputFile.length() > 0) {
            return outputPath
        }
        differenceGenerator.generateDifference()
        return outputPath
    }
}
