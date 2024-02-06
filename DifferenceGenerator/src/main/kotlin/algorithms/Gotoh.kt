// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: Simon Sasse <simonsasse97@gmail.com>
package algorithms

import MetricInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import wrappers.ResettableIterable

/**
 * An implementation of the Gotoh algorithm for sequence alignment.
 *
 * It uses a similar approach as the dynamic programming algorithm by Needleman and Wunsch. In
 * addition, it accounts for the gap size in the alignment. Depending on the hyperparameters, a
 * newly opened gap can be very costly ([gapOpenPenalty]), while extending an existing gap can be
 * made cheaper ([gapExtensionPenalty]).
 *
 * @param T the type of the objects to align
 * @param metric the metric to use for calculating the distance between two objects
 * @param gapOpenPenalty the penalty for opening a gap
 * @param gapExtensionPenalty the penalty for extending an existing gap
 */
class Gotoh<T>(
    private val metric: MetricInterface<T>,
    private val gapOpenPenalty: Double = 0.2,
    private val gapExtensionPenalty: Double = -0.8,
) : AlignmentAlgorithm<T>() {
    private lateinit var score: Array<DoubleArray>
    private lateinit var gapA: Array<DoubleArray>
    private lateinit var gapB: Array<DoubleArray>
    private lateinit var similarityM: Array<DoubleArray>
    private var m: Int = -1
    private var n: Int = -1

    /**
     * The function to execute the algorithm on the given sequences of objects.
     *
     * @param a the first sequence of objects
     * @param b the second sequence of objects
     *
     * Returns an array of [AlignmentElement]s that represent the alignment between the two
     * sequences.
     */
    override fun run(
        a: ArrayList<T>,
        b: ArrayList<T>,
    ): Array<AlignmentElement> {
        m = a.size
        n = b.size

        // initialize the dynamic programming matrices
        initialize()

        // execute the recursively defined algorithm
        execute(a, b)

        // traceback through the matrices to obtain the alignment sequence
        return traceback()
    }

    override fun run(
        a: ResettableIterable<T>,
        b: ResettableIterable<T>,
    ): Array<AlignmentElement> {
        return run(a.toList() as ArrayList<T>, b.toList() as ArrayList<T>)
    }

    private fun initialize() {
        // three matrices of the size (n + 1) x (m + 1) each
        // score: if the last pair was a match
        // gapA: if the last pair was a gap in the first sequence
        // gapB: if the last pair was a gap in the second sequence
        // similarityM: the similarity matrix for the two sequences (used for caching)
        score = Array(m + 1) { DoubleArray(n + 1) { 0.0 } }
        gapA = Array(m + 1) { DoubleArray(n + 1) { 0.0 } }
        gapB = Array(m + 1) { DoubleArray(n + 1) { 0.0 } }
        similarityM = Array(m + 1) { DoubleArray(n + 1) { 0.0 } }

        // initialize the matrices
        score[0][0] = 0.0
        gapA[0][0] = 0.0
        gapB[0][0] = 0.0

        // initialize the first row and column, for gapA with an accumulated gap penalty
        // we use negative infinity to account for impossible alignments in the first row/column
        for (i in 1..m) {
            score[i][0] = Double.NEGATIVE_INFINITY
            gapA[i][0] = gapOpenPenalty + (i - 1) * gapExtensionPenalty
            gapB[i][0] = Double.NEGATIVE_INFINITY
        }

        // initialize the first row and column, for gapB with an accumulated gap penalty
        for (j in 1..n) {
            score[0][j] = Double.NEGATIVE_INFINITY
            gapA[0][j] = Double.NEGATIVE_INFINITY
            gapB[0][j] = gapOpenPenalty + (j - 1) * gapExtensionPenalty
        }
    }

    private fun execute(
        a: ArrayList<T>,
        b: ArrayList<T>,
    ) {
        // fill the similarity matrix
        runBlocking {
            for (i in 1..m) {
                launch(Dispatchers.Default) {
                    for (j in 1..n) {
                        similarityM[i - 1][j - 1] = 1 - metric.measureDistance(a[i - 1], b[j - 1])
                    }
                }
            }
        }

        // main calculation loop, iterating over all rows and columns of the matrices
        for (i in 1..m) {
            for (j in 1..n) {
                // calculate the three possible scores for the current position
                gapA[i][j] =
                    maxOf(
                        // open a new gap in A
                        score[i - 1][j] + gapOpenPenalty,
                        // extend an existing gap in A
                        gapA[i - 1][j] + gapExtensionPenalty,
                        // extend an existing gap in B
                        gapB[i - 1][j] + gapExtensionPenalty,
                    )
                // same for gapB
                gapB[i][j] =
                    maxOf(
                        // open a new gap in B
                        score[i][j - 1] + gapOpenPenalty,
                        // extend an existing gap in A
                        gapA[i][j - 1] + gapExtensionPenalty,
                        // extend an existing gap in B
                        gapB[i][j - 1] + gapExtensionPenalty,
                    )

                val similarity = similarityM[i - 1][j - 1]
                val matchScore = score[i - 1][j - 1] + similarity // two matches in a row
                val gapAScore = gapA[i - 1][j - 1] + similarity // gap in A and then a match
                val gapBScore = gapB[i - 1][j - 1] + similarity // gap in B and then a match

                score[i][j] = maxOf(matchScore, gapAScore, gapBScore)
            }
        }
    }

    private fun traceback(): Array<AlignmentElement> {
        // the final score of the alignment
        val finalScore: Double = maxOf(score[m][n], gapA[m][n], gapB[m][n])

        // now, we need to trace back through the matrices to retrieve the optimal alignment
        // for that, we start at the end of the alignment, store the alignment elements and reverse
        // them afterward
        // to get the final alignment sequence

        val traceback: ArrayList<AlignmentElement> = ArrayList()

        var i: Int = m
        var j: Int = n

        // variable to store the last alignment "action"
        var origin =
            when (finalScore) {
                score[m][n] -> AlignmentElement.MATCH
                gapA[m][n] -> AlignmentElement.DELETION
                else -> AlignmentElement.INSERTION
            }

        while (i > 0 || j > 0) {
            // if there is a perfect match, add PERFECT instead of MATCH
            if (origin == AlignmentElement.MATCH && similarityM[i][j] == 1.0) {
                traceback.add(AlignmentElement.PERFECT)
            } else {
                traceback.add(origin)
            }

            when (origin) {
                AlignmentElement.MATCH -> {
                    val similarity = similarityM[i - 1][j - 1]

                    // determine, where the current score came from
                    origin =
                        when (score[i][j]) {
                            (score[i - 1][j - 1] + similarity) -> AlignmentElement.MATCH
                            (gapA[i - 1][j - 1] + similarity) -> AlignmentElement.DELETION
                            else -> AlignmentElement.INSERTION
                        }
                    i--
                    j--
                }
                AlignmentElement.DELETION -> {
                    // determine, where the current score came from
                    origin =
                        when (gapA[i][j]) {
                            (gapA[i - 1][j] + gapExtensionPenalty) -> AlignmentElement.DELETION
                            (gapB[i - 1][j] + gapExtensionPenalty) -> AlignmentElement.INSERTION
                            else -> AlignmentElement.MATCH
                        }

                    i--
                }
                AlignmentElement.INSERTION -> {
                    // determine, where the current score came from
                    origin =
                        when (gapB[i][j]) {
                            (gapA[i][j - 1] + gapExtensionPenalty) -> AlignmentElement.DELETION
                            (gapB[i][j - 1] + gapExtensionPenalty) -> AlignmentElement.INSERTION
                            else -> AlignmentElement.MATCH
                        }

                    j--
                }
                else -> throw Exception("Invalid alignment element")
            }
        }

        // reverse the alignment sequence as it is currently from back to front
        traceback.reverse()
        return traceback.toTypedArray()
    }
}
