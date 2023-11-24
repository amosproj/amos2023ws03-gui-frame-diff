/**
 * Abstract class for video generators.
 * Extend this class to create your own video generator.
 *
 * @param videoPath Output filesystem path for the resulting video.
 */

abstract class AbstractVideoGenerator(videoPath: String) {
    /**
     * Endpoint to load images into the generator.
     *
     * @param frameBytes A byte array containing image data.
     */
    abstract fun loadFrame(frameBytes: ByteArray)

    /**
     * Saves the video to the output path.
     * This is where you should save the video.
     * If possible reuse variables from the init() method.
     */
    abstract fun save()
}
