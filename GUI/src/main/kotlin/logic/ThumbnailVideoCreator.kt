package logic

import androidx.compose.runtime.MutableState
import models.AppState
import org.bytedeco.javacv.FFmpegFrameRecorder
import wrappers.IterableFrameGrabber
import wrappers.Resettable2DFrameConverter
import java.awt.image.BufferedImage
import java.io.File

/**
 * Creates a scaled video from the input video as a temp file.
 *
 * @param inputPath [String] containing the path to the input video.
 * @param scale [Float] containing the scaling factor, must be greater than 0f.
 * @return [String] containing the path to the scaled video.
 */
fun createScaledVideo(
    inputPath: String,
    scale: Float = 0.5f,
): String {
    assert(scale > 0.0f) { "Scaling factor must be positive!" }
    val outputPath = kotlin.io.path.createTempFile(prefix = "gui_thumbnail_video", suffix = ".mkv").toString()

    val grabber = IterableFrameGrabber(File(inputPath))

    val inWidth = grabber.imageWidth
    val inHeight = grabber.imageHeight

    val outWidth = (inWidth * scale).toInt()
    val outHeight = (inHeight * scale).toInt()

    val recorder = FFmpegFrameRecorder(outputPath, outWidth, outHeight)
    val converter = Resettable2DFrameConverter()

    // copy some metadata over
    recorder.frameRate = grabber.frameRate
    recorder.videoCodec = grabber.videoCodec

    recorder.start()

    for (image in grabber) {
        // scale down the image and record it
        val scaledImage = BufferedImage(outWidth, outHeight, BufferedImage.TYPE_3BYTE_BGR)
        scaledImage.createGraphics().drawImage(image.getScaledInstance(outWidth, outHeight, 0), 0, 0, null)
        recorder.record(converter.convert(scaledImage))
    }

    recorder.close()
    grabber.close()

    return outputPath
}

/**
 * Creates the thumbnail videos for the reference and current videos and updates their paths in the global state.
 *
 * @param state [MutableState]<[AppState]> containing the global state.
 */
fun createThumbnailVideos(state: MutableState<AppState>) {
    // create the thumbnail videos
    val tempReference = createScaledVideo(state.value.videoReferencePath!!, 0.25f)
    val tempCurrent = createScaledVideo(state.value.videoCurrentPath!!, 0.25f)

    state.value = state.value.copy(thumbnailVideoPathReference = tempReference, thumbnailVideoPathCurrent = tempCurrent)
}
