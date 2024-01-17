package frameNavigation

import FrameNavigationInterface
import algorithms.AlignmentElement
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.*
import models.AppState
import org.bytedeco.javacv.FFmpegFrameGrabber
import util.ColoredFrameGenerator
import wrappers.Resettable2DFrameConverter
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

/**
 * A class that implements the [FrameNavigationInterface] interface.
 * @param state [MutableState]<[AppState]> containing the global state.
 */
class FrameNavigation(state: MutableState<AppState>, val scope: CoroutineScope) : FrameNavigationInterface {
    // create the grabbers
    private val videoReferenceGrabber: FFmpegFrameGrabber = FFmpegFrameGrabber(state.value.videoReferencePath)
    private val videoCurrentGrabber: FFmpegFrameGrabber = FFmpegFrameGrabber(state.value.videoCurrentPath)
    private val grabberDiff: FFmpegFrameGrabber = FFmpegFrameGrabber(state.value.outputPath)

    // create the sequences
    var diffSequence: Array<AlignmentElement> = state.value.sequenceObj
    private var videoReferenceFrames: MutableList<Int> = mutableListOf()
    private var videoCurrentFrames: MutableList<Int> = mutableListOf()

    // create the converter
    private val converter = Resettable2DFrameConverter()

    // create the bitmaps
    var videoReferenceBitmap: MutableState<ImageBitmap> = mutableStateOf(BufferedImage(1, 1, 1).toComposeImageBitmap())
    var videoCurrentBitmap: MutableState<ImageBitmap> = mutableStateOf(BufferedImage(1, 1, 1).toComposeImageBitmap())
    var diffBitmap: MutableState<ImageBitmap> = mutableStateOf(BufferedImage(1, 1, 1).toComposeImageBitmap())

    // state variables for the current frame index
    var currentIndex: Int = 0
    var currentDiffIndex: MutableState<Int> = mutableStateOf(0)
    var jumpLock = false

    // holds the relative position of the current frame in the diff video
    // 0.0 means the first frame, 1.0 means the last frame
    var currentRelativePosition: MutableState<Double> = mutableStateOf(0.0)

    var width: Int = 0
    var height: Int = 0

    private var insertionBitmap: ImageBitmap
    private var deletionBitmap: ImageBitmap

    private var onNavigateCallback: () -> Unit = {}

    init {
        // start the grabbers
        videoReferenceGrabber.start()
        videoCurrentGrabber.start()
        grabberDiff.start()

        // generate the sequences for video 1 and video 2
        // diffSequence is already generated
        generateSequences()

        width = grabberDiff.imageWidth
        height = grabberDiff.imageHeight

        val coloredFrameGenerator = ColoredFrameGenerator(width, height)
        insertionBitmap = coloredFrameGenerator.getColoredBufferedImage(Color.GREEN).toComposeImageBitmap()
        deletionBitmap = coloredFrameGenerator.getColoredBufferedImage(Color.BLUE).toComposeImageBitmap()

        // jump to the first frame
        jumpToFrame()
    }

    /**
     * Close the grabbers.
     */
    fun close() {
        videoReferenceGrabber.stop()
        videoCurrentGrabber.stop()
        grabberDiff.stop()
        videoReferenceGrabber.close()
        videoCurrentGrabber.close()
        grabberDiff.close()
    }

    /**
     * Generate the sequences for the reference and current video.
     *
     * If there is no image for one of the sequences (because of insertions/deletions),
     * the index will be -1. This has to be handled when accessing a certain position in the alignment.
     */
    private fun generateSequences() {
        // running indices for both videos
        var videoReferenceIndex = 0
        var videoCurrentIndex = 0
        for (i in diffSequence) {
            when (i) {
                AlignmentElement.MATCH -> {
                    videoReferenceFrames.add(videoReferenceIndex++)
                    videoCurrentFrames.add(videoCurrentIndex++)
                }

                AlignmentElement.INSERTION -> {
                    videoReferenceFrames.add(-1)
                    videoCurrentFrames.add(videoCurrentIndex++)
                }

                AlignmentElement.DELETION -> {
                    videoReferenceFrames.add(videoReferenceIndex++)
                    videoCurrentFrames.add(-1)
                }

                AlignmentElement.PERFECT -> {
                    videoReferenceFrames.add(videoReferenceIndex++)
                    videoCurrentFrames.add(videoCurrentIndex++)
                }
            }
        }
    }

    /**
     * Jump n frames in a specified direction.
     * @param frames [Int] containing the number of frames to jump.
     * @return [Unit]
     */
    override fun jumpFrames(frames: Int) {
        currentIndex = grabberDiff.frameNumber + frames
        jumpToFrame()
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
        if (diffFrame == currentIndex) {
            return
        }

        // jump to the frame
        currentIndex = diffFrame
        jumpToFrame()
    }

