import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import org.junit.jupiter.api.Test
import java.awt.image.DataBufferByte
import java.awt.image.WritableRaster
import java.io.File
import kotlin.experimental.and
import kotlin.experimental.xor
import kotlin.system.measureTimeMillis

// maybe an interface for the methods?

internal class ReadRuntimeTests {
    private val resourcesPathPrefix = "src/test/resources/"
    private val video9Frames = resourcesPathPrefix + "9Screenshots.mov"
    private val modifiedVideo9Frames = resourcesPathPrefix + "9ScreenshotsModified.mov"
    private val video1File = File(video9Frames)
    private val video2File = File(modifiedVideo9Frames)
    private val runAmount = 50

    private val methodMap: Map<String, (Resettable2DFrameConverter, Frame, Frame, Int, Int) -> Int> =
        mapOf(
            "BufferedImage" to ::method1,
            "Raster" to ::method2,
            "ByteArray with 'xor'" to ::method3,
            "ByteArray with 'and'" to ::method4,
            "ByteArray with 'and' + extra var" to ::method5,
            "ByteArray with 'and' + less vars" to ::method6,
            "ByteArray with indexedMap" to ::method7,
        )

    private fun averageRunTime(
        runs: Int = runAmount,
        methodName: String,
    ) {
        var totalTime = 0L
        var difs = 0
        val method = methodMap[methodName] ?: throw Exception("Method not found")
        for (i in 0 until runs) {
            val (differences, time) = wrapper(video1File, video2File, method)
            totalTime += time
            difs += differences
        }
        println("Average for $methodName: ${totalTime / runs} ms, $difs differences")
    }

    @Test
    fun `test pixel comparison`() {
        for (method in methodMap.keys) {
            averageRunTime(methodName = method)
        }
    }

    private fun wrapper(
        vid1: File,
        vid2: File,
        method: (Resettable2DFrameConverter, Frame, Frame, Int, Int) -> Int,
    ): Pair<Int, Long> {
        val video1Grabber = FFmpegFrameGrabber(vid1)
        val video2Grabber = FFmpegFrameGrabber(vid2)

        video1Grabber.start()
        video2Grabber.start()

        val width = video1Grabber.imageWidth
        val height = video1Grabber.imageHeight

        val converter = Resettable2DFrameConverter()

        var frame1 = video1Grabber.grabImage()
        var frame2 = video2Grabber.grabImage()
        var counter = 0
        var time = 0L

        while (frame1 != null && frame2 != null) {
            time += measureTimeMillis { counter += method(converter, frame1, frame2, width, height) }

            frame1 = video1Grabber.grabImage()
            frame2 = video2Grabber.grabImage()
        }
        video1Grabber.stop()
        video2Grabber.stop()
        return Pair(counter, time)
    }

