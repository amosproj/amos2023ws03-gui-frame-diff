package algorithms

import DifferenceGeneratorStoppedException
import wrappers.ResettableIterable

/**
 * Represents a position in a sequence alignment.
 * The names are chosen from the perspective of the first sequence of the alignment.
 *
 * MATCH: the two objects in the sequence are associated with each other.
 * INSERTION: the object in the second sequence is not contained in the first sequence.
 * DELETION: the object in the first sequence is not contained in the second sequence.
 * PERFECT: the two objects in the sequence are equal.
 */
enum class AlignmentElement {
    MATCH,
    INSERTION,
    DELETION,
    PERFECT,
}

/**
 * A singleton class that can be used to signal the cancellation of an algorithm run.
 *
 * An algorithm implementation can check if the algorithm is still supposed to run by calling [isAlive].
 * As this is a singleton, it is expected that only one algorithm instance is running at a time.
 * After an algorithm has been cancelled, or before a new instance is started, [reset] should be called
 * to avoid false positives.
 *
 * Inspired by this article: https://www.baeldung.com/kotlin/singleton-classes
 */
class AlgorithmExecutionState private constructor() {
    companion object {
        private var instance: AlgorithmExecutionState? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AlgorithmExecutionState().also { instance = it }
            }
    }

    private var isAlive = true

    /**
     * Resets the state of the singleton.
     *
     * This prepares for a new algorithm run.
     */
    fun reset() {
        isAlive = true
    }

    /**
     * Signals a stop to the running algorithm.
     *
     * This does not immediately stop the algorithm, but the algorithm can check if it is still supposed to run.
     * The algorithm checks if it is still alive regularly. So, it might take a short time until the algorithm
     * actually stops.
     */
    fun stop() {
        isAlive = false
    }

    /**
     * Checks if the algorithm running thread is still wanted by the user.
     */
    fun isAlive(): Boolean {
        return isAlive
    }
}

/**
 * An abstract class for alignment algorithms.
 *
 * @param T the type of the objects to align
 */
abstract class AlignmentAlgorithm<T> {
    /**
     * The function to execute the algorithm on the given sequences of objects.
     *
     * @param a the first sequence of objects
     * @param b the second sequence of objects
     *
     * Returns an array of [AlignmentElement]s that represent the alignment between the two sequences.
     */
    fun run(
        a: Array<T>,
        b: Array<T>,
    ): Array<AlignmentElement> {
        return run(a.toCollection(ArrayList()), b.toCollection(ArrayList()))
    }

    abstract fun run(
        a: ArrayList<T>,
        b: ArrayList<T>,
    ): Array<AlignmentElement>

    abstract fun run(
        a: ResettableIterable<T>,
        b: ResettableIterable<T>,
    ): Array<AlignmentElement>

    /**
     * Function that checks if the algorithm is still supposed to run.
     *
     * If not, it throws a [DifferenceGeneratorStoppedException].
     */
    fun isAlive() {
        if (!AlgorithmExecutionState.getInstance().isAlive()) {
            throw DifferenceGeneratorStoppedException("The difference computation was stopped")
        }
    }
}
