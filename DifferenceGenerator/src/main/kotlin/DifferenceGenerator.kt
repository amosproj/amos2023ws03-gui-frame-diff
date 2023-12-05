import algorithms.AlignmentAlgorithm
import algorithms.AlignmentElement
import mask.CompositeMask
import mask.Mask
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_FFV1
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import wrappers.MaskedImageGrabber
import wrappers.Resettable2DFrameConverter
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import kotlin.experimental.and

class DifferenceGenerator(
    video1Path: String,
    video2Path: String,
    outputPath: String,
    private val algorithm: AlignmentAlgorithm<BufferedImage>,
    maskPath: String? = null,
) : AbstractDifferenceGenerator(video1Path, video2Path, outputPath, maskPath) {
    private val outputFile = File(outputPath)
    private val video1File = File(video1Path)
    private val video2File = File(video2Path)
    private val maskFile = if (maskPath != null) File(maskPath) else null

    private var video1Grabber: MaskedImageGrabber = MaskedImageGrabber(video1File, null)
    private var video2Grabber: MaskedImageGrabber = MaskedImageGrabber(video2File, null)

    private val converter = Resettable2DFrameConverter()

    private var width = 0
    private var height = 0

    var alignment: Array<AlignmentElement> = arrayOf() // remove later
    private var mask: Mask

    /**
     * Initializes a new instance of the class.
     *
     * @throws Exception if the videos are not in an [AcceptedCodecs.ACCEPTED_CODECS].
     */
    init {
        if (!isLosslessCodec(video1Grabber) || !isLosslessCodec(video2Grabber)) {
            throw Exception("Videos must be in a lossless codec")
        }

        if (this.video1Grabber.imageWidth != this.video2Grabber.imageWidth ||
            this.video1Grabber.imageHeight != this.video2Grabber.imageHeight
        ) {
            throw Exception("Videos must have the same dimensions")
        }

        this.width = this.video1Grabber.imageWidth
        this.height = this.video1Grabber.imageHeight
        mask =
            if (maskFile == null) {
                CompositeMask(getColoredBufferedImage(Color(255, 255, 255, 0), BufferedImage.TYPE_4BYTE_ABGR))
            } else {
                CompositeMask(maskFile, this.width, this.height)
            }

        video1Grabber.mask = mask
        video2Grabber.mask = mask

        generateDifference()
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
     *  - only blue pixels: a frame was deleted (only in first video)
     *  - only green pixels: a frame was inserted (only in second video)
     *  - red and black pixels: a frame was modified (both videos contain the frame)
     */
    override fun generateDifference() {
        val encoder = FFmpegFrameRecorder(this.outputFile, this.width, this.height)
        encoder.videoCodec = AV_CODEC_ID_FFV1
        encoder.frameRate = 1.0
        encoder.start()

        alignment = algorithm.run(video1Grabber, video2Grabber)

        // reset the grabbers to put the iterators to the videos' beginning
        video1Grabber.reset()
        video2Grabber.reset()

        for (el in alignment) {
            when (el) {
                AlignmentElement.MATCH -> {
                    encoder.record(getDifferencesBetweenBufferedImages(video1Grabber.next(), video2Grabber.next()))
                }
                AlignmentElement.INSERTION -> {
                    encoder.record(getColoredFrame(Color.GREEN))
                    video2Grabber.next()
                }
                AlignmentElement.DELETION -> {
                    encoder.record(getColoredFrame(Color.BLUE))
                    video1Grabber.next()
                }
            }
        }

        encoder.stop()
        encoder.release()

        video1Grabber.stop()
        video2Grabber.stop()
        video1Grabber.release()
        video2Grabber.release()
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
        val differences = getColoredBufferedImage(Color.BLACK)
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

    /**
     * Creates a Frame with a given color.
     *
     * @param color the color
     * @return a frame colored in the given color
     */
    private fun getColoredFrame(color: Color): Frame {
        return converter.getFrame(getColoredBufferedImage(color))
    }

    /**
     * Creates a Buffered Image with a given color.
     *
     * @param color the color
     * @return a Buffered Image colored in the given color
     */
    private fun getColoredBufferedImage(
        color: Color,
        type: Int = BufferedImage.TYPE_3BYTE_BGR,
    ): BufferedImage {
        val result = BufferedImage(width, height, type)
        val g2d: Graphics2D = result.createGraphics()
        g2d.paint = color
        g2d.fillRect(0, 0, width, height)
        g2d.dispose()
        return result
    }
}
