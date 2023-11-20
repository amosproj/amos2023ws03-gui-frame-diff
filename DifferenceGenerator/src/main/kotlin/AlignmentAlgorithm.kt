class DnaMetric : MetricInterface<Char> {
    override fun measureDistance(
        a: Char,
        b: Char,
    ): Double {
        return if (a == b) 0.0 else 1.0
    }
}

enum class AlignmentElement {
    MATCH,
    INSERTION,
    DELETION,
}

abstract class AlignmentAlgorithm<T>(metric: MetricInterface<T>) {
    /**
     * The function to execute the algorithm on the given sequences of objects.
     */
    abstract fun run(
        a: Array<T>,
        b: Array<T>,
    ): Array<AlignmentElement>
}
