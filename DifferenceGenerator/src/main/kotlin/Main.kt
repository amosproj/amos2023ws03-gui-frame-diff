import algorithms.AlignmentAlgorithm
import algorithms.AlignmentElement
import algorithms.Gotoh

class DnaMetric : MetricInterface<Char> {
    override fun measureDistance(
        a: Char,
        b: Char,
    ): Double {
        return if (a == b) 0.0 else 1.0
    }
}

fun main(args: Array<String>) {
    var gapOpen = -0.5
    var gapExtension = -0.0
    if (args.size == 2) {
        gapOpen = args[0].toDouble()
        gapExtension = args[1].toDouble()
    }
    println("GapOpenPenalty: ${gapOpen}, GapExtensionPenalty: ${gapExtension}")


    val metric = DnaMetric()
    val algorithm: AlignmentAlgorithm<Char> = Gotoh(metric, gapOpen, gapExtension)

    /*
    TODO:
    - generate random data with solution
    - run algorithm on data
    - compare results with solution
    - print correctness percentage in a new line
    - update readme
     */

}



