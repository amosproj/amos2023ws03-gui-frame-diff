import org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_FFV1

import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat
import org.bytedeco.opencv.global.opencv_core.CV_8UC3
import org.bytedeco.opencv.global.opencv_core.absdiff

import org.bytedeco.opencv.opencv_core.*
import org.bytedeco.opencv.opencv_imgproc.*
import org.bytedeco.opencv.opencv_core.Mat


import java.io.File

class DifferenceGenerator(video1Path: String, video2Path: String, outputPath: String) :
    AbstractDifferenceGenerator(video1Path, video2Path, outputPath) {
    val outputFile = File(outputPath)
    private val video1File = File(video1Path)
    private val video2File = File(video2Path)

    private val video1Grabber = FFmpegFrameGrabber(video1File)
    private val video2Grabber = FFmpegFrameGrabber(video2File)

    /**
     * Initializes a new instance of the [DifferenceGenerator] class.
     *
     * @throws Exception if the videos are not in an [AcceptedCodecs.ACCEPTED_CODECS].
     */
    init {
        if (!isLosslessCodec(video1Grabber) || !isLosslessCodec(video2Grabber)) {
            throw Exception("Videos must be in a lossless codec")
        }
        generateDifference()
    }

    /**
     * Determines whether the given video file is encoded using one of the
     * [AcceptedCodecs.ACCEPTED_CODECS].
     *
     * @param [FFmpegFrameGrabber] of the video to check
     * @return true if the video file is encoded using one of the [AcceptedCodecs.ACCEPTED_CODECS],
     * false otherwise
     */
    private fun isLosslessCodec(grabber: FFmpegFrameGrabber): Boolean {
        grabber.start()
        val codecName = grabber.videoMetadata["encoder"]
        if (codecName == null) {
            grabber.stop()
            throw Exception("Video must have a codec")
        }

        return codecName in AcceptedCodecs.ACCEPTED_CODECS
    }

    override fun generateDifference() {
        val encoder = FFmpegFrameRecorder(this.outputFile, video1Grabber.imageWidth, video1Grabber.imageHeight)
        encoder.videoCodec = AV_CODEC_ID_FFV1
        encoder.start()

        var frame1 = this.video1Grabber.grabImage()
        var frame2 = this.video2Grabber.grabImage()

        while (frame1 != null && frame2 != null) {
            val differences = getDifferences(frame1, frame2)
            encoder.record(differences)
            frame1 = this.video1Grabber.grabImage()
            frame2 = this.video2Grabber.grabImage()
        }

        encoder.stop()
        encoder.release()
        this.video1Grabber.stop()
        this.video2Grabber.stop()
    }


    private fun getDifferences(frame1: Frame, frame2: Frame): Frame {
        val width = frame1.imageWidth
        val height = frame1.imageHeight
        val differences = Frame(width, height, frame1.imageDepth, frame1.imageChannels)


        val frame1Pixels = frame1.image[0]
        val frame2Pixels = frame2.image[0]


        val capacity = frame1Pixels.capacity()



        return differences

    }



    override fun saveDifferences() {}
}
