import algorithms.AlignmentAlgorithm
import algorithms.AlignmentElement
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_FFV1
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

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

    private val video1Grabber = FFmpegFrameGrabber(video1File)
    private val video2Grabber = FFmpegFrameGrabber(video2File)

    private val converter = Resettable2DFrameConverter()

    private var width = 0
    private var height = 0

    lateinit var alignment: Array<AlignmentElement>
    private lateinit var mask: BufferedImage

    /**
     * Initializes a new instance of the [DifferenceGenerator] class.
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
        generateMasking()
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
     * Uses the algorithm given in the constructor to align the frames of both videos.
     *
     * The resulting video consists of frames which are one of types:
     *  - only blue pixels: a frame was deleted (only in first video)
     *  - only green pixels: a frame was inserted (only in second video)
     *  - red and black pixels: a frame was modified (both videos contain the frame)
     */
    override fun generateDifference() {
        val encoder = FFmpegFrameRecorder(this.outputFile, video1Grabber.imageWidth, video1Grabber.imageHeight)
        encoder.videoCodec = AV_CODEC_ID_FFV1
        encoder.frameRate = 1.0
        encoder.start()

        val video1Images = grabBufferedImages(this.video1Grabber)
        val video2Images = grabBufferedImages(this.video2Grabber)

        // execute the alignment algorithm with the images of both videos
        alignment = algorithm.run(video1Images, video2Images)

        val video1It = video1Images.iterator()
        val video2It = video2Images.iterator()

        // iterate through the alignment sequence to build the image sequence
        for (a in alignment) {
            when (a) {
                AlignmentElement.MATCH -> {
                    val differences = getDifferences(video1It.next(), video2It.next())
                    encoder.record(differences)
                }
                AlignmentElement.INSERTION -> {
                    encoder.record(getColoredFrame(Color.GREEN))
                    // skipping the second video's frame (insertion)
                    video2It.next()
                }
                AlignmentElement.DELETION -> {
                    encoder.record(getColoredFrame(Color.BLUE))
                    // skipping the first video's frame (deletion)
                    video1It.next()
                }
            }
        }

        encoder.stop()
        encoder.release()
        this.video1Grabber.stop()
        this.video2Grabber.stop()
    }

    /**
     * Calculates the difference between two images.
     *
     * @param image1 the first image
     * @param image2 the second image
     * @return a frame where different pixels are red and identical pixels are black
     */
    private fun getDifferences(
        image1: BufferedImage,
        image2: BufferedImage,
    ): Frame {
        val differences = getColoredBufferedImage(Color.BLACK)

        // using a BufferedImage.raster.dataBuffer or just .raster might be faster
        for (x in 0 until width) {
            for (y in 0 until height) {
                val frame1Pixel = image1.getRGB(x, y)
                val frame2Pixel = image2.getRGB(x, y)
                if (frame1Pixel - frame2Pixel != 0) {
                    differences.setRGB(x, y, Color.RED.rgb)
                }
            }
        }

        return converter.getFrame(differences)
    }

    /**
     * Grabs all the frames from a video as BufferedImages.
     *
     * @param grabber the frame grabber of the video
     * @return an array of BufferedImages
     */
    private fun grabBufferedImages(grabber: FFmpegFrameGrabber): Array<BufferedImage> {
        val images = ArrayList<BufferedImage>()
        var frame = grabber.grabImage()

        while (frame != null) {
            val image = converter.getImage(frame)
            images.add(applyMasking(image))
            frame = grabber.grabImage()
        }
        return images.toTypedArray()
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
     * @return a Buffered Imnage colored in the given color
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

    private fun generateMasking() {
        if (maskFile == null) {
            mask = getColoredBufferedImage(Color(255, 255, 255, 0), BufferedImage.TYPE_4BYTE_ABGR)
            return
        }
        val maskGrabber = ImageIO.read(maskFile)
        mask = maskGrabber
        if (maskGrabber.width != width || maskGrabber.height != height) {
            throw Exception("Mask must have the same dimensions as the videos")
        }
    }

    private fun applyMasking(img: BufferedImage): BufferedImage {
        val g2d: Graphics2D = img.createGraphics()
        g2d.drawImage(mask, 0, 0, null)
        g2d.dispose()
        return img
    }
}
