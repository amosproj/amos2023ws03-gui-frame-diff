package algorithms

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
}