    /**
     * Jump to a concretely specified frame.
     * @param index [Int] containing the index of the frame to jump to.
     * @return [Unit]
     */
    override fun jumpToFrame() {
        // calculate the index to jump to; round to the nearest whole integer
        var coercedIndex = currentIndex.coerceIn(0, diffSequence.size - 1)
        // update the percentage used for rendering the timeline position
        currentRelativePosition.value = coercedIndex.toDouble() / (diffSequence.size - 1).toDouble()
        // do nothing if locked
        if (jumpLock) {
            return
        }
        // lock the jump to prevent multiple updates from running at the same time
        jumpLock = true
        // set unrealistic goal to force an update
        var goal = -1
        // launch a coroutine to update the bitmaps
        scope.launch(Dispatchers.IO) {
            // temp variables to hold the bitmaps and update all at once
            var b1: ImageBitmap? = null
            var b2: ImageBitmap? = null
            var b3: ImageBitmap? = null

            // coercedIndex can be updated outside of the coroutine, so check it every iteration
            while (goal != coercedIndex) {
                // if current rendering does not match the selected frame, update the rendering
                goal = coercedIndex

                // update the bitmaps
                if (videoReferenceFrames[goal] != -1) {
                    videoReferenceGrabber.setVideoFrameNumber(videoReferenceFrames[goal])
                    b1 = getBitmap(videoReferenceGrabber)
                } else {
                    b1 = insertionBitmap
                }
                if (videoCurrentFrames[goal] != -1) {
                    videoCurrentGrabber.setVideoFrameNumber(videoCurrentFrames[goal])
                    b2 = getBitmap(videoCurrentGrabber)
                } else {
                    b2 = deletionBitmap
                }

                grabberDiff.setVideoFrameNumber(goal)
                b3 = getBitmap(grabberDiff)

                // update the selected frame in case it changed while the coroutine was running
                coercedIndex = currentIndex.coerceIn(0, diffSequence.size - 1)
            }
            // after goal was met update the rendered bitmaps in UI thread and unlock the jump
            withContext(Dispatchers.Main) {
                videoReferenceBitmap.value = b1!!
                videoCurrentBitmap.value = b2!!
                diffBitmap.value = b3!!
            }
            currentIndex = coercedIndex
            currentDiffIndex.value = currentIndex
            jumpLock = false
        }.invokeOnCompletion {
            onNavigateCallback()
        }
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
        var index = currentIndex
        // create a function that increments or decrements the index
        val op: (Int) -> Int = if (forward) { x: Int -> x + 1 } else { x: Int -> x - 1 }
        // ignore current frame by jumping once
        index = op(index)
        // jump until a diff is found or the end is reached
        while (index >= 0 && index < diffSequence.size && diffSequence[index] == AlignmentElement.PERFECT) {
            index = op(index)
        }
        // jump to the frame
        currentIndex = index
        jumpToFrame()
    }

    /**
     * Get count of frames in diff
     * @return [Int] containing the number of frames in the diff.
     */
    override fun getSizeOfDiff(): Int {
        return diffSequence.size
    }

    /**
     * Get count of frames in reference video
     * @return [Int] containing the number of frames in the reference video.
     */
    fun getSizeOfVideoReference(): Int {
        return videoReferenceGrabber.lengthInFrames
    }

    /**
     * Get count of frames in current video
     * @return [Int] containing the number of frames in the current video.
     */
    fun getSizeOfVideoCurrent(): Int {
        return videoCurrentGrabber.lengthInFrames
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
        val width = videoReferenceGrabber.imageWidth
        var xOffset = 0
        // create the collage
        val collage =
            BufferedImage(width * 3 + border * 2, videoReferenceGrabber.imageHeight + titleHeight, BufferedImage.TYPE_INT_RGB)
        val g = collage.createGraphics()
        // fill the background with white
        g.color = java.awt.Color.WHITE
        g.fillRect(0, 0, collage.width, collage.height)

        // draw the images
        for (item in listOf(videoReferenceBitmap, diffBitmap, videoCurrentBitmap)) {
            val img = item.value.toAwtImage()
            g.drawImage(img, xOffset, titleHeight, null)
            xOffset += width + border
        }

        // draw the titles
        g.color = java.awt.Color.BLACK
        g.font = font
        xOffset = 0
        for (item in listOf("Reference Video", "Diff", "Current Video")) {
            val metrics = g.fontMetrics
            val x = (width - metrics.stringWidth(item)) / 2
            g.drawString(item, x + xOffset, titleHeight - 10)
            xOffset += width + border
        }

        // save the collage
        val file = java.io.File(outputPath)
        javax.imageio.ImageIO.write(collage, "png", file)
    }

    /**
     * Filters all inserted frames.
     * @return [List]<[ImageBitmap]> containing the inserted frames.
     */
    fun getInsertedFrames(): List<ImageBitmap> {
        val insertedFrames = mutableListOf<ImageBitmap>()

        for (i in diffSequence.indices) {
            if (diffSequence[i] == AlignmentElement.INSERTION) {
                videoCurrentGrabber.setVideoFrameNumber(videoCurrentFrames[i])
                insertedFrames.add(getBitmap(videoCurrentGrabber))
            }
        }

        return insertedFrames
    }

    /**
     * Get the images at a certain diff index.
     *
     * If the index is an insertion or deletion, the corresponding bitmap will be returned.
     *
     * @param diffIndex [Int] containing the index of the diff.
     * @return [List]<[ImageBitmap]> containing the bitmaps of the images.
     */
    fun getImagesAtDiff(diffIndex: Int): List<ImageBitmap> {
        val videoReferenceIndex = videoReferenceFrames[diffIndex]
        val videoCurrentIndex = videoCurrentFrames[diffIndex]
        val videoReferenceBitmap =
            if (videoReferenceIndex == -1) {
                insertionBitmap
            } else {
                videoReferenceGrabber.setVideoFrameNumber(videoReferenceIndex)
                getBitmap(videoReferenceGrabber)
            }
        val videoCurrentBitmap =
            if (videoCurrentIndex == -1) {
                deletionBitmap
            } else {
                videoCurrentGrabber.setVideoFrameNumber(videoCurrentIndex)
                getBitmap(videoReferenceGrabber)
            }
        return listOf(videoReferenceBitmap, videoCurrentBitmap)
    }

    fun setOnNavigateCallback(callback: () -> Unit) {
        onNavigateCallback = callback
    }
}
