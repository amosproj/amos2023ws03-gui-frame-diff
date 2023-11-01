import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists

/**
 * Abstract class for video generators.
 * Extend this class to create your own video generator.
 *
 * @param outputPath The path to the output video.
 */

abstract class AbstractVideoGenerator(private val outputPath: String, private val deleteInput: Boolean = false) {
    private val videoPath: String = outputPath
    private val inputDeletion: Boolean = deleteInput

    /**
     * Initializes a video generator.
     * Saves the generator into a private variable to be reused.
     *
     * Initializes an output video instance.
     * Saves the instance into a private variable to be reused.
     *
     * Override this method to initialize your video generator. Otherwise, ignore.
     */
    fun init() {
    }

    /**
     * Deletes the input image.
     */
    fun deleteInput(path: String) {
        var result = Path(path).deleteIfExists()
        if (!result) {
            println("Failed to delete input image with path: $path.")
        }
    }

    /**
     * Adds a single frame to the video.
     * This is where you should add a frame to the video.
     * If possible reuse variables from the init() method.
     *
     * @param framePath The path to the frame to be added. Image type is dynamic.
     * @param deleteInput Whether to delete the input image after adding it to the video.
     */
    abstract fun addFrame(
        framePath: String,
        deleteInput: Boolean = this.inputDeletion,
    )

    /**
     * Adds multiple frames to the video.
     * Reuses the addFrame() method.
     *
     *
     * @param framePaths The paths to the frames to be added. Image type is dynamic.
     * @param deleteInput Whether to delete the input images after adding them to the video.
     */
    fun addFrames(
        framePaths: List<String>,
        deleteInput: Boolean = this.inputDeletion,
    ) {
        for (framePath in framePaths) {
            addFrame(framePath, deleteInput)
        }
    }

    /**
     * Saves the video to the output path.
     * This is where you should save the video.
     * If possible reuse variables from the init() method.
     * @return The path to the output video.
     */
    abstract fun save(): String
}
