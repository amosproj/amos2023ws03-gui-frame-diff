import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_FFV1
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File

class DifferenceGenerator(
    video1Path: String,
    video2Path: String,
    outputPath: String,
    private val algorithm: AlignmentAlgorithm<BufferedImage>,
) : AbstractDifferenceGenerator(video1Path, video2Path, outputPath) {
    private val outputFile = File(outputPath)
    private val video1File = File(video1Path)
    private val video2File = File(video2Path)

    private val video1Grabber = FFmpegFrameGrabber(video1File)
    private val video2Grabber = FFmpegFrameGrabber(video2File)

    private var width = 0
    private var height = 0

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

        if (this.video1Grabber.lengthInFrames != this.video2Grabber.lengthInFrames) {
            throw Exception("Videos must have the same number of frames")
        }

        this.width = this.video1Grabber.imageWidth
        this.height = this.video1Grabber.imageHeight
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
        val encoder = FFmpegFrameRecorder(this.outputFile, video1Grabber.imageWidth, video1Grabber.imageHeight)
        encoder.videoCodec = AV_CODEC_ID_FFV1
        encoder.start()

        val video1Images = grabBufferedImages(this.video1Grabber)
        val video2Images = grabBufferedImages(this.video2Grabber)

        val alignment = algorithm.run(video1Images, video2Images)

        val video1It = video1Images.iterator()
        val video2It = video2Images.iterator()

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
        val differences = getBufferedImage(Color.BLACK)

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

        val converterOutput = Java2DFrameConverter()
        return converterOutput.getFrame(differences, 1.0)
    }

    /**
     * Creates a Buffered Image with a given color.
     *
     * @param color the color
     * @return a Buffered Imnage colored in the given color
     */
    private fun getBufferedImage(color: Color): BufferedImage {
        val result = BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
        val g2d: Graphics2D = result.createGraphics()
        g2d.paint = color
        g2d.fillRect(0, 0, width, height)
        g2d.dispose()
        return result
    }

    /**
     * Grabs all the frames from a video as BufferedImages.
     *
     * @param grabber the frame grabber of the video
     * @return an array of BufferedImages
     */
    private fun grabBufferedImages(grabber: FFmpegFrameGrabber): Array<BufferedImage> {
        val frames = ArrayList<BufferedImage>()
        var frame = grabber.grabImage()
        while (frame != null) {
            val converter = Java2DFrameConverter()
            frames.add(converter.convert(frame))
            frame = grabber.grabImage()
        }
        return frames.toTypedArray()
    }

    /**
     * Creates a Frame with a given color.
     *
     * @param color the color
     * @return a frame colored in the given color
     */
    private fun getColoredFrame(color: Color): Frame {
        val converterOutput = Java2DFrameConverter()
        return converterOutput.getFrame(getBufferedImage(color), 1.0)
    }
}
