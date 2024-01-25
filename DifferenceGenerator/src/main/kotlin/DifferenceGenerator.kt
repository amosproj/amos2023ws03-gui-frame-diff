import algorithms.AlignmentAlgorithm
import algorithms.AlignmentElement
import mask.CompositeMask
import mask.Mask
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_FFV1
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import util.ColoredFrameGenerator
import wrappers.MaskedImageGrabber
import wrappers.Resettable2DFrameConverter
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import kotlin.experimental.and

class DifferenceGenerator(
    videoReferencePath: String,
    videoCurrentPath: String,
    outputPath: String,
    private val algorithm: AlignmentAlgorithm<BufferedImage>,
    maskPath: String? = null,
) : AbstractDifferenceGenerator() {
    private val outputFile = File(outputPath)
    private val videoReferenceFile = File(videoReferencePath)
    private val videoCurrentFile = File(videoCurrentPath)
    private val maskFile = if (maskPath != null) File(maskPath) else null

    private var videoReferenceGrabber: MaskedImageGrabber = MaskedImageGrabber(videoReferenceFile, null)
    private var videoCurrentGrabber: MaskedImageGrabber = MaskedImageGrabber(videoCurrentFile, null)

    private val converter = Resettable2DFrameConverter()
    private var coloredFrameGenerator: ColoredFrameGenerator

    private var width = 0
    private var height = 0

    var alignment: Array<AlignmentElement> = arrayOf() // remove later
    private var mask: Mask

    /**
     * Initializes a new instance of the class.
     *
     * @throws DifferenceGeneratorCodecException if the videos are not in an [AcceptedCodecs.ACCEPTED_CODECS].
     * @throws DifferenceGeneratorDimensionException if the videos' dimensions don't match.
     */
    init {
        if (!isLosslessCodec(videoReferenceGrabber) || !isLosslessCodec(videoCurrentGrabber)) {
            throw DifferenceGeneratorCodecException("Videos must be in a lossless codec")
        }

        if (this.videoReferenceGrabber.imageWidth != this.videoCurrentGrabber.imageWidth ||
            this.videoReferenceGrabber.imageHeight != this.videoCurrentGrabber.imageHeight
        ) {
            throw DifferenceGeneratorDimensionException("Videos must have the same dimensions")
        }

        this.width = this.videoReferenceGrabber.imageWidth
        this.height = this.videoReferenceGrabber.imageHeight
        coloredFrameGenerator = ColoredFrameGenerator(this.width, this.height)
        mask =
            if (maskFile == null) {
                CompositeMask(coloredFrameGenerator.getColoredBufferedImage(Color(255, 255, 255, 0), BufferedImage.TYPE_4BYTE_ABGR))
            } else {
                CompositeMask(maskFile, this.width, this.height)
            }

        videoReferenceGrabber.mask = mask
        videoCurrentGrabber.mask = mask

        // turn off verbose ffmpeg output
        avutil.av_log_set_level(avutil.AV_LOG_QUIET)
    }

    /**
     * Determines whether the given video file is encoded using one of the
     * [AcceptedCodecs.ACCEPTED_CODECS].
     *
     * @param grabber [MaskedImageGrabber] of the video to check
     * @return true if the video file is encoded using one of the [AcceptedCodecs.ACCEPTED_CODECS],
     * false otherwise
     */
    private fun isLosslessCodec(grabber: MaskedImageGrabber): Boolean {
        grabber.start()
        val codecName = grabber.videoMetadata["encoder"] ?: grabber.videoCodecName
        return codecName in AcceptedCodecs.ACCEPTED_CODECS
    }

    /**
     * Generates a difference video from the two videos given in the constructor.
     *
     * Uses the algorithm given in the constructor to align the frames of both videos.
     *
     * The resulting video consists of frames which are one of types:
     *  - only blue pixels: a frame was deleted (only in reference video)
     *  - only green pixels: a frame was inserted (only in current video)
     *  - red and black pixels: a frame was modified (both videos contain the frame)
     */
    override fun generateDifference() {
        val encoder = FFmpegFrameRecorder(this.outputFile, this.width, this.height)
        encoder.videoCodec = AV_CODEC_ID_FFV1
        encoder.frameRate = 1.0
        encoder.start()

        alignment = algorithm.run(videoReferenceGrabber, videoCurrentGrabber)

        // reset the grabbers to put the iterators to the videos' beginning
        videoReferenceGrabber.reset()
        videoCurrentGrabber.reset()

        for (el in alignment) {
            when (el) {
                AlignmentElement.MATCH -> {
                    encoder.record(getDifferencesBetweenBufferedImages(videoReferenceGrabber.next(), videoCurrentGrabber.next()))
                }
                AlignmentElement.INSERTION -> {
                    encoder.record(coloredFrameGenerator.getColoredFrame(Color.GREEN))
                    videoCurrentGrabber.next()
                }
                AlignmentElement.DELETION -> {
                    encoder.record(coloredFrameGenerator.getColoredFrame(Color.BLUE))
                    videoReferenceGrabber.next()
                }
                AlignmentElement.PERFECT -> {
                    encoder.record(coloredFrameGenerator.getColoredFrame(Color.BLACK))
                    videoReferenceGrabber.next()
                    videoCurrentGrabber.next()
                }
            }
        }
        encoder.stop()
        encoder.release()

        // check one last time, if the algorithm is still alive
        algorithm.isAlive()

        videoReferenceGrabber.stop()
        videoCurrentGrabber.stop()
        videoReferenceGrabber.release()
        videoCurrentGrabber.release()
    }

    /**
     * Calculates the difference between two images.
     *
     * @param image1 the first image
     * @param image2 the second image
     * @return a frame where different pixels are red and identical pixels are black
     */
    private fun getDifferencesBetweenBufferedImages(
        image1: BufferedImage,
        image2: BufferedImage,
    ): Frame {
        val differences = coloredFrameGenerator.getColoredBufferedImage(Color.BLACK)
        val differencesData = (differences.raster.dataBuffer as DataBufferByte)

        val data1 = (image1.raster.dataBuffer as DataBufferByte).data
        val data2 = (image2.raster.dataBuffer as DataBufferByte).data
        var index = 0

        while (index < this.height * this.width * 3) {
            val blue1 = data1[index] and 0xFF.toByte()
            val green1 = data1[index + 1] and 0xFF.toByte()
            val red1 = data1[index + 2] and 0xFF.toByte()

            val blue2 = data2[index] and 0xFF.toByte()
            val green2 = data2[index + 1] and 0xFF.toByte()
            val red2 = data2[index + 2] and 0xFF.toByte()

            differencesData.data[index] = 0x00.toByte() // blue
            differencesData.data[index + 1] = 0x00.toByte() // green

            var differenceRed = 0x00.toByte()
            if (blue1 != blue2 || green1 != green2 || red1 != red2) {
                differenceRed = 0xFF.toByte() // red
            }

            differencesData.data[index + 2] = differenceRed
            index += 3
        }
        return converter.getFrame(differences)
    }
}
