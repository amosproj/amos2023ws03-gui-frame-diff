package wrappers

class ResettableArrayListIterator<T>(private val arr: ArrayList<T>) : ResettableIterable<T> {
    private var index = 0

    override fun reset() {
        index = 0
    }

    override fun size(): Int {
        return arr.size
    }

    override fun hasNext(): Boolean {
        return index < arr.size - 1
    }

    override fun next(): T {
        return arr[index++]
    }

    override fun iterator(): Iterator<T> {
        return this
    }
}
