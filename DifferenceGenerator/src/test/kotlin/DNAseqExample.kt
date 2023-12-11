import algorithms.AlignmentAlgorithm
import algorithms.AlignmentElement
import algorithms.Gotoh
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals

class DnaMetric : MetricInterface<Char> {
    override fun measureDistance(
        a: Char,
        b: Char,
    ): Double {
        return if (a == b) 0.0 else 1.0
    }
}

class DNAseqExample {
    @Test
    fun `DNAseq example`() {
        val metric = DnaMetric()
        val algorithm: AlignmentAlgorithm<Char> = Gotoh(metric, -0.5, -0.0)

        val a: Array<Char> = arrayOf('A', 'A', 'G', 'G', 'T', 'A', 'G', 'C', 'A', 'C', 'G', 'T')
        val b: Array<Char> = arrayOf('A', 'A', 'A', 'A', 'G', 'G', 'T', 'A', 'C', 'G', 'T')

        val alignment: Array<AlignmentElement> = algorithm.run(a, b)
        assertContentEquals(
            alignment,
            arrayOf(
                AlignmentElement.INSERTION, AlignmentElement.INSERTION,
                AlignmentElement.PERFECT, AlignmentElement.PERFECT, AlignmentElement.PERFECT,
                AlignmentElement.PERFECT, AlignmentElement.PERFECT,
                AlignmentElement.DELETION, AlignmentElement.DELETION, AlignmentElement.DELETION,
                AlignmentElement.PERFECT, AlignmentElement.PERFECT, AlignmentElement.PERFECT, AlignmentElement.MATCH,
            ),
        )

        val l1 = ArrayList<Char>()
        val l2 = ArrayList<Char>()
        val lDifference = ArrayList<Char>()

        var i = 0
        var j = 0

        for (al in alignment) {
            when (al) {
                AlignmentElement.MATCH -> {
                    l1.add(a[i])
                    l2.add(b[j])
                    lDifference.add('|')
                    i++
                    j++
                }
                AlignmentElement.INSERTION -> {
                    l1.add('-')
                    l2.add(b[j])
                    lDifference.add(' ')
                    j++
                }
                AlignmentElement.DELETION -> {
                    l1.add(a[i])
                    l2.add('-')
                    lDifference.add(' ')
                    i++
                }
                AlignmentElement.PERFECT -> {
                    l1.add(a[i])
                    l2.add(b[j])
                    lDifference.add('|')
                    i++
                    j++
                }
            }
        }
        println(l1.joinToString())
        println(lDifference.joinToString())
        println(l2.joinToString())
    }
}
