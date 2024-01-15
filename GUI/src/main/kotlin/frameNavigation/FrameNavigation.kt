package frameNavigation

import FrameNavigationInterface
import algorithms.AlignmentElement
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import models.AppState
import org.bytedeco.javacv.FFmpegFrameGrabber
import wrappers.Resettable2DFrameConverter
import java.awt.image.BufferedImage
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

    // state variables for the current frame index
    var currentIndex: MutableState<Int> = mutableStateOf(0)

    // holds the relative position of the current frame in the diff video
    // 0.0 means the first frame, 1.0 means the last frame
    var currentRelativePosition: MutableState<Double> = mutableStateOf(0.0)

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
     */
    override fun jumpToPercentage(percentage: Double) {
        // check bounds
        if (percentage < 0.0 || percentage > 1.0) {
            throw Exception("Percentage must be between 0.0 and 1.0")
        }

        // calculate the index to jump to; round to the nearest whole integer
        val diffFrame = ((diffSequence.size - 1).toDouble() * percentage).roundToInt()

        // check if the frame is already displayed
        if (diffFrame == currentIndex.value) {
            return
        }

        // jump to the frame
        jumpToFrame(diffFrame)
    }

    /**
     * Jump to a concretely specified frame.
     * @param index [Int] containing the index of the frame to jump to.
     * @return [Unit]
     */
    override fun jumpToFrame(index: Int) {
        // check bounds
        val boundedIndex = index.coerceIn(0, diffSequence.size - 1)
        currentIndex.value = boundedIndex
        currentRelativePosition.value = boundedIndex.toDouble() / (diffSequence.size - 1).toDouble()

        // jump to the frame in each video by mapping the frame using the generated sequences
        video1Grabber.setVideoFrameNumber(video1Frames[boundedIndex])
        video2Grabber.setVideoFrameNumber(video2Frames[boundedIndex])
        grabberDiff.setVideoFrameNumber(boundedIndex)

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
        var index = currentIndex.value
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
     * Get count of frames in diff
     * @return [Int] containing the number of frames in the diff.
     */
    override fun getSizeOfDiff(): Int {
        return diffSequence.size
    }

    /**
     * Get count of frames in video1
     * @return [Int] containing the number of frames in the first video.
     */
    fun getSizeOfVideo1(): Int {
        return video1Grabber.lengthInFrames
    }

    /**
     * Get count of frames in video2
     * @return [Int] containing the number of frames in the second video.
     */
    fun getSizeOfVideo2(): Int {
        return video2Grabber.lengthInFrames
    }

    /**
     * Get count of insertions
     * @return [Int] containing the number of insertions.
     */
    fun getInsertions(): Int {
        return diffSequence.count { it == AlignmentElement.INSERTION }
    }

    /**
     * Get count of deletions
     * @return [Int] containing the number of deletions.
     */
    fun getDeletions(): Int {
        return diffSequence.count { it == AlignmentElement.DELETION }
    }

    /**
     * Get count of frames with pixel differences
     * @return [Int] containing the number of frames with pixel differences.
     */
    fun getFramesWithPixelDifferences(): Int {
        return diffSequence.count { it == AlignmentElement.MATCH }
    }

    /**
     * Creates a collage of the 3 videos and saves it to a file.
     * @param outputPath [String] containing the path to save the collage to.
     * @param border [Int] containing the width of the border between the videos.
     * @param titleHeight [Int] containing the height of the title.
     * @param font [java.awt.Font] containing the font to use for the title.
     * @return [Unit]
     */
    fun createCollage(
        outputPath: String,
        border: Int = 20,
        titleHeight: Int = 100,
        font: java.awt.Font =
            java.awt.Font(
                "Arial",
                java.awt.Font.BOLD,
                100,
            ),
    ) {
        val width = video1Grabber.imageWidth
        var xOffset = 0
        // create the collage
        val collage = BufferedImage(width * 3 + border * 2, video1Grabber.imageHeight + titleHeight, BufferedImage.TYPE_INT_RGB)
        val g = collage.createGraphics()
        // fill the background with white
        g.color = java.awt.Color.WHITE
        g.fillRect(0, 0, collage.width, collage.height)

        // draw the images
        for (item in listOf(video1Bitmap, diffBitmap, video2Bitmap)) {
            val img = item.value.toAwtImage()
            g.drawImage(img, xOffset, titleHeight, null)
            xOffset += width + border
        }

        // draw the titles
        g.color = java.awt.Color.BLACK
        g.font = font
        xOffset = 0
        for (item in listOf("Video 1", "Diff", "Video 2")) {
            val metrics = g.fontMetrics
            val x = (width - metrics.stringWidth(item)) / 2
            g.drawString(item, x + xOffset, titleHeight - 10)
            xOffset += width + border
        }

        // save the collage
        val file = java.io.File("$outputPath.png")
        javax.imageio.ImageIO.write(collage, "png", file)
    }
}
