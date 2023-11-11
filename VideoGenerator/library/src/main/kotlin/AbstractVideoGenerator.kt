/**
 * Abstract class for video generators.
 * Extend this class to create your own video generator.
 *
 * @param videoPath Output filesystem path for the resulting video.
 * @param imageWidth Horizontal number of pixels to be expected in images.
 * @param imageHeight Vertical number of pixels to be expected in images.
 */

abstract class AbstractVideoGenerator(videoPath: String, imageWidth: Int, imageHeight: Int) {
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
