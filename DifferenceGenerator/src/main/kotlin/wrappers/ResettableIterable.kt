package wrappers

/**
 * An interface for iterables that can be reset and have a size.
 */
interface ResettableIterable<T> : Iterator<T>, Iterable<T> {
    /**
     * Resets the iterable to its start.
     */
    fun reset()

    /**
     * Returns the size of the iterable.
     *
     * @return The size of the iterable.
     */
    fun size(): Int

    /**
     * Returns whether the iterable has a next element.
     *
     * @return Whether the iterable has a next element.
     */
    override fun hasNext(): Boolean

    /**
     * Returns the next element of the iterable.
     *
     * @return The next element of the iterable.
     */
    override fun next(): T

    /**
     * Returns an iterator for the iterable.
     *
     * @return An iterator for the iterable.
     */
    override fun iterator(): Iterator<T>
}
