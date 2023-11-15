import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_FFV1
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File

class DifferenceGenerator(video1Path: String, video2Path: String, outputPath: String) :
    AbstractDifferenceGenerator(video1Path, video2Path, outputPath) {
    private val outputFile = File(outputPath)
    private val video1File = File(video1Path)
    private val video2File = File(video2Path)

    private val video1Grabber = FFmpegFrameGrabber(video1File)
    private val video2Grabber = FFmpegFrameGrabber(video2File)

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
        return codecName in AcceptedCodecs.ACCEPTED_CODECS
    }

    /**
     * Generates a difference video from the two videos given in the constructor.
     *
     * Loops through each frame of the videos and calculates the difference between the two frames.
     */
    override fun generateDifference() {
        if (this.video1Grabber.imageWidth != this.video2Grabber.imageWidth ||
            this.video1Grabber.imageHeight != this.video2Grabber.imageHeight
        ) {
            throw Exception("Videos must have the same dimensions")
        }

        if (this.video1Grabber.lengthInFrames != this.video2Grabber.lengthInFrames) {
            throw Exception("Videos must have the same number of frames")
        }

        val encoder = FFmpegFrameRecorder(this.outputFile, video1Grabber.imageWidth, video1Grabber.imageHeight)
        encoder.videoCodec = AV_CODEC_ID_FFV1
        encoder.start()

        var frame1 = this.video1Grabber.grabImage()
        var frame2 = this.video2Grabber.grabImage()

        while (frame1 != null && frame2 != null) {
            val differences = getDifferences(frame1, frame2)
            encoder.record(differences)
            frame1 = this.video1Grabber.grabImage()
            frame2 = this.video2Grabber.grabImage()
        }

        encoder.stop()
        encoder.release()
        this.video1Grabber.stop()
        this.video2Grabber.stop()
    }

    /**
     * Calculates the difference between two frames.
     *
     * @param frame1 the first frame
     * @param frame2 the second frame
     * @return a frame where different pixels are red and the same pixels are black
     */
    private fun getDifferences(
        frame1: Frame,
        frame2: Frame,
    ): Frame {
        val width = frame1.imageWidth
        val height = frame1.imageHeight
        val differences = BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)

        val converterFrame1 = Java2DFrameConverter()
        val converterFrame2 = Java2DFrameConverter()

        val frame1Image = converterFrame1.convert(frame1)
        val frame2Image = converterFrame2.convert(frame2)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val frame1Pixel = frame1Image.getRGB(x, y)
                val frame2Pixel = frame2Image.getRGB(x, y)
                if (frame1Pixel - frame2Pixel == 0) {
                    differences.setRGB(x, y, Color.BLACK.rgb)
                } else {
                    differences.setRGB(x, y, Color.RED.rgb)
                }
            }
        }

        val converterOutput = Java2DFrameConverter()
        return converterOutput.getFrame(differences, 1.0)
    }

    /**
     * Overridden to finish class implementation.
     * Possibly remove from abstract class in the future.
     */
    override fun saveDifferences() {}
}
