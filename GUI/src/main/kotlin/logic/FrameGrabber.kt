package logic

import algorithms.AlignmentElement
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import models.AppState
import org.bytedeco.javacv.FFmpegFrameGrabber
import util.ColoredFrameGenerator
import wrappers.Resettable2DFrameConverter
import java.awt.image.BufferedImage

class FrameGrabber(state: MutableState<AppState>) {
    // create the grabbers
    private val videoReferenceGrabber: FFmpegFrameGrabber =
        FFmpegFrameGrabber(state.value.videoReferencePath)
    private val videoCurrentGrabber: FFmpegFrameGrabber =
        FFmpegFrameGrabber(state.value.videoCurrentPath)
    private val grabberDiff: FFmpegFrameGrabber = FFmpegFrameGrabber(state.value.outputPath)

    // create the sequences
    var diffSequence: Array<AlignmentElement> = state.value.sequenceObj
    private var videoReferenceFrames: MutableList<Int> = mutableListOf()
    private var videoCurrentFrames: MutableList<Int> = mutableListOf()

    // create the converter
    private val converter = Resettable2DFrameConverter()

    // create the bitmaps
    var videoReferenceBitmap: MutableState<ImageBitmap> =
        mutableStateOf(BufferedImage(1, 1, 1).toComposeImageBitmap())
    var videoCurrentBitmap: MutableState<ImageBitmap> =
        mutableStateOf(BufferedImage(1, 1, 1).toComposeImageBitmap())
    var diffBitmap: MutableState<ImageBitmap> =
        mutableStateOf(BufferedImage(1, 1, 1).toComposeImageBitmap())

    // state variables for the current frame index
    var currentIndex: Int = 0
    var currentDiffIndex: MutableState<Int> = mutableStateOf(0)
    var jumpLock = false

    private var insertionBitmap: ImageBitmap
    private var deletionBitmap: ImageBitmap

    var width: Int = 0
    var height: Int = 0

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
        insertionBitmap =
            coloredFrameGenerator
                .getColoredBufferedImage(AlignmentElement.INSERTION)
                .toComposeImageBitmap()
        deletionBitmap =
            coloredFrameGenerator
                .getColoredBufferedImage(AlignmentElement.DELETION)
                .toComposeImageBitmap()
    }

    fun getReferenceVideoFrame(index: Int): ImageBitmap {
        if (videoReferenceFrames[index] != -1) {
            videoReferenceGrabber.setVideoFrameNumber(videoReferenceFrames[index])
            return getBitmap(videoReferenceGrabber)
        } else {
            return insertionBitmap
        }
    }

    fun getCurrentVideoFrame(index: Int): ImageBitmap {
        if (videoCurrentFrames[index] != -1) {
            videoCurrentGrabber.setVideoFrameNumber(videoCurrentFrames[index])
            return getBitmap(videoCurrentGrabber)
        } else {
            return deletionBitmap
        }
    }

    fun getDiffVideoFrame(index: Int): ImageBitmap {
        grabberDiff.setVideoFrameNumber(index)
        return getBitmap(grabberDiff)
    }

    /**
     * Generate the sequences for the reference and current video.
     *
     * If there is no image for one of the sequences (because of insertions/deletions), the index
     * will be -1. This has to be handled when accessing a certain position in the alignment.
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
     * Get count of frames in diff
     * @return [Int] containing the number of frames in the diff.
     */
    fun getSizeOfDiff(): Int {
        return diffSequence.size
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
            val bufferedIm =
                BufferedImage(
                    grabber.imageWidth,
                    grabber.imageHeight,
                    BufferedImage.TYPE_INT_RGB,
                )
            return bufferedIm.toComposeImageBitmap()
        }
        // grab the image
        return converter.convert(grabber.grabImage()).toComposeImageBitmap()
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
                getBitmap(videoCurrentGrabber)
            }
        return listOf(videoReferenceBitmap, videoCurrentBitmap)
    }

    fun setOnNavigateCallback(callback: () -> Unit) {
        onNavigateCallback = callback
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

    /** Close the grabbers. */
    fun close() {
        videoReferenceGrabber.stop()
        videoCurrentGrabber.stop()
        grabberDiff.stop()
        videoReferenceGrabber.close()
        videoCurrentGrabber.close()
        grabberDiff.close()
    }
}
