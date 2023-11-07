import android.graphics.Bitmap

/**
 * Abstract class for video generators.
 * Extend this class to create your own video generator.
 *
 * @param outputPath The path to the output video.
 */

abstract class AbstractVideoGenerator(private val outputPath: String) {
    private val videoPath: String = outputPath

    /**
     * Adds a single frame to the video.
     * This is where you should add a frame to the video.
     *
     * @param image An image in the form of a Bitmap passed by the user.
     */
    abstract fun loadFrame(image: Bitmap)

    /**
     * Saves the video to the output path.
     * This is where you should save the video.
     * If possible reuse variables from the init() method.
     */
    abstract fun save()
}
