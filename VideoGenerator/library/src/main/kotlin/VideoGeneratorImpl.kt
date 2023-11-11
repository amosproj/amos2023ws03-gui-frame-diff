
import java.util.LinkedList
import java.util.Queue

class VideoGeneratorImpl(videoPath: String, imageWidth: Int, imageHeight: Int) : AbstractVideoGenerator(
    videoPath,
    imageWidth,
    imageHeight,
) {
    private val _queue: Queue<ByteArray> = LinkedList()
    private val _videoPath: String = videoPath
    private val _imageWidth: Int = imageWidth
    private val _imageHeight: Int = imageHeight

    /**
     * Here, you can initialize your complex member object and
     * make sure that the instance is prepared for incoming frames.
     */
    init { }

    /**
     * Appends the image bytes to the internal queue for further processing.
     */
    override fun loadFrame(frameBytes: ByteArray) {
        _queue.add(frameBytes)
    }

    override fun save() {
        // To be implemented
    }
}
