import algorithms.Gotoh

class DnaMetric : MetricInterface<Char> {
    override fun measureDistance(
        a: Char,
        b: Char,
    ): Double {
        return if (a != b) -1.0 else 1.0
    }
}

fun main(args: Array<String>) {
    println("Hello World!")

    println("Program arguments: ${args.joinToString()}")
    val metric: DnaMetric = DnaMetric()
    val algorithm: AlignmentAlgorithm<Char> = Gotoh(metric, -0.5, -0.5)

    val a: Array<Char> = arrayOf('A', 'A', 'G', 'G', 'T', 'A', 'G', 'C', 'A', 'C', 'G', 'T')
    val b: Array<Char> = arrayOf('A', 'A', 'A', 'A', 'G', 'G', 'T', 'A', 'C', 'G', 'T')

    val alignment: Array<AlignmentElement> = algorithm.run(a, b)

    var l1 = ArrayList<Char>()
    var l2 = ArrayList<Char>()
    var lDifference = ArrayList<Char>()

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
        }
    }
    println(l1.joinToString())
    println(lDifference.joinToString())
    println(l2.joinToString())
}
