package algorithms

import MetricInterface

/**
 * Represents a position in a sequence alignment.
 * The names are chosen from the perspective of the first sequence of the alignment.
 *
 * MATCH: the two objects in the sequence are associated with each other.
 * INSERTION: the object in the second sequence is not contained in the first sequence.
 * DELETION: the object in the first sequence is not contained in the second sequence.
 */
enum class AlignmentElement {
    MATCH,
    INSERTION,
    DELETION
}

/**
 * An abstract class for alignment algorithms.
 *
 * @param T the type of the objects to align
 * @param metric the metric to use for calculating the distance between two objects
 */
abstract class AlignmentAlgorithm<T>(metric: MetricInterface<T>) {
    /**
     * The function to execute the algorithm on the given sequences of objects.
     *
     * @param a the first sequence of objects
     * @param b the second sequence of objects
     *
     * Returns an array of [AlignmentElement]s that represent the alignment between the two sequences.
     */
    abstract fun run(
        a: Array<T>,
        b: Array<T>,
    ): Array<AlignmentElement>
}
