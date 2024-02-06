// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
import org.bytedeco.javacv.Frame
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import wrappers.Resettable2DFrameConverter
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.system.measureTimeMillis

// maybe an interface for the methods?

@Tag("benchmark")
internal class WriteRuntimeTests {
    private val runAmount = 50
    private val size = 6000

    private val methodMap: Map<String, (Resettable2DFrameConverter) -> Frame> =
        mapOf(
            "BufferedImage" to ::method1,
            "Raster" to ::method2,
            "DataBuffer" to ::method3,
            "DataBuffer single loop" to ::method4,
            "DataBuffer while loop" to ::method5,
        )

    private fun averageRunTime(
        runs: Int = runAmount,
        methodName: String,
    ) {
        var totalTime = 0L
        val method = methodMap[methodName] ?: throw Exception("Method not found")
        for (i in 0 until runs) {
            totalTime += wrapper(method)
        }
        println("Average for $methodName: ${totalTime / runs} ms")
    }

    @Test
    fun `test pixel comparison`() {
        for (method in methodMap.keys) {
            averageRunTime(methodName = method)
        }
    }

    private fun wrapper(method: (Resettable2DFrameConverter) -> Frame): Long {
        val converter = Resettable2DFrameConverter()
        var f: Frame
        val time =
            measureTimeMillis {
                f = method(converter)
            }
        if (f.imageWidth != size || f.imageHeight != size) {
            throw Exception("Wrong size")
        }

        if (converter.convert(f).getRGB(0, 0) != Color.WHITE.rgb) {
            throw Exception("Wrong color")
        }
        return time
    }

    private fun method1(converter: Resettable2DFrameConverter): Frame {
        val bufferedImage = BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bufferedImage.setRGB(x, y, 0xFFFFFF)
            }
        }
        return converter.getFrame(bufferedImage)
    }

    private fun method2(converter: Resettable2DFrameConverter): Frame {
        val bufferedImage = BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR)
        val raster = bufferedImage.raster
        for (x in 0 until size) {
            for (y in 0 until size) {
                raster.setPixel(x, y, intArrayOf(0xFF, 0xFF, 0xFF))
            }
        }
        return converter.getFrame(bufferedImage)
    }

    private fun method3(converter: Resettable2DFrameConverter): Frame {
        val bufferedImage = BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR)
        val dataBuffer = bufferedImage.raster.dataBuffer
        for (x in 0 until size) {
            for (y in 0 until size) {
                dataBuffer.setElem(x * size + y, 0xFFFFFF)
            }
        }
        return converter.getFrame(bufferedImage)
    }

    private fun method4(converter: Resettable2DFrameConverter): Frame {
        val bufferedImage = BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR)
        val dataBuffer = bufferedImage.raster.dataBuffer
        for (index in 0 until size * size) {
            dataBuffer.setElem(index, 0xFFFFFF)
        }
        return converter.getFrame(bufferedImage)
    }

    private fun method5(converter: Resettable2DFrameConverter): Frame {
        val bufferedImage = BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR)
        val dataBuffer = bufferedImage.raster.dataBuffer
        var index = 0
        while (index < size * size) {
            dataBuffer.setElem(index, 0xFFFFFF)
            index++
        }
        return converter.getFrame(bufferedImage)
    }
}
