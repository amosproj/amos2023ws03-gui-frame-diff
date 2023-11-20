/**
 * Represents a metric interface that measures the distance between two objects of type T.
 *
 * @param T the type of objects to measure the distance between.
 */
interface MetricInterface<T> {
    /**
     * Measures the distance between two objects of type T.
     *
     * @param frame1 the first object.
     * @param frame2 the second object.
     * @return the distance between the two objects.
     */
    fun measureDistance(
        frame1: T,
        frame2: T,
    ): Double
}
