import java.io.File
import java.io.IOException
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.stream.FileImageOutputStream

class GifCreator {
    private lateinit var gifWriter: ImageWriter
    private lateinit var gifOutput: FileImageOutputStream

    fun init(outputGifPath: String) {
        gifWriter = ImageIO.getImageWritersByFormatName("gif").next()
        gifOutput = FileImageOutputStream(File(outputGifPath))
        gifWriter.output = gifOutput

        val imageWriteParam = gifWriter.defaultWriteParam
        imageWriteParam.compressionMode = ImageWriteParam.MODE_DEFAULT

        try {
            gifWriter.prepareWriteSequence(null)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun write(inputPath: String) {
        try {
            val image = ImageIO.read(File(inputPath))
            val iioImage = IIOImage(image, null, null)
            gifWriter.writeToSequence(iioImage, null)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun close() {
        try {
            gifWriter.endWriteSequence()
            gifOutput.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            gifWriter.dispose()
        }
    }
}
