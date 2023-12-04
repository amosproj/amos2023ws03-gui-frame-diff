package wrappers

interface ResettableIterable<T> : Iterator<T>, Iterable<T> {
    fun reset()

    fun size(): Int

    override fun hasNext(): Boolean

    override fun next(): T

    override fun iterator(): Iterator<T>
}
