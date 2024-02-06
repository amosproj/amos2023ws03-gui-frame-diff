// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
package wrappers

/**
 * An iterator for ArrayLists that can be reset and have a size.
 *
 * This implementation can be used to plug [ArrayList]s into functions or classes that expect
 * [ResettableIterable]s. E.g. the AlignmentAlgorithm class.
 */
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
