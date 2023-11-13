
import java.util.LinkedList
import java.util.Queue

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
    init { }

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
