/**
 * Abstract class for the DifferenceGenerator.
 *
 * @param video1Path the path to the first video
 * @param video2Path the path to the second video
 * @param outputPath the path to the output file
 */


abstract class AbstractDifferenceGenerator(video1Path: String, video2Path: String, outputPath: String)  {
    private val video1Path: String = video1Path
    private val video2Path: String = video2Path
    private val outputPath: String = outputPath


    /**
     * Loads the video1 and video2 files into memory.
     * Saves them into local variables.
     *
     * Calls the generateDifference() method!!!
     *
     * @return the video1Path
     */
    abstract fun init()

    /**
     * Generates the difference between the two videos.
     *
     * Calls the saveDifferences() method.
     *
     *
     */
    abstract fun generateDifference()

    /**
     * Saves the differences to the output file.
     *
     * @return the video1Path
     */
    abstract fun saveDifferences()


}