import org.bytedeco.javacv.FFmpegFrameGrabber

/**
 * A globally accessible object that contains a list of all accepted codecs. This list can be
 * expanded to include more codecs.
 */
class AcceptedCodecs {
    companion object {
        val ACCEPTED_CODECS =
            setOf(
                "ffv1",
                "YUV",
                "flashsv",
                "gif",
                "png",
                "tiff",
                "ljpeg",
                "Uncompressed YUV 422 10-bit",
                "Uncompressed YUV 422 8-bit",
                "FFV1 YUV 422 8-bit",
            )

        public fun checkFile(path: String): Boolean {
            if (!path.endsWith(".mkv") && !path.endsWith(".mov")) {
                return false
            }
            val grabber = FFmpegFrameGrabber(path)
            grabber.start()
            val codecName = grabber.videoMetadata["encoder"] ?: grabber.videoCodecName
            for (codec in ACCEPTED_CODECS) {
                if (codecName.contains(codec, ignoreCase = true)) {
                    return true
                }
            }
            return false
        }
    }
}
