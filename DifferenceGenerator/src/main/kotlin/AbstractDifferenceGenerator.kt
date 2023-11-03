import org.bytedeco.javacv.FFmpegFrameGrabber
import java.awt.image.BufferedImage

/**
 * Abstract class for the DifferenceGenerator.
 *
 * @param video1Path the path to the first video
 * @param video2Path the path to the second video
 * @param outputPath the path to the output file
 */
abstract class AbstractDifferenceGenerator(
    video1Path: String,
    video2Path: String,
    outputPath: String,
) {
    val video1Path: String = video1Path
    val video2Path: String = video2Path
    val outputPath: String = outputPath

    /**
     * Generates the difference between the two videos.
     *
     * Calls the saveDifferences() method.
     */
    abstract fun generateDifference(
        oldFileGrabber: FFmpegFrameGrabber,
        newFileGrabber: FFmpegFrameGrabber,
    )

    /**
     * Saves the differences to the output file.
     *
     * @return the video1Path
     */
    abstract fun saveDifferences(differences: List<BufferedImage>)
}
