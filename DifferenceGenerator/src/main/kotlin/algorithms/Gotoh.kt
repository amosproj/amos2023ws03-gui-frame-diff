package algorithms

import MetricInterface

class Gotoh<T>(
    private val metric: MetricInterface<T>,
    private val gapOpenPenalty: Double,
    private val gapExtensionPenalty: Double,
) : AlignmentAlgorithm<T>(metric) {
    override fun run(
        a: Array<T>,
        b: Array<T>,
    ): Array<AlignmentElement> {
        val n: Int = a.size
        val m: Int = b.size

        val score = Array(n + 1) { DoubleArray(m + 1) }
        val gapA = Array(n + 1) { DoubleArray(m + 1) }
        val gapB = Array(n + 1) { DoubleArray(m + 1) }

        score[0][0] = 0.0
        gapA[0][0] = 0.0
        gapB[0][0] = 0.0

        for (i in 1..n) {
            score[i][0] = Double.NEGATIVE_INFINITY
            gapA[i][0] = gapOpenPenalty + (i - 1) * gapExtensionPenalty
            gapB[i][0] = Double.NEGATIVE_INFINITY
        }

        for (j in 1..m) {
            score[0][j] = Double.NEGATIVE_INFINITY
            gapA[0][j] = Double.NEGATIVE_INFINITY
            gapB[0][j] = gapOpenPenalty + (j - 1) * gapExtensionPenalty
        }

        for (i in 1..n) {
            for (j in 1..m) {
                gapA[i][j] =
                    maxOf(
                        score[i - 1][j] + gapOpenPenalty,
                        gapA[i - 1][j] + gapExtensionPenalty,
                        gapB[i - 1][j] + gapExtensionPenalty,
                    )
                gapB[i][j] =
                    maxOf(
                        score[i][j - 1] + gapOpenPenalty,
                        gapA[i][j - 1] + gapExtensionPenalty,
                        gapB[i][j - 1] + gapExtensionPenalty,
                    )

                val similarity = metric.measureDistance(a[i - 1], b[j - 1])
                val matchScore = score[i - 1][j - 1] + similarity // last pair was match and this one too
                val gapAScore = gapA[i - 1][j - 1] + similarity // gap in A and then a match
                val gapBScore = gapB[i - 1][j - 1] + similarity // gap in B and then a match

                score[i][j] = maxOf(matchScore, gapAScore, gapBScore)
            }
        }

        val finalScore: Double = maxOf(score[n][m], gapA[n][m], gapB[n][m])
        val traceback: ArrayList<AlignmentElement> = ArrayList<AlignmentElement>()

        var i: Int = n
        var j: Int = m

        var origin = (
            if (finalScore == score[n][m]) {
                AlignmentElement.MATCH
            } else if (finalScore == gapA[n][m]) {
                AlignmentElement.DELETION
            } else {
                AlignmentElement.INSERTION
            }
        )

        while (i > 0 || j > 0) {
            traceback.add(origin)

            when (origin) {
                AlignmentElement.MATCH -> {
                    val similarity = metric.measureDistance(a[i - 1], b[j - 1])
                    origin =
                        if (score[i][j] == score[i - 1][j - 1] + similarity) {
                            AlignmentElement.MATCH
                        } else if (score[i][j] == gapA[i - 1][j - 1] + similarity) {
                            AlignmentElement.DELETION
                        } else {
                            AlignmentElement.INSERTION
                        }
                    i--
                    j--
                }
                AlignmentElement.DELETION -> {
                    origin =
                        if (gapA[i][j] == gapA[i - 1][j] + gapExtensionPenalty) {
                            AlignmentElement.DELETION
                        } else if (gapA[i][j] == gapB[i - 1][j] + gapExtensionPenalty) {
                            AlignmentElement.INSERTION
                        } else {
                            AlignmentElement.MATCH
                        }
                    i--
                }
                AlignmentElement.INSERTION -> {
                    origin =
                        if (gapB[i][j] == gapA[i][j - 1] + gapExtensionPenalty) {
                            AlignmentElement.DELETION
                        } else if (gapB[i][j] == gapB[i][j - 1] + gapExtensionPenalty) {
                            AlignmentElement.INSERTION
                        } else {
                            AlignmentElement.MATCH
                        }
                    j--
                }
            }
        }

        traceback.reverse()
        return traceback.toTypedArray()
    }
}
