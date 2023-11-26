
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.junit.jupiter.api.Test

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBuffer
import java.awt.image.Raster
import java.awt.image.WritableRaster
import java.io.File
import java.io.Writer
import kotlin.system.measureTimeMillis

internal class RuntimeTests {
    private val resourcesPathPrefix = "src/test/resources/"

    // Video with 10 frames, the second frame is added compared to the vide9Frames
    // Video with 9 frames
    private val video9Frames = resourcesPathPrefix + "9Screenshots.mov"

    // Modified video with 9 frames
    private val modifiedVideo9Frames = resourcesPathPrefix + "9ScreenshotsModified.mov"


    val video1File = File(video9Frames)
    val video2File = File(modifiedVideo9Frames)
    private val runAmount = 10

    fun averageRunTime(runs: Int = runAmount,  method: (File, File) -> Int): Pair<Long, Int> {
        var totalTime = 0L
        var difs = 0
        for (i in 0 until runs) {
            totalTime += measureTimeMillis {
                difs = method(video1File, video2File)
            }
        }
        return Pair(totalTime / runs, difs)
    }


    @Test
    fun `test pixel comparison`() {
        println("Average time for bufferedImage: ${averageRunTime { vid1, vid2 -> method1(vid1, vid2) }}")
        println("Average time for raster: ${averageRunTime { vid1, vid2 -> method2(vid1, vid2) }}")
        println("Average time for dataBuffer: ${averageRunTime { vid1, vid2 -> method3(vid1, vid2) }}")

    }

    private fun method1(vid1: File, vid2: File): Int {
        val video1Grabber = FFmpegFrameGrabber(vid1)
        val video2Grabber = FFmpegFrameGrabber(vid2)

        video1Grabber.start()
        video2Grabber.start()

        val width = video1Grabber.imageWidth
        val height = video1Grabber.imageHeight

        val converter = Resettable2DFrameConverter()

        var frame1 = video1Grabber.grabImage()
        var frame2 = video2Grabber.grabImage()
        var counter = 0;

        while (frame1 != null && frame2 != null) {
            val frame1Data = converter.convert(frame1)
            val frame2Data = converter.convert(frame2)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel1 = frame1Data.getRGB(x, y)
                    val pixel2 = frame2Data.getRGB(x, y)

                    if (pixel1 - pixel2 != 0) {
                        counter= counter+1
                    }
                }
            }
            frame1 = video1Grabber.grabImage()
            frame2 = video2Grabber.grabImage()
        }
        video1Grabber.stop()
        video2Grabber.stop()
        return counter
    }

    fun method2(vid1: File, vid2: File): Int {
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

        while (frame1 != null && frame2 != null) {
            // Get Raster from BufferedImage
            val frame1Raster: WritableRaster = converter.getImage(frame1).raster
            val frame2Raster: WritableRaster = converter.getImage(frame2).raster

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel1 = frame1Raster.getPixel(x, y, IntArray(3))
                    val pixel2 = frame2Raster.getPixel(x, y, IntArray(3))

                    if (pixel1.contentEquals(pixel2)) {
                        counter++
                    }

                }
            }

            frame1 = video1Grabber.grabImage()
            frame2 = video2Grabber.grabImage()
        }
        video1Grabber.stop()
        video2Grabber.stop()
        return counter
    }


    fun method3(vid1: File, vid2: File): Int {
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

        while (frame1 != null && frame2 != null) {
            // Get Raster from BufferedImage
            val frame1Raster: DataBuffer = converter.getImage(frame1).raster.dataBuffer
            val frame2Raster: DataBuffer = converter.getImage(frame2).raster.dataBuffer

            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel1 = frame1Raster.getElem(x)
                    val pixel2 = frame2Raster.getElem(x)

                    if (pixel1 == pixel2) {
                        counter++
                    }

                }
            }

            frame1 = video1Grabber.grabImage()
            frame2 = video2Grabber.grabImage()
        }
        video1Grabber.stop()
        video2Grabber.stop()
        return counter
    }



}
