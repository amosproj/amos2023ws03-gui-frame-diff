import algorithms.AlignmentElement
import java.io.File
import java.io.FilenameFilter
import kotlin.random.Random

/**
 * Class for generating test cases for the DifferenceGenerator.
 * @param path1 The path of the first video.
 * @param path2 The path of the second video.
 * @param baseLength The base length of the videos (defaulted to 30).
 */
class TestCaseGenerator(
    private val path1: String,
    private val path2: String,
    private val baseLength: Int = 30,
) {
    /**
     * Represents the transition probabilities between alignment elements. The transition
     * probabilities are stored in a nested map structure. The outer map represents the source
     * alignment element, and the inner map represents the target alignment element along with
     * its corresponding probability. The probabilities are arbitrarily chosen but each row must
     * sum to 1.
     *
     * @property transitionProbabilities The map of transition probabilities.
     */
    private val transitionProbabilities: Map<AlignmentElement, Map<AlignmentElement, Float>> =
        mapOf(
            AlignmentElement.INSERTION to
                mapOf(
                    AlignmentElement.INSERTION to
                        0.3f,
                    AlignmentElement.DELETION to
                        0.05f,
                    AlignmentElement.MATCH to 0.05f,
                    AlignmentElement.PERFECT to
                        0.6f,
                ),
            AlignmentElement.DELETION to
                mapOf(
                    AlignmentElement.INSERTION to
                        0.05f,
                    AlignmentElement.DELETION to
                        0.2f,
                    AlignmentElement.MATCH to 0.05f,
                    AlignmentElement.PERFECT to
                        0.7f,
                ),
            AlignmentElement.MATCH to
                mapOf(
                    AlignmentElement.INSERTION to
                        0.05f,
                    AlignmentElement.DELETION to
                        0.2f,
                    AlignmentElement.MATCH to 0.1f,
                    AlignmentElement.PERFECT to
                        0.65f,
                ),
            AlignmentElement.PERFECT to
                mapOf(
                    AlignmentElement.INSERTION to
                        0.1f,
                    AlignmentElement.DELETION to
                        0.05f,
                    AlignmentElement.MATCH to 0.05f,
                    AlignmentElement.PERFECT to
                        0.8f,
                ),
        )

    /**
     * Generates a random test case.
     * @return The Alignment of the two videos.
     */
    fun generateRandomTestCase(): Array<AlignmentElement> {
        val videoGenerator1 = VideoGeneratorImpl(path1)
        val videoGenerator2 = VideoGeneratorImpl(path2)

        var position = 0
        val result = ArrayList<AlignmentElement>()

        var currentElement = AlignmentElement.PERFECT

        while (position < baseLength) { // + Random.nextInt(-2, 2)
            // use transitionProbabilities to determine next element
            // generate random float between 0 and 1
            val randomFloat = Random.nextFloat()

            // safe the cutoff values for the different alignment elements
            val currentElementToInsertionThreshold =
                transitionProbabilities[
                    currentElement,
                ]!![
                    AlignmentElement.INSERTION,
                ]!!
            val currentElementToDeletionThreshold =
                currentElementToInsertionThreshold +
                    transitionProbabilities[
                        currentElement,
                    ]!![
                        AlignmentElement.DELETION,
                    ]!!

            val currentElementToMatchThreshold =
                currentElementToDeletionThreshold +
                    transitionProbabilities[
                        currentElement,
                    ]!![
                        AlignmentElement.MATCH,
                    ]!!
            // determine the next element
            val nextElement =
                when {
                    randomFloat <
                        currentElementToInsertionThreshold -> {
                        AlignmentElement.INSERTION
                    }
                    randomFloat <
                        currentElementToDeletionThreshold -> {
                        AlignmentElement.DELETION
                    }
                    randomFloat <
                        currentElementToMatchThreshold -> {
                        if (modifiedScreenshotExists(position + 1)) {
                            AlignmentElement.MATCH
                        } else {
                            AlignmentElement.PERFECT
                        }
                    }
                    else -> {
                        AlignmentElement.PERFECT
                    }
                }
            // add the next element to the result
            result.add(nextElement)

            // add frames to the videos
            when (nextElement) {
                AlignmentElement.INSERTION -> {
                    videoGenerator2.loadFrame(getByteArray(position + 1, false))
                }
                AlignmentElement.DELETION -> {
                    videoGenerator1.loadFrame(getByteArray(position + 1, false))
                }
                AlignmentElement.MATCH -> {
                    videoGenerator1.loadFrame(getByteArray(position + 1, false))
                    videoGenerator2.loadFrame(getByteArray(position + 1, true))
                }
                AlignmentElement.PERFECT -> {
                    videoGenerator1.loadFrame(getByteArray(position + 1, false))
                    videoGenerator2.loadFrame(getByteArray(position + 1, false))
                }
            }

            position++
            currentElement = nextElement
        }
        // save the videos
        videoGenerator1.save()
        videoGenerator2.save()

        return result.toTypedArray()
    }

    /**
     * Returns a byte array representing the screenshot with the given index.
     *
     * @param index The index of the screenshot.
     * @param modified Indicates whether to return the modified version of the screenshot.
     * @return The byte array representing the screenshot.
     */
    private fun getByteArray(
        index: Int,
        modified: Boolean,
    ): ByteArray {
        // load Screenshot with index from test resources path
        val pathPrefix = "src/test/resources/"
        val folder = if (modified) "screenModified/" else "screen/"
        val paddedIndex = index.toString().padStart(5, '0')

        // load the screenshot
        val screenshot = File(pathPrefix + folder + "Screenshot$paddedIndex.png")
        val byteArray = screenshot.readBytes()

        // return the byte array
        return byteArray
    }

    /**
     * Checks if a modified screenshot exists for a given index.
     * @param index The index of the screenshot.
     * @return True if a modified screenshot exists, false otherwise.
     */
    private fun modifiedScreenshotExists(index: Int): Boolean {
        val pathPrefix = "src/test/resources/"
        val folder = "screenModifiedOnly/"
        val paddedIndex = index.toString().padStart(5, '0')

        // create filename filter
        val fileNameFilter =
            FilenameFilter { _, name ->
                name.startsWith("Screenshot$paddedIndex")
            }
        // check if a file matching the pattern exists
        val matchingFiles = File(pathPrefix + folder).listFiles(fileNameFilter)

        if (matchingFiles != null) {
            if (matchingFiles.size > 1) {
                throw Exception("There should only be one screenshot with the same index.")
            }
            return matchingFiles.isNotEmpty()
        }
        return false
    }
}
