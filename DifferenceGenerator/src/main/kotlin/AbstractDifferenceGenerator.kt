/**
 * Abstract class for the DifferenceGenerator.
 *
 * @param videoReferencePath the path to the reference video
 * @param videoCurrentPath the path to the current video
 * @param outputPath the path to the output file
 */
abstract class AbstractDifferenceGenerator {
    /**
     * Generates the difference between the two videos.
     *
     * Calls the saveDifferences() method.
     */
    abstract fun generateDifference()
}
