package frameNavigation

import FrameNavigationInterface
import algorithms.AlignmentElement
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import models.AppState
import org.bytedeco.javacv.FFmpegFrameGrabber
import wrappers.Resettable2DFrameConverter
import java.awt.image.BufferedImage
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * A class that implements the [FrameNavigationInterface] interface.
 * @param state [MutableState]<[AppState]> containing the global state.
 */
class FrameNavigation(state: MutableState<AppState>) : FrameNavigationInterface {
    // create the grabbers
    private val video1Grabber: FFmpegFrameGrabber = FFmpegFrameGrabber(state.value.video1Path)
    private val video2Grabber: FFmpegFrameGrabber = FFmpegFrameGrabber(state.value.video2Path)
    private val grabberDiff: FFmpegFrameGrabber = FFmpegFrameGrabber(state.value.outputPath)

    // create the sequences
    private var diffSequence: Array<AlignmentElement> = state.value.sequenceObj
    private var video1Frames: MutableList<Int> = mutableListOf()
    private var video2Frames: MutableList<Int> = mutableListOf()

    // create the converter
    private val converter = Resettable2DFrameConverter()

    // create the bitmaps
    var video1Bitmap: MutableState<ImageBitmap> = mutableStateOf(BufferedImage(1, 1, 1).toComposeImageBitmap())
    var video2Bitmap: MutableState<ImageBitmap> = mutableStateOf(BufferedImage(1, 1, 1).toComposeImageBitmap())
    var diffBitmap: MutableState<ImageBitmap> = mutableStateOf(BufferedImage(1, 1, 1).toComposeImageBitmap())

    init {
        // start the grabbers
        video1Grabber.start()
        video2Grabber.start()
        grabberDiff.start()
        // generate the sequences for video 1 and video 2
        // diffSequence is already generated
        generateSequences()
        // jump to the first frame
        jumpToFrame(0)
    }

    /**
     * Close the grabbers.
     */
    fun close() {
        video1Grabber.stop()
        video2Grabber.stop()
        grabberDiff.stop()
        video1Grabber.close()
        video2Grabber.close()
        grabberDiff.close()
    }

    /**
     * Generate the sequences for video 1 and video 2.
     */
    private fun generateSequences() {
        // init with -1 to account for deletions/insertions on the first frame
        video1Frames.add(-1)
        video2Frames.add(-1)
        for (i in diffSequence) {
            when (i) {
                AlignmentElement.MATCH -> {
                    video1Frames.add(video1Frames.last() + 1)
                    video2Frames.add(video2Frames.last() + 1)
                }
                AlignmentElement.INSERTION -> {
                    video1Frames.add(video1Frames.last())
                    video2Frames.add(video2Frames.last() + 1)
                }
                AlignmentElement.DELETION -> {
                    video1Frames.add(video1Frames.last() + 1)
                    video2Frames.add(video2Frames.last())
                }
                AlignmentElement.PERFECT -> {
                    video1Frames.add(video1Frames.last() + 1)
                    video2Frames.add(video2Frames.last() + 1)
                }
            }
        }
        // remove the initial -1
        video1Frames.removeAt(0)
        video2Frames.removeAt(0)
    }

    /**
     * Jump n frames in a specified direction.
     * @param frames [Int] containing the number of frames to jump.
     * @return [Unit]
     */
    override fun jumpFrames(frames: Int) {
        jumpToFrame(grabberDiff.frameNumber + frames)
    }

    /**
     * Jump to a specified percentage of the diff video.
     * @param percentage [Double] containing the percentage to jump to, between 0 and 1.
     * @return [Double] containing the percentage that was actually jumped to.
     * @return [Double] containing the percentage that was actually jumped to.
     */
    override fun jumpToPercentage(percentage: Double): Double {
        // check bounds
        if (percentage < 0.0 || percentage > 1.0) {
            throw Exception("Percentage must be between 0 and 1.0")
        }

        // calculate the frame to jump to
        val diffFrame = (grabberDiff.lengthInFrames.toDouble() * percentage).roundToInt()

        // jump to the frame
        jumpToFrame(diffFrame)

        return diffFrame.toDouble() / grabberDiff.lengthInFrames.toDouble()
    }

    /**
     * Jump to a concretely specified frame.
     * @param index [Int] containing the index of the frame to jump to.
     * @return [Unit]
     */
    override fun jumpToFrame(index: Int) {
        // check bounds
        var indexAligned = index
        indexAligned = max(indexAligned, 0)
        indexAligned = min(indexAligned, diffSequence.size - 1)
        // jump to the frame in each video by mapping the frame using the generated sequences
        video1Grabber.setVideoFrameNumber(video1Frames[indexAligned])
        video2Grabber.setVideoFrameNumber(video2Frames[indexAligned])
        grabberDiff.setVideoFrameNumber(indexAligned)
        // update the bitmaps
        video1Bitmap.value = getBitmap(video1Grabber)
        video2Bitmap.value = getBitmap(video2Grabber)
        diffBitmap.value = getBitmap(grabberDiff)
    }

    /**
     * Get the bitmap of the current frame of the grabber.
     * @param grabber [FFmpegFrameGrabber] containing the grabber to get the bitmap from.
     * @return [ImageBitmap] containing the bitmap of the current frame.
     */
    private fun getBitmap(grabber: FFmpegFrameGrabber): ImageBitmap {
        // check bounds
        if (grabber.frameNumber < 0 || grabber.frameNumber >= grabber.lengthInFrames) {
            // return a blank image
            val bufferedIm = BufferedImage(grabber.imageWidth, grabber.imageHeight, BufferedImage.TYPE_INT_RGB)
            return bufferedIm.toComposeImageBitmap()
        }
        // grab the image
        return converter.convert(grabber.grabImage()).toComposeImageBitmap()
    }

    /**
     * Jump to the next diff.
     * @param forward [Boolean] containing whether to jump forward or backward.
     * @return [Unit]
     */
    override fun jumpToNextDiff(forward: Boolean) {
        // get the current frame
        var index = grabberDiff.frameNumber
        // create a function that increments or decrements the index
        val op: (Int) -> Int = if (forward) { x: Int -> x + 1 } else { x: Int -> x - 1 }
        // ignore current frame by jumping once
        index = op(index)
        // jump until a diff is found or the end is reached
        while (index >= 0 && index < diffSequence.size && diffSequence[index] == AlignmentElement.PERFECT) {
            index = op(index)
        }
        // jump to the frame
        jumpToFrame(index)
    }

    /**
     * get count of frames in diff as String
     * @return [String]
     */
    override fun getSizeOfDiff(): String {
        return diffSequence.size.toString()
    }
}
