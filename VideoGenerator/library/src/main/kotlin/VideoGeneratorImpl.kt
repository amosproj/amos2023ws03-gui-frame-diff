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
     */
    override fun processFrames() {
        val recorder: FFmpegFrameRecorder =
            FFmpegFrameRecorder(videoPath, imageWidth, imageHeight).apply {
                videoCodec = avcodec.AV_CODEC_ID_FFV1
                format = "matroska"
                frameRate = 25.0
            }

        val converter = Java2DFrameConverter()

        try {
            recorder.start()
            while (queue.isNotEmpty()) {
                val image = queue.poll()
                ByteArrayInputStream(image).use { bis ->
                    val bImage = ImageIO.read(bis)
                    val frame = converter.convert(bImage)
                    recorder.record(frame)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            recorder.stop()
            recorder.release()
        }
    }

    override fun save() {}
}
