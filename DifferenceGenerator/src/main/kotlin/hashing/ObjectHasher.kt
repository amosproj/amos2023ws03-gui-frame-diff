package hashing

interface ObjectHasher<T> {
    fun hash(obj: T): ByteArray

    fun getHashes(objs: Iterable<T>): Array<ByteArray>
}
