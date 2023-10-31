/**
 * Abstract class for video generators.
 * Extend this class to create your own video generator.
 *
 * @param outputPath The path to the output video.
 */


abstract class AbstractVideoGenerator(private val outputPath: String) {
    private val videoPath: String = outputPath

    /**
     * Initializes a video generator.
     * Saves the generator into a private variable to be reused.
     *
     * Initializes an output video instance.
     * Saves the instance into a private variable to be reused.
     *
     * Override this method to initialize your video generator. Otherwise ignore.
     */
    fun init() {
    }


    /**
     * Adds a single frame to the video.
     * This is where you should add a frame to the video.
     * If possible reuse variables from the init() method.
     *
     * @param framePath The path to the frame to be added. Image type is dynamic.
     */
    abstract fun addFrame(framePath: String)


    /**
     * Adds multiple frames to the video.
     * Reuses the addFrame() method.
     *
     *
     * @param framePaths The paths to the frames to be added. Image type is dynamic.
     */
    fun addFrames(framePaths: List<String>) {
        for (framePath in framePaths) {
            addFrame(framePath)
        }
    }

    /**
     * Saves the video to the output path.
     * This is where you should save the video.
     * If possible reuse variables from the init() method.
     */
    abstract fun save()

}