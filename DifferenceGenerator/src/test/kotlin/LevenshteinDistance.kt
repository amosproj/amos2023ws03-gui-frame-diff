// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: simonsasse <simonsasse97@gmail.com>
import algorithms.AlignmentElement

/**
 * Calculates the Levenshtein distance between two arrays of AlignmentElement objects.
 *
 * @param a The first array of AlignmentElement objects.
 * @param b The second array of AlignmentElement objects.
 */
class LevenshteinDistance(
    a: Array<AlignmentElement>,
    b: Array<AlignmentElement>,
) {
    private val m = a.size
    private val n = b.size
    private val d = Array(m + 1) { IntArray(n + 1) }

    init {
        for (i in 0..m) {
            d[i][0] = i
        }
        for (j in 0..n) {
            d[0][j] = j
        }
        for (j in 1..n) {
            for (i in 1..m) {
                if (a[i - 1] == b[j - 1]) {
                    d[i][j] = d[i - 1][j - 1]
                } else {
                    d[i][j] =
                        minOf(
                            d[i - 1][j] + 1,
                            d[i][j - 1] + 1,
                            d[i - 1][j - 1] + 1,
                        )
                }
            }
        }
    }

    val distance = d[m][n]
}
