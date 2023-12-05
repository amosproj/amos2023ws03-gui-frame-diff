package algorithms

import hashing.ObjectHasher
import wrappers.ResettableArrayListIterator
import wrappers.ResettableIterable

/**
 * An alignment algorithm that uses a divide and conquer approach to align two videos.
 *
 *
 * @param T The type of the frames of the videos.
 * @param algorithm The alignment algorithm to use for the sub-alignments.
 * @param hasher The hasher to use for the sub-alignments.
 */
class DivideAndConquerAligner<T>(private val algorithm: AlignmentAlgorithm<T>, private val hasher: ObjectHasher<T>) :
    AlignmentAlgorithm<T>() {
    private lateinit var hashes1: Array<ByteArray>
    private lateinit var hashes2: Array<ByteArray>
    private val alignment: ArrayList<AlignmentElement> = ArrayList()

    override fun run(
        a: ArrayList<T>,
        b: ArrayList<T>,
    ): Array<AlignmentElement> {
        return run(ResettableArrayListIterator(a), ResettableArrayListIterator(b))
    }

    /**
     * Aligns two sequences by divide and conquer.
     * The dividing happens by finding exact matches using the hasher.
     * If there are two or more objects with the same hash, they are not considered for matches, as we cant be sure
     * about which match to take. This happens in the [markDuplicates] function.
     *
     * The conquer step is done by the given alignment algorithm.
     *
     * @param a The first sequence given as a [ResettableIterable].
     * @param b The second sequence given as a [ResettableIterable].
     * @return The alignment of the two sequences.
     */
    override fun run(
        a: ResettableIterable<T>,
        b: ResettableIterable<T>,
    ): Array<AlignmentElement> {
        alignment.clear() // clear in case the instance is reused

        // get hashes and mark duplicates
        hashes1 = markDuplicates(hasher.getHashes(a))
        hashes2 = markDuplicates(hasher.getHashes(b))

        // find unique exact matches between the two sequences
        val equals = findMatches()

        var lastMatchIndex1 = 0
        var lastMatchIndex2 = 0

        // reset the sequence iterators to the start
        a.reset()
        b.reset()

        for (matchPair in equals) {
            // create sub-sequences up to the next match to be aligned
            val subArray1 = a.take(matchPair.first - lastMatchIndex1)
            val subArray2 = b.take(matchPair.second - lastMatchIndex2)

            // create alignment up to the next match
            alignment.addAll(getSubAlignment(subArray1, subArray2))

            // insert a match for the currently selected exact match
            alignment += AlignmentElement.MATCH

            // update last match indices
            lastMatchIndex1 = matchPair.first + 1
            lastMatchIndex2 = matchPair.second + 1

            // advance iterators to skip the current match's positions
            a.next()
            b.next()
        }

        // process alignment after last known match
        val subArray1 = a.take(a.size() - lastMatchIndex1)
        val subArray2 = b.take(b.size() - lastMatchIndex2)
        alignment.addAll(getSubAlignment(subArray1, subArray2))

        return alignment.toTypedArray()
    }

    /**
     * Aligns two sequences by the given alignment algorithm.
     *
     * For performance reasons, we use shortcuts if one of the sequences is empty.
     */
    private fun getSubAlignment(
        slice1: List<T>,
        slice2: List<T>,
    ): Array<AlignmentElement> {
        return if (slice1.isEmpty() && slice2.isEmpty()) {
            arrayOf()
        } else if (slice1.isEmpty()) {
            Array(slice2.size) { AlignmentElement.INSERTION }
        } else if (slice2.isEmpty()) {
            Array(slice1.size) { AlignmentElement.DELETION }
        } else {
            // gather alignment between two exact matches (or start and first match)
            // this runs the underlying alignment algorithm aka this is the divide and conquer step
            algorithm.run(ArrayList(slice1), ArrayList(slice2))
        }
    }

    /**
     * Marks duplicates in the given array of hashes.
     *
     * @param hashArray The array of hashes to mark duplicates in.
     * @return The array of hashes with duplicates marked as empty byte-arrays.
     */
    private fun markDuplicates(hashArray: Array<ByteArray>): Array<ByteArray> {
        val duplicates: Set<Int> = setOf()
        for (i in hashArray.indices) {
            for (j in i + 1 until hashArray.size) {
                // if two hashes are equal, put the indices of both in the duplicates set
                if (hashArray[i].contentEquals(hashArray[j])) {
                    duplicates.plus(j)
                    duplicates.plus(i)
                }
            }
        }

        // mark duplicates with a zero-length byte-array
        return hashArray.mapIndexed { index, x -> if (index in duplicates) ByteArray(0) else x }.toTypedArray()
    }

    /**
     * Finds all exact matches between the two sequences.
     *
     * Skips all frames that are marked as duplicates (zero length [ByteArray]s).
     *
     * @return An array of pairs of indices of the exact matches.
     */
    private fun findMatches(): Array<Pair<Int, Int>> {
        val equals = ArrayList<Pair<Int, Int>>()
        // find all equal frames that are not marked as duplicates
        for (i in hashes1.indices) {
            // skip if marked as duplicate
            if (hashes1[i].isEmpty()) continue
            for (j in hashes2.indices) {
                if (hashes1[i].contentEquals(hashes2[j])) {
                    equals.add(Pair(i, j)) // only one pair is possible
                    break
                }
            }
        }

        return equals.toTypedArray()
    }
}
