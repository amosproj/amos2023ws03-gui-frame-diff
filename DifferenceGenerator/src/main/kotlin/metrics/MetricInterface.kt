/**
 * Represents a metric interface that measures the distance between two objects of type T.
 *
 * @param T the type of objects to measure the distance between.
 */
interface MetricInterface<T> {
    /**
     * Measures the distance between two objects of type T.
     *
     * @param a the first object.
     * @param b the second object.
     * @return the distance between the two objects.
     */
    fun measureDistance(
        a: T,
        b: T,
    ): Double
}
