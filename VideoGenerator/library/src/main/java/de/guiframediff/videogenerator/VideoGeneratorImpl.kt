import android.media.Image

import java.io.ByteArrayInputStream
import java.util.LinkedList
import java.util.Queue


class VideoGeneratorImpl(outputPath: String) : AbstractVideoGenerator(outputPath) {
    val queue: Queue<Image> = LinkedList()

/**
     * Initializes a video generator.
     * Saves the generator into a private variable to be reused.
     *
     * Initializes an output video instance.
     * Saves the instance into a private variable to be reused.
     *
     * Override this method to initialize your video generator. Otherwise, ignore.*/


    init { }
/*
*
     * Converts the given byte array to an internal image.
     * Appends that image to the internal queue for further processing
     */

    override fun loadFrame(frameBytes: Array<Byte>) {
        val stream: ByteArrayInputStream = ByteArrayInputStream(frameBytes.toByteArray())
        //queue.add(ImageIO.read(stream))

        // TODO: Decide if to immediately append the image, or if that should be done by a separate call.
    }

    override fun save() {
        // To be implemented
    }
}
