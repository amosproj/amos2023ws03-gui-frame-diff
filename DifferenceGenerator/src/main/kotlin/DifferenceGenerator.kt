import org.bytedeco.javacv.FFmpegFrameGrabber
import java.awt.image.BufferedImage
import java.io.File

class DifferenceGenerator(video1Path: String, video2Path: String, outputPath: String) :
    AbstractDifferenceGenerator(video1Path, video2Path, outputPath) {
    val outputFile = File(outputPath)
    val video1File = File(video1Path)
    val video2File = File(video2Path)

    /**
     * Initializes a new instance of the [DifferenceGenerator] class.
     *
     * @throws Exception if the videos are not in an [AcceptedCodecs.ACCEPTED_CODECS].
     */
    init {
        if (isLosslessCodec(video1File) && isLosslessCodec(video2File)) {
            val video1Grabber = FFmpegFrameGrabber(video1File)
            val video2Grabber = FFmpegFrameGrabber(video2File)

            generateDifference(video1Grabber, video2Grabber)
        } else {
            throw Exception("Videos must be in a lossless codec")
        }
    }

    /**
     * Determines whether the given video file is encoded using one of the
     * [AcceptedCodecs.ACCEPTED_CODECS].
     *
     * @param videoFile the video file to check
     * @return true if the video file is encoded using one of the [AcceptedCodecs.ACCEPTED_CODECS],
     * false otherwise
     */
    private fun isLosslessCodec(videoFile: File): Boolean {
        val grabber = FFmpegFrameGrabber(videoFile)
        grabber.start()
        val codecName = grabber.videoMetadata["encoder"]
        grabber.stop()
        if (codecName == null) {
            throw Exception("Video must have a codec")
        }

        for (codec in AcceptedCodecs.ACCEPTED_CODECS) {
            if (codecName.contains(codec)) {
                return true
            }
        }
        return false
    }

    override fun generateDifference(
        oldFileGrabber: FFmpegFrameGrabber,
        newFileGrabber: FFmpegFrameGrabber,
    ) {}

    override fun saveDifferences(differences: List<BufferedImage>) {}
}
