package algorithms

import hashing.ObjectHasher
import wrappers.ResettableArrayListIterator
import wrappers.ResettableIterable

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

    override fun run(
        a: ResettableIterable<T>,
        b: ResettableIterable<T>,
    ): Array<AlignmentElement> {
        alignment.clear() // clear in case the instance is reused

        hashes1 = removeDuplicates(hasher.getHashes(a))
        hashes2 = removeDuplicates(hasher.getHashes(b))

        a.reset()
        b.reset()

        val equals = findMatches(hashes1, hashes2)

        var nextGrabbedFrame1 = 0
        var nextGrabbedFrame2 = 0

        // process frames from (0,0) until last match in equals
        for (matchPair in equals) {
            val video1Frames = a.take(matchPair.first - nextGrabbedFrame1)
            val video2Frames = b.take(matchPair.second - nextGrabbedFrame2)

            alignment.addAll(getSubAlignment(video1Frames, video2Frames))

            // insert a match for the exact match
            alignment += AlignmentElement.MATCH

            nextGrabbedFrame1 = matchPair.first + 1
            nextGrabbedFrame2 = matchPair.second + 1
            a.next()
            b.next()
        }

        // process alignment after last match
        val video1Frames = a.take(a.size() - nextGrabbedFrame1)
        val video2Frames = b.take(b.size() - nextGrabbedFrame2)
        alignment.addAll(getSubAlignment(video1Frames, video2Frames))

        return alignment.toTypedArray()
    }

    private fun getSubAlignment(
        slice1: List<T>,
        slice2: List<T>,
    ): Array<AlignmentElement> {
        return if (slice1.isEmpty()) {
            Array(slice2.size) { AlignmentElement.INSERTION }
        } else if (slice2.isEmpty()) {
            Array(slice1.size) { AlignmentElement.DELETION }
        } else {
            // gather alignment between two exact matches (or start and first match)
            // this runs the underlying alignment algorithm aka this is the divide and conquer step
            algorithm.run(ArrayList(slice1), ArrayList(slice2))
        }
    }

    private fun removeDuplicates(hashArray: Array<ByteArray>): Array<ByteArray> {
        // find duplicates
        val duplicates: Set<Int> = setOf()
        for (i in hashArray.indices) {
            for (j in i + 1 until hashArray.size) {
                if (hashArray[i].contentEquals(hashArray[j])) {
                    duplicates.plus(j)
                    duplicates.plus(i)
                }
            }
        }

        // remove duplicates
        return hashArray.filterIndexed { index, _ -> !duplicates.contains(index) }.toTypedArray()
    }

    private fun findMatches(
        hashes1: Array<ByteArray>,
        hashes2: Array<ByteArray>,
    ): Array<Pair<Int, Int>> {
        val equals = ArrayList<Pair<Int, Int>>()
        // find all equal frames
        for (i in this.hashes1.indices) {
            for (j in this.hashes2.indices) {
                if (this.hashes1[i].contentEquals(this.hashes2[j])) {
                    equals.add(Pair(i, j)) // only one pair is possible
                    break
                }
            }
        }
        return equals.toTypedArray()
    }
}
