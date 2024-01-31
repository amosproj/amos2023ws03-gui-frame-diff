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

        val ACTIVE_CODECS =
            setOf(
                "ffv1",
                "FFV1 YUV 422 8-bit",
            )

    }
}
