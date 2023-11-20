import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Java2DFrameConverter
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import java.io.ByteArrayInputStream
import java.util.*
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
    private val converter = Java2DFrameConverter()

    init {
        recorder.start()
    }

    /**
     * Appends an image to the video file.
     *
     * @param frameBytes the byte array containing the image data
     */
    override fun loadFrame(frameBytes: ByteArray) {
        ByteArrayInputStream(frameBytes).use { bis ->
            val bImage = ImageIO.read(bis)
            bImage?.let {
                recordFrame(it)
            }
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
    private fun initializeRecorder() = FFmpegFrameRecorder(videoPath, imageWidth, imageHeight).apply {
        videoCodec = avcodec.AV_CODEC_ID_FFV1
        format = "matroska"
        frameRate = 25.0
    }

    /**
     * Records a frame.
     *
     * @param bImage the BufferedImage representing the frame to be recorded
     */
    private fun recordFrame(bImage: BufferedImage) {
        val frame = converter.convert(bImage)
        recorder.record(frame)
    }
}
