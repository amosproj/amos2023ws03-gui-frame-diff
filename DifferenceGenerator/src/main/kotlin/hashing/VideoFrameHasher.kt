package hashing

import wrappers.Resettable2DFrameConverter
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.security.MessageDigest

/**
 * A hasher for BufferedImages.
 */
class VideoFrameHasher : ObjectHasher<BufferedImage> {
    private val converter = Resettable2DFrameConverter()

    /**
     * Returns the hash of the given image.
     *
     * Here we are using the MD5 algorithm to hash the image.
     *
     * @param obj The image.
     * @return The hash of the given image as [ByteArray].
     */
    override fun hash(obj: BufferedImage): ByteArray {
        val image = (obj.raster.dataBuffer as DataBufferByte).data
        val md5 = MessageDigest.getInstance("MD5")
        md5.update(image)
        return md5.digest()
    }

    /**
     * Returns the hashes of the given iterable of images.
     *
     * @param objs The images to hash.
     * @return The hashes of the given images as [Array] of [ByteArray]s.
     */
    override fun getHashes(objs: Iterable<BufferedImage>): Array<ByteArray> {
        val hashArray = ArrayList<ByteArray>()
        for (frame in objs)
            hashArray.add(hash(frame))

        return hashArray.toTypedArray()
    }
}
