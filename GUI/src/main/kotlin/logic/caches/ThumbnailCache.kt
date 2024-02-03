package logic.caches

import androidx.compose.ui.graphics.ImageBitmap
import org.bytedeco.opencv.opencv_core.Mutex

/**
 * A class that caches thumbnails for the timeline.
 *
 * @param getImages A function that returns the thumbnails for a given diff index.
 */
class ThumbnailCache(val maxCacheSize: Int, val getImages: (Int) -> List<ImageBitmap>) {
    private val cache = mutableMapOf<Int, List<ImageBitmap>>()
    private val mutex = Mutex()

    // kind of a queue of diff indices ordered by most recently used
    private val recentlyUsed = mutableListOf<Int>()

    /**
     * Returns the thumbnails for a given diff index.
     *
     * If the thumbnails are not already cached, they are loaded via the `getImages` function and cached.
     *
     * @param index [Int] containing the index of the diff.
     * @return [List]<[ImageBitmap]> containing the thumbnails for the given diff index.
     */
    fun get(index: Int): List<ImageBitmap> {
        recentlyUsed.remove(index)
        println(index)

        if (!cache.containsKey(index)) {
            mutex.lock()
            cache[index] = getImages(index)
            mutex.unlock()
        }

        // if the queue is full, remove the least recently used item
        if (recentlyUsed.size >= maxCacheSize) {
            cache.remove(recentlyUsed.removeAt(0))
        }

        recentlyUsed.add(index)
        return cache[index]!!
    }
}
