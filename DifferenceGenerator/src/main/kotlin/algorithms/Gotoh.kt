package algorithms

import AlignmentAlgorithm
import AlignmentElement
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
                    )
                gapB[i][j] =
                    maxOf(
                        score[i][j - 1] + gapOpenPenalty,
                        gapB[i][j - 1] + gapExtensionPenalty,
                    )

                val similarity = -metric.measureDistance(a[i - 1], b[j - 1])
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

        var c = 0
        println(score[n][m])
        println(gapA[n][m])
        println(gapB[n][m])
        println(finalScore)
        var origin = (
            if (finalScore == score[n][m]) {
                AlignmentElement.MATCH
            } else if (finalScore == gapA[n][m]) {
                AlignmentElement.DELETION
            } else {
                AlignmentElement.INSERTION
            }
        )

        while ((i > 0 || j > 0) && c < 30) {
            c++
            println("i: $i, j: $j")

            val current = (
                if (origin == AlignmentElement.MATCH) {
                    score[i][j]
                } else if (origin == AlignmentElement.DELETION) {
                    gapA[i][j]
                } else {
                    gapB[i][j]
                }
            )
            println("origin: $origin")
            println("current: $current")
            println("score[i-1][j-1]: ${score[i - 1][j - 1]}")
            println("gapA[i-1][j]: ${gapA[i - 1][j]}")
            println("gapB[i][j-1]: ${gapB[i][j - 1]}")
            println("a[i-1]: ${a[i - 1]}")
            println("b[j-1]: ${b[j - 1]}\n")
            if (i > 0 && j > 0 && current == score[i - 1][j - 1] + metric.measureDistance(a[i - 1], b[j - 1])) {
                traceback.add(AlignmentElement.MATCH)
                origin = AlignmentElement.MATCH
                i--
                j--
            } else if (j > 0 && (current == gapB[i][j - 1] + gapExtensionPenalty || current == gapB[i][j - 1] + gapOpenPenalty)) {
                traceback.add(AlignmentElement.INSERTION)
                origin = AlignmentElement.INSERTION
                j--
            } else if (i > 0 && (current == gapA[i - 1][j] + gapExtensionPenalty || current == gapA[i - 1][j] + gapOpenPenalty)) {
                traceback.add(AlignmentElement.DELETION)
                origin = AlignmentElement.DELETION
                i--
            }
        }

        traceback.reverse()
        return traceback.toTypedArray()
    }
}
