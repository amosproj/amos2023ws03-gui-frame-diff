package logic.caches

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.yield
import kotlin.coroutines.cancellation.CancellationException

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
     * The image retrieval is locked with a mutex to prevent race conditions. If the executing job is cancelled, while
     * waiting for the lock, the function returns immediately.
     *
     * @param index [Int] containing the index of the diff.
     * @return [List]<[ImageBitmap]> containing the thumbnails for the given diff index, optional.
     */
    suspend fun get(index: Int): List<ImageBitmap>? {
        recentlyUsed.remove(index)

        if (!cache.containsKey(index)) {
            // ask to enter the critical section, wait if it's already locked
            mutex.lock()

            try {
                // check if the images are still wanted
                yield()
            } catch (e: CancellationException) {
                // ...if not, release the lock and return immediately
                mutex.unlock()
                return null
            }

            // actually grab the images
            cache[index] = getImages(index)

            // release the lock as the critical section is over
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
