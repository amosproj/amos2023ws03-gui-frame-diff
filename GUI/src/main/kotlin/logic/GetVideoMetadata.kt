package logic

import org.bytedeco.javacv.FFmpegFrameGrabber

fun getVideoMetadata(path: String): MutableMap<String, String> {
    val grabber = FFmpegFrameGrabber(path)
    grabber.start()
    val metadata = grabber.metadata
    grabber.stop()
    grabber.release()
    return metadata
}
