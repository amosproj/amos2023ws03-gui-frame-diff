import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import javax.imageio.ImageIO

/**
 * A class that implements the AbstractVideoGenerator interface to generate video files.
 *
 * @param videoPath The path to the output video file.
 * @param imageWidth The width of each frame in the video.
 * @param imageHeight The height of each frame in the video.
 */
class VideoGeneratorImpl(
    private val videoPath: String,
    private val imageWidth: Int,
    private val imageHeight: Int,
) : AbstractVideoGenerator(videoPath, imageWidth, imageHeight) {
    private val recorder: FFmpegFrameRecorder = initializeRecorder()

    init {
        recorder.start()
    }

    /**
     * Loads a frame from the given byte array.
     *
     * @param frameBytes the byte array containing the frame data
     */
    override fun loadFrame(frameBytes: ByteArray) {
        ByteArrayInputStream(frameBytes).use { stream ->
            val bufferedImage = ImageIO.read(stream)

            // Getting pixel data
            val width = bufferedImage.width
            val height = bufferedImage.height
            val pixels = IntArray(width * height)
            bufferedImage.getRGB(0, 0, width, height, pixels, 0, width)

            // Converting ARGB pixel data to BGR
            val frame = Frame(width, height, Frame.DEPTH_UBYTE, 3)
            val frameData = ByteArray(width * height * 3)

            for (i in 0 until width * height) {
                val biHeight = i / width
                val biWidth = i % width
                val fiIndex = (biHeight * width + biWidth) * 3
                frameData[fiIndex] = (pixels[i] and 0xff).toByte() // B
                frameData[fiIndex + 1] = (pixels[i] shr 8 and 0xff).toByte() // G
                frameData[fiIndex + 2] = (pixels[i] shr 16 and 0xff).toByte() // R
            }

            frame.image[0] = ByteBuffer.wrap(frameData)
            frame.imageStride = width * 3 // ensure the stride is set

            // Record the converted Frame
            recorder.record(frame)
        }
    }

    /**
     * This method stops the ongoing recording and releases the resources used by the recorder.
     */
    override fun save() {
        recorder.stop()
        recorder.release()
    }

    /**
     * Initializes the recorder with the specified video path, image width, and image height.
     *
     * @return an instance of FFmpegFrameRecorder initialized with the specified parameters.
     */
    private fun initializeRecorder() =
        FFmpegFrameRecorder(videoPath, imageWidth, imageHeight).apply {
            videoCodec = avcodec.AV_CODEC_ID_FFV1
            format = "matroska"
            frameRate = 25.0
        }
}
