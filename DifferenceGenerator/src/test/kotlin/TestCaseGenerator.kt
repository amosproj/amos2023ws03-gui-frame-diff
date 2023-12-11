import algorithms.AlignmentElement
import java.io.File
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
                    AlignmentElement.PERFECT to
                        0.65f,
                ),
            AlignmentElement.DELETION to
                mapOf(
                    AlignmentElement.INSERTION to
                        0.05f,
                    AlignmentElement.DELETION to
                        0.2f,
                    AlignmentElement.PERFECT to
                        0.75f,
                ),
            AlignmentElement.PERFECT to
                mapOf(
                    AlignmentElement.INSERTION to
                        0.1f,
                    AlignmentElement.DELETION to
                        0.05f,
                    AlignmentElement.PERFECT to
                        0.85f,
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
        var result = ArrayList<AlignmentElement>()

        var currentElement = AlignmentElement.PERFECT

        while (position < baseLength) { // + Random.nextInt(-2, 2)
            // use transitionProbabilites to determine next element
            // generate random float between 0 and 1
            val randomFloat = Random.nextFloat()

            // get the next element based on the random float
            val nextElement =
                when {
                    randomFloat <
                        transitionProbabilities[
                            currentElement,
                        ]!![
                            AlignmentElement.INSERTION,
                        ]!! -> {
                        AlignmentElement.INSERTION
                    }
                    randomFloat <
                        transitionProbabilities[
                            currentElement,
                        ]!![
                            AlignmentElement.INSERTION,
                        ]!! +
                        transitionProbabilities[
                            currentElement,
                        ]!![
                            AlignmentElement.DELETION,
                        ]!! -> {
                        AlignmentElement.DELETION
                    }
                    else -> {
                        AlignmentElement.PERFECT
                    }
                }
            // add the next element to the result
            result.add(nextElement)

            // calculate the chance by which to modify the screenshot
            // such that in average one screenshot is modified
            val modify = Random.nextFloat() < 1 / baseLength

            // add frames to the videos
            when (nextElement) {
                AlignmentElement.INSERTION -> {
                    videoGenerator2.loadFrame(getByteArray(position + 1, false))
                }
                AlignmentElement.DELETION -> {
                    videoGenerator1.loadFrame(getByteArray(position + 1, false))
                }
                AlignmentElement.PERFECT -> {
                    videoGenerator1.loadFrame(getByteArray(position + 1, false))
                    videoGenerator2.loadFrame(
                        getByteArray(
                            position + 1,
                            modify,
                        ),
                    )
                }
                else -> throw Exception("Invalid alignment element")
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
}
