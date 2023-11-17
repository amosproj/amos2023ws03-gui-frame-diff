import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Java2DFrameConverter
import java.io.ByteArrayInputStream
import java.util.*
import javax.imageio.ImageIO

class VideoGeneratorImpl(private val videoPath: String, private val imageWidth: Int, private val imageHeight: Int) : AbstractVideoGenerator(
    videoPath,
    imageWidth,
    imageHeight,
) {
    private val queue: Queue<ByteArray> = LinkedList()

    /**
     * Here, you can initialize your complex member object and
     * make sure that the instance is prepared for incoming frames.
     */
    init {}

    /**
     * Appends the image bytes to the internal queue for further processing.
     */
    override fun loadFrame(frameBytes: ByteArray) {
        queue.add(frameBytes)
    }

    /**
     * Process frames from a queue and record them into a video file.
     *
     * @throws Exception if any error occurs during the processing.
     */
    override fun processFrames() {
        // FFmpegFrameRecorder is created (class provided by JavaCV library)
        val recorder: FFmpegFrameRecorder =
            FFmpegFrameRecorder(videoPath, imageWidth, imageHeight).apply {
                videoCodec = avcodec.AV_CODEC_ID_FFV1
                format = "matroska"
                frameRate = 25.0
            }

        // Java2DFrameConverter is created (allowing conversion between Java's
        // native image data structure (BufferedImage) and JavaCV's Frame object)
        val converter = Java2DFrameConverter()

        try {
            recorder.start()
            while (queue.isNotEmpty()) {
                // fetch image from the queue
                val image = queue.poll()
                ByteArrayInputStream(image).use { bis ->
                    // 'image' bytes are read and converted to BufferedImage object 'bImage'
                    val bImage = ImageIO.read(bis)
                    // BufferedImage object is converted to Frame object
                    val frame = converter.convert(bImage)
                    // Frame object is recorded into video file
                    recorder.record(frame)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // video recording is stopped and the resources are released
            recorder.stop()
            recorder.release()
        }
    }

    override fun save() {}
}
