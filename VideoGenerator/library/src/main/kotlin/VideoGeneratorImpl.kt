
import java.util.LinkedList
import java.util.Queue

class VideoGeneratorImpl(videoPath: String, imageWidth: Int, imageHeight: Int) : AbstractVideoGenerator(
    videoPath,
    imageWidth,
    imageHeight,
) {
    val queue: Queue<ByteArray> = LinkedList()

    /**
     * Appends the image bytes to the internal queue for further processing.
     */
    override fun loadFrame(frameBytes: ByteArray) {
        queue.add(frameBytes)
    }

    override fun save() {
        // To be implemented
    }
}
