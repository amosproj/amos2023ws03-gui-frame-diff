import algorithms.AlignmentAlgorithm
import algorithms.AlignmentElement
import mask.CompositeMask
import mask.Mask
import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_FFV1
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import wrappers.MaskedImageGrabber
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.io.File
import java.security.MessageDigest
import javax.imageio.ImageIO
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
        mask = if (maskFile == null) {
            CompositeMask(getColoredBufferedImage(Color(255, 255, 255, 0), BufferedImage.TYPE_4BYTE_ABGR))
        } else {
            CompositeMask(maskFile, this.width, this.height)
        }

        video1Grabber.setMask(mask)
        video2Grabber.setMask(mask)

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
        val encoder = FFmpegFrameRecorder(this.outputFile, video1Grabber.imageWidth, video1Grabber.imageHeight)
        encoder.videoCodec = AV_CODEC_ID_FFV1
        encoder.frameRate = 1.0
        encoder.start()

        val video1Hashes = getHashedVideo(video1File)
        val video2Hashes = getHashedVideo(video2File)
        val equals = findEquals(video1Hashes, video2Hashes)
        println("Equals: $equals")

        processAlignment(equals, encoder)

        encoder.stop()
        encoder.release()
        this.video1Grabber.stop()
        this.video2Grabber.stop()
    }

    private fun processAlignment(
        equals: ArrayList<Pair<Int, Int>>,
        encoder: FFmpegFrameRecorder,
    ) {
        val video1Length = video1Grabber.lengthInFrames
        val video2Length = video2Grabber.lengthInFrames

        var nextGrabbedFrame1 = 0
        var nextGrabbedFrame2 = 0

        // process frames from (0,0) until last match in equals
        for (i in equals) {
            val video1Frames = i.first - nextGrabbedFrame1
            val video2Frames = i.second - nextGrabbedFrame2
            processFrames(video1Frames, video2Frames, encoder)
            encoder.record(getColoredFrame(Color.BLACK))
            alignment += AlignmentElement.MATCH
            nextGrabbedFrame1 = i.first + 1
            nextGrabbedFrame2 = i.second + 1
            video1Grabber.grabImage()
            video2Grabber.grabImage()
        }

        // process frames from last match in equals until end of video
        val video1Frames = video1Length - nextGrabbedFrame1
        val video2Frames = video2Length - nextGrabbedFrame2
        processFrames(video1Frames, video2Frames, encoder)
    }

    private fun getFrames(
        amount: Int,
        grabber: FFmpegFrameGrabber,
    ): ArrayList<BufferedImage> {
        val images = ArrayList<BufferedImage>()
        var i = 0
        while (i < amount) {
            var frame: Frame? = grabber.grabImage() ?: throw Exception("Video Grabbing calculation is wrong")
            val image = converter.getImage(frame!!)
            images.add(mask.apply(image))
            i++
        }
        return images
    }

    private fun processFrames(
        amountVideo1: Int,
        amountVideo2: Int,
        encoder: FFmpegFrameRecorder,
    ) {
        if (amountVideo1 == 0 && amountVideo2 == 0) {
            return
        }

        if (amountVideo1 == 0) {
            for (i in 0 until amountVideo2) {
                encoder.record(getColoredFrame(Color.GREEN))
                alignment += AlignmentElement.INSERTION
            }
            return
        }

        if (amountVideo2 == 0) {
            for (i in 0 until amountVideo1) {
                encoder.record(getColoredFrame(Color.BLUE))
                alignment += AlignmentElement.DELETION
            }
            return
        }

        val video1Images = getFrames(amountVideo1, this.video1Grabber)
        val video2Images = getFrames(amountVideo2, this.video2Grabber)

        // execute the alignment algorithm with the images of both videos
        val result = algorithm.run(video1Images.toTypedArray(), video2Images.toTypedArray())
        for (a in result) {
            when (a) {
                AlignmentElement.MATCH -> {
                    val differences = getDifferences(video1Images[0], video2Images[0])
                    encoder.record(differences)
                    alignment += AlignmentElement.MATCH
                }
                AlignmentElement.INSERTION -> {
                    encoder.record(getColoredFrame(Color.GREEN))
                    // skipping the second video's frame (insertion)
                    video2Images.removeAt(0)
                    alignment += AlignmentElement.INSERTION
                }
                AlignmentElement.DELETION -> {
                    encoder.record(getColoredFrame(Color.BLUE))
                    // skipping the first video's frame (deletion)
                    video1Images.removeAt(0)
                    alignment += AlignmentElement.DELETION
                }
            }
        }
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

            if (blue1 != blue2 || green1 != green2 || red1 != red2) {
                differencesData.data[index] = 0x00.toByte() // blue
                differencesData.data[index + 1] = 0x00.toByte() // green
                differencesData.data[index + 2] = 0xFF.toByte() // red
            }
            index += 3
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
            images.add(mask.apply(image))
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

    private fun hashFrame(frame: Frame): ByteArray {
        val image = (converter.getImage(frame).raster.dataBuffer as DataBufferByte).data
        val md5 = MessageDigest.getInstance("MD5")
        md5.update(image)
        return md5.digest()
    }

    private fun getHashedVideo(video: File): ArrayList<ByteArray> {
        val grabber = FFmpegFrameGrabber(video)
        grabber.start()
        var hashArray = ArrayList<ByteArray>()
        var frame = grabber.grabImage()
        while (frame != null) {
            hashArray.add(hashFrame(frame))
            frame = grabber.grabImage()
        }
        grabber.stop()

        // find duplicates
        val duplicates: Set<Int> = setOf()
        for (i in hashArray.indices) {
            for (j in i + 1 until hashArray.size) {
                if (hashArray[i].contentEquals(hashArray[j])) {
                    duplicates.plus(j)
                    duplicates.plus(i)
                }
            }
        }

        // remove duplicates
        hashArray = hashArray.filterIndexed { index, _ -> !duplicates.contains(index) } as ArrayList<ByteArray>
        return hashArray
    }

    private fun findEquals(
        video1Hashes: ArrayList<ByteArray>,
        video2Hashes: ArrayList<ByteArray>,
    ): ArrayList<Pair<Int, Int>> {
        val equals = ArrayList<Pair<Int, Int>>()
        // find all equal frames
        for (i in video1Hashes.indices) {
            for (j in video2Hashes.indices) {
                if (video1Hashes[i].contentEquals(video2Hashes[j])) {
                    equals.add(Pair(i, j)) // only one pair is possible
                    break
                }
            }
        }
        return equals
    }
}