    private fun method1(
        converter: Resettable2DFrameConverter,
        frame1: Frame,
        frame2: Frame,
        width: Int,
        height: Int,
    ): Int {
        var counter = 0
        val frame1Data = converter.getImage(frame1)
        val frame2Data = converter.getImage(frame2)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel1 = frame1Data.getRGB(x, y)
                val pixel2 = frame2Data.getRGB(x, y)

                if (pixel1 - pixel2 != 0) {
                    counter += 1
                }
            }
        }
        return counter
    }

    private fun method2(
        converter: Resettable2DFrameConverter,
        frame1: Frame,
        frame2: Frame,
        width: Int,
        height: Int,
    ): Int {
        // Get Raster from BufferedImage
        var counter = 0
        val frame1Raster: WritableRaster = converter.getImage(frame1).raster
        val frame2Raster: WritableRaster = converter.getImage(frame2).raster

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel1 = frame1Raster.getPixel(x, y, IntArray(3))
                val pixel2 = frame2Raster.getPixel(x, y, IntArray(3))

                if (!pixel1.contentEquals(pixel2)) {
                    counter++
                }
            }
        }
        return counter
    }

    private fun method3(
        converter: Resettable2DFrameConverter,
        frame1: Frame,
        frame2: Frame,
        width: Int,
        height: Int,
    ): Int {
        var counter = 0
        val frame1Data = converter.getImage(frame1)
        val frame2Data = converter.getImage(frame2)

        val dataBuffer1 = frame1Data.raster.dataBuffer as DataBufferByte
        val dataBuffer2 = frame2Data.raster.dataBuffer as DataBufferByte

        val data1 = dataBuffer1.data
        val data2 = dataBuffer2.data

        for (x in 0 until width) {
            for (y in 0 until height) {
                val index = (y * width + x) * 3 // 3 bytes per pixel in TYPE_3BYTE_BGR

                if ((data1[index] xor data2[index]) != 0.toByte() ||
                    (data1[index + 1] xor data2[index + 1]) != 0.toByte() ||
                    (data1[index + 2] xor data2[index + 2]) != 0.toByte()
                ) {
                    counter++
                }
            }
        }
        return counter
    }

    private fun method4(
        converter: Resettable2DFrameConverter,
        frame1: Frame,
        frame2: Frame,
        width: Int,
        height: Int,
    ): Int {
        var counter = 0
        val frame1Data = converter.getImage(frame1)
        val frame2Data = converter.getImage(frame2)

        val dataBuffer1 = frame1Data.raster.dataBuffer as DataBufferByte
        val dataBuffer2 = frame2Data.raster.dataBuffer as DataBufferByte

        val data1 = dataBuffer1.data
        val data2 = dataBuffer2.data

        for (x in 0 until width) {
            for (y in 0 until height) {
                val index = (y * width + x) * 3
                val blue1 = data1[index] and 0xFF.toByte()
                val green1 = data1[index + 1] and 0xFF.toByte()
                val red1 = data1[index + 2] and 0xFF.toByte()

                val blue2 = data2[index] and 0xFF.toByte()
                val green2 = data2[index + 1] and 0xFF.toByte()
                val red2 = data2[index + 2] and 0xFF.toByte()

                if (blue1 != blue2 || green1 != green2 || red1 != red2) {
                    counter++
                }
            }
        }
        return counter
    }

    private fun method5(
        converter: Resettable2DFrameConverter,
        frame1: Frame,
        frame2: Frame,
        width: Int,
        height: Int,
    ): Int {
        var counter = 0
        val frame1Data = converter.getImage(frame1)
        val frame2Data = converter.getImage(frame2)

        val dataBuffer1 = frame1Data.raster.dataBuffer as DataBufferByte
        val dataBuffer2 = frame2Data.raster.dataBuffer as DataBufferByte

        val data1 = dataBuffer1.data
        val data2 = dataBuffer2.data

        for (x in 0 until width) {
            for (y in 0 until height) {
                val index = (y * width + x) * 3 // 3 bytes per pixel in TYPE_3BYTE_BGR
                val ff = 0xFF.toByte()
                val blue1 = data1[index] and ff
                val green1 = data1[index + 1] and ff
                val red1 = data1[index + 2] and ff

                val blue2 = data2[index] and ff
                val green2 = data2[index + 1] and ff
                val red2 = data2[index + 2] and ff

                if (blue1 != blue2 || green1 != green2 || red1 != red2) {
                    counter++
                }
            }
        }
        return counter
    }

    private fun method6(
        converter: Resettable2DFrameConverter,
        frame1: Frame,
        frame2: Frame,
        width: Int,
        height: Int,
    ): Int {
        var counter = 0
        val data1 = (converter.getImage(frame1).raster.dataBuffer as DataBufferByte).data
        val data2 = (converter.getImage(frame2).raster.dataBuffer as DataBufferByte).data
        for (x in 0 until width) {
            for (y in 0 until height) {
                val index = (y * width + x) * 3 // 3 bytes per pixel in TYPE_3BYTE_BGR
                val blue1 = data1[index] and 0xFF.toByte()
                val green1 = data1[index + 1] and 0xFF.toByte()
                val red1 = data1[index + 2] and 0xFF.toByte()

                val blue2 = data2[index] and 0xFF.toByte()
                val green2 = data2[index + 1] and 0xFF.toByte()
                val red2 = data2[index + 2] and 0xFF.toByte()

                if (blue1 != blue2 || green1 != green2 || red1 != red2) {
                    counter++
                }
            }
        }
        return counter
    }

    private fun method7(
        converter: Resettable2DFrameConverter,
        frame1: Frame,
        frame2: Frame,
        width: Int,
        height: Int,
    ): Int {
        var counter = 0
        val frame1Data = converter.getImage(frame1)
        val frame2Data = converter.getImage(frame2)

        val dataBuffer1 = frame1Data.raster.dataBuffer as DataBufferByte
        val dataBuffer2 = frame2Data.raster.dataBuffer as DataBufferByte

        val data1 = dataBuffer1.data
        val data2 = dataBuffer2.data

        counter =
            (
                data1.mapIndexed { index, byte ->
                    byte == data2[index]
                }.filter { !it }
            ).size
        return counter
    }
}