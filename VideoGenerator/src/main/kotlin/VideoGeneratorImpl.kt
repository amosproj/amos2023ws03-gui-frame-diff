import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.ffmpeg.global.avutil
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.time.LocalDateTime
import javax.imageio.ImageIO

/**
 * A class that implements the AbstractVideoGenerator interface to generate video files.
 *
 * @param videoPath The path to the output video file.
 */
class VideoGeneratorImpl(
    private val videoPath: String,
) : AbstractVideoGenerator(videoPath) {
    private lateinit var recorder: FFmpegFrameRecorder
    var codecId: Int = avcodec.AV_CODEC_ID_FFV1
    var videoFormat: String = "matroska"
    var codecOptions: Map<String, String> = mapOf("" to "")

    init {
        // turn off verbose ffmpeg output
        avutil.av_log_set_level(avutil.AV_LOG_QUIET)
    }

    /**
     * Loads a frame from the given byte array.
     * Note, that this function is specifically using javax.ImageIO and is thus not suitable
     * for Android projects. Use the `loadFrame(IntArray, Int, Int)` overload for that instead.
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

            loadFrame(pixels, width, height)
        }
    }

    /**
     * Loads a frame from a pixel buffer.
     * Initializes recorder if not yet initialized.
     *
     * @param pixels the IntArray with image pixels. Encoding is expected to be ARGB
     * @param width the width of the frame
     * @param height the height of the frame
     */
    fun loadFrame(
        pixels: IntArray,
        width: Int,
        height: Int,
    ) {
        // initialize and start Recorder
        if (!::recorder.isInitialized) {
            recorder = initializeRecorder(width, height)
            recorder.setMetadata("creation_time", LocalDateTime.now().toString())
            recorder.start()
        }

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

    /**
     * This method stops the ongoing recording and releases the resources used by the recorder.
     */
    override fun save() {
        if (!::recorder.isInitialized) {
            throw RuntimeException("Recorder was never initialized - no video created!")
        }

        recorder.stop()
        recorder.release()
    }

    /**
     * Initializes the recorder with the specified video path, image width, and image height.
     *
     * @return an instance of FFmpegFrameRecorder initialized with the specified parameters.
     */
    private fun initializeRecorder(
        imageWidth: Int,
        imageHeight: Int,
    ) = FFmpegFrameRecorder(videoPath, imageWidth, imageHeight).apply {
        videoCodec = codecId
        format = videoFormat
        frameRate = 25.0
        videoOptions = codecOptions
    }
}
