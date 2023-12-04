package algorithms

import MetricInterface
import kotlin.properties.Delegates

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
    private val gapOpenPenalty: Double,
    private val gapExtensionPenalty: Double,
) : AlignmentAlgorithm<T>(metric) {
    /**
     * The function to execute the algorithm on the given sequences of objects.
     *
     * @param a the first sequence of objects
     * @param b the second sequence of objects
     *
     * Returns an array of [AlignmentElement]s that represent the alignment between the two
     * sequences.
     */

    private var n by Delegates.notNull<Int>()
    private var m by Delegates.notNull<Int>()

    private lateinit var a: Array<T>
    private lateinit var b: Array<T>

    private lateinit var score: Array<DoubleArray>
    private lateinit var gapA: Array<DoubleArray>
    private lateinit var gapB: Array<DoubleArray>
    private lateinit var similarityM: Array<DoubleArray>

    override fun run(
        arr1: Array<T>,
        arr2: Array<T>,
    ): Array<AlignmentElement> {
        a = arr1
        b = arr2

        initializeMatrices()
        calculateBestRoute(a, b)
        val traceback: ArrayList<AlignmentElement> = tracebackBestRoute()
        return traceback.toTypedArray()
    }

    private fun initializeMatrices() {
        n = a.size
        m = b.size

        // three matrices of the size (n + 1) x (m + 1) each
        // score: if the last pair was a match
        // gapA: if the last pair was a gap in the first sequence
        // gapB: if the last pair was a gap in the second sequence
        // similarityM: the similarity matrix for the two sequences (used for caching)
        score = Array(n + 1) { DoubleArray(m + 1) { 0.0 } }
        gapA = Array(n + 1) { DoubleArray(m + 1) { 0.0 } }
        gapB = Array(n + 1) { DoubleArray(m + 1) { 0.0 } }
        similarityM = Array(n + 1) { DoubleArray(m + 1) { 0.0 } }

        // initialize the matrices
        score[0][0] = 0.0
        gapA[0][0] = 0.0
        gapB[0][0] = 0.0

        // initialize the first row and column, for gapA with an accumulated gap penalty
        // we use negative infinity to account for impossible alignments in the first row/column
        for (i in 1..n) {
            score[i][0] = Double.NEGATIVE_INFINITY
            gapA[i][0] = gapOpenPenalty + (i - 1) * gapExtensionPenalty
            gapB[i][0] = Double.NEGATIVE_INFINITY
        }

        // initialize the first row and column, for gapB with an accumulated gap penalty
        for (j in 1..m) {
            score[0][j] = Double.NEGATIVE_INFINITY
            gapA[0][j] = Double.NEGATIVE_INFINITY
            gapB[0][j] = gapOpenPenalty + (j - 1) * gapExtensionPenalty
        }
    }

    private fun calculateBestRoute(
        a: Array<T>,
        b: Array<T>,
    ) {
        for (i in 1..n) {
            for (j in 1..m) {
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

                // calculate the score for having choosing matching alignment instead of gaps
                similarityM[i - 1][j - 1] = 1 - metric.measureDistance(a[i - 1], b[j - 1])
                val similarity = similarityM[i - 1][j - 1]
                val matchScore =
                    score[i - 1][j - 1] + similarity // last pair was match and this one too
                val gapAScore = gapA[i - 1][j - 1] + similarity // gap in A and then a match
                val gapBScore = gapB[i - 1][j - 1] + similarity // gap in B and then a match

                score[i][j] = maxOf(matchScore, gapAScore, gapBScore)
            }
        }
    }

    private fun tracebackBestRoute(): ArrayList<AlignmentElement> {
        // the final score of the alignment
        val finalScore: Double = maxOf(score[n][m], gapA[n][m], gapB[n][m])

        // now, we need to trace back through the matrices to retrieve the optimal alignment
        // for that, we start at the end of the alignment, store the alignment elements and reverse
        // them afterward
        // to get the final alignment sequence

        val traceback: ArrayList<AlignmentElement> = ArrayList()

        var i: Int = n
        var j: Int = m

        // variable to store the last alignment "action"
        var origin = getLastAlignment(finalScore)

        while (i > 0 || j > 0) {
            traceback.add(origin)

            when (origin) {
                AlignmentElement.MATCH -> {
                    origin = handleAlignmentMatch(i, j)
                    i--
                    j--
                }

                AlignmentElement.DELETION -> {
                    // determine, where the current score came from
                    origin = handleAlignmentDeletion(i, j)
                    i--
                }

                AlignmentElement.INSERTION -> {
                    // determine, where the current score came from
                    origin = handleAlignmentInsertion(i, j)
                    j--
                }
            }
        }

        // reverse the alignment sequence as it is currently from back to front
        traceback.reverse()
        return traceback
    }

    private fun handleAlignmentInsertion(
        i: Int,
        j: Int,
    ): AlignmentElement {
        if (gapB[i][j] == gapA[i][j - 1] + gapExtensionPenalty) {
            return AlignmentElement.DELETION
        }

        if (gapB[i][j] == gapB[i][j - 1] + gapExtensionPenalty) {
            return AlignmentElement.INSERTION
        }

        return AlignmentElement.MATCH
    }

    private fun handleAlignmentDeletion(
        i: Int,
        j: Int,
    ): AlignmentElement {
        if (gapA[i][j] == gapA[i - 1][j] + gapExtensionPenalty) {
            return AlignmentElement.DELETION
        }

        if (gapA[i][j] == gapB[i - 1][j] + gapExtensionPenalty) {
            return AlignmentElement.INSERTION
        }

        return AlignmentElement.MATCH
    }

    private fun handleAlignmentMatch(
        i: Int,
        j: Int,
    ): AlignmentElement {
        val similarity = similarityM[i - 1][j - 1]

        if (score[i][j] == score[i - 1][j - 1] + similarity) {
            return AlignmentElement.MATCH
        }

        if (score[i][j] == gapA[i - 1][j - 1] + similarity) {
            return AlignmentElement.DELETION
        }

        return AlignmentElement.INSERTION
    }

    private fun getLastAlignment(finalScore: Double): AlignmentElement {
        when (finalScore) {
            score[n][m] -> {
                return AlignmentElement.MATCH
            }

            gapA[n][m] -> {
                return AlignmentElement.DELETION
            }

            else -> {
                return AlignmentElement.INSERTION
            }
        }
    }
}
