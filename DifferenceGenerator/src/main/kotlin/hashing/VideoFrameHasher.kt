package hashing

import Resettable2DFrameConverter
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.security.MessageDigest

class VideoFrameHasher : ObjectHasher<BufferedImage> {
    private val converter = Resettable2DFrameConverter()

    override fun hash(obj: BufferedImage): ByteArray {
        val image = (obj.raster.dataBuffer as DataBufferByte).data
        val md5 = MessageDigest.getInstance("MD5")
        md5.update(image)
        return md5.digest()
    }

    override fun getHashes(objs: Iterable<BufferedImage>): Array<ByteArray> {
        val hashArray = ArrayList<ByteArray>()
        for (frame in objs)
            hashArray.add(hash(frame))

        return hashArray.toTypedArray()
    }
}
