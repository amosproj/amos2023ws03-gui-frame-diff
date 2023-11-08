import org.bytedeco.javacv.FFmpegFrameGrabber
import java.io.File

class DifferenceGenerator(video1Path: String, video2Path: String, outputPath: String) :
    AbstractDifferenceGenerator(video1Path, video2Path, outputPath) {
    val outputFile = File(outputPath)
    val video1File = File(video1Path)
    val video2File = File(video2Path)

    val video1Grabber = FFmpegFrameGrabber(video1File)
    val video2Grabber = FFmpegFrameGrabber(video2File)

    /**
     * Initializes a new instance of the [DifferenceGenerator] class.
     *
     * @throws Exception if the videos are not in an [AcceptedCodecs.ACCEPTED_CODECS].
     */
    init {
        if (!isLosslessCodec(video1Grabber) || !isLosslessCodec(video2Grabber)) {
            throw Exception("Videos must be in a lossless codec")
        }
        generateDifference()
    }

    /**
     * Determines whether the given video file is encoded using one of the
     * [AcceptedCodecs.ACCEPTED_CODECS].
     *
     * @param [FFmpegFrameGrabber] of the video to check
     * @return true if the video file is encoded using one of the [AcceptedCodecs.ACCEPTED_CODECS],
     * false otherwise
     */
    private fun isLosslessCodec(grabber: FFmpegFrameGrabber): Boolean {
        grabber.start()
        val codecName = grabber.videoMetadata["encoder"]
        grabber.stop()
        if (codecName == null) {
            throw Exception("Video must have a codec")
        }

        return codecName in AcceptedCodecs.ACCEPTED_CODECS
    }

    override fun generateDifference() {}

    override fun saveDifferences() {}
}
