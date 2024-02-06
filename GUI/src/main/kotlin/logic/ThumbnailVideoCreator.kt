package logic

import org.bytedeco.javacv.FFmpegFrameRecorder
import wrappers.IterableFrameGrabber
import wrappers.Resettable2DFrameConverter
import java.awt.image.BufferedImage
import java.io.File

fun createThumbnailVideo(
    inputPath: String,
    downScaling: Float = 0.5f,
): String {
    assert(downScaling > 0.0f && downScaling <= 1.0f) { "Downscaling factor must be between 0 and 1" }
    val outputPath = kotlin.io.path.createTempFile(prefix = "gui_thumbnail_video", suffix = ".mkv").toString()

    val grabber = IterableFrameGrabber(File(inputPath))

    val inWidth = grabber.imageWidth
    val inHeight = grabber.imageHeight

    val outWidth = (inWidth * downScaling).toInt()
    val outHeight = (inHeight * downScaling).toInt()

    val recorder = FFmpegFrameRecorder(outputPath, outWidth, outHeight)

    val converter = Resettable2DFrameConverter()

    recorder.frameRate = grabber.frameRate
    recorder.videoCodec = grabber.videoCodec

    recorder.start()

    for (image in grabber) {
        // do something with the frame
        val scaledImage = BufferedImage(outWidth, outHeight, BufferedImage.TYPE_3BYTE_BGR)
        scaledImage.createGraphics().drawImage(image.getScaledInstance(outWidth, outHeight, 0), 0, 0, null)
        recorder.record(converter.convert(scaledImage))
    }

    recorder.close()
    grabber.close()

    return outputPath
}
