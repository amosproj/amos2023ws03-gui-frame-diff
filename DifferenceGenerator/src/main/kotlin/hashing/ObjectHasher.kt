// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
package hashing

/**
 * A hasher for objects of type T.
 *
 * @param T The type of the objects to hash.
 */
interface ObjectHasher<T> {
    /**
     * Returns the hash of the given object.
     *
     * @param obj The object to hash.
     * @return The hash of the given object as [ByteArray].
     */
    fun hash(obj: T): ByteArray

    /**
     * Returns the hashes of the given iterable of objects.
     *
     * @param objs The objects to hash.
     * @return The hashes of the given objects as [Array] of [ByteArray]s.
     */
    fun getHashes(objs: Iterable<T>): Array<ByteArray>
}
