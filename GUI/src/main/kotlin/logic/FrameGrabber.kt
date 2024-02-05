package logic

import algorithms.AlignmentElement
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import models.AppState
import org.bytedeco.javacv.FFmpegFrameGrabber
import util.ColoredFrameGenerator
import wrappers.Resettable2DFrameConverter
import java.awt.image.BufferedImage

/**
 * Class to grab frames from the input and difference videos.
 *
 * Warning: Grabbing a frame of any video is not thread-safe. Do not call any of the grabbing
 * functions from different threads at the same time! Use a locking mechanism to prevent race conditions.
 *
 * @param state [MutableState]<[AppState]> containing the global state. Needed to get the file paths to the videos.
 */
class FrameGrabber(state: MutableState<AppState>) {
    // create the grabbers
    private val videoReferenceGrabber: FFmpegFrameGrabber =
        FFmpegFrameGrabber(state.value.videoReferencePath)
    private val videoCurrentGrabber: FFmpegFrameGrabber =
        FFmpegFrameGrabber(state.value.videoCurrentPath)
    private val grabberDiff: FFmpegFrameGrabber = FFmpegFrameGrabber(state.value.outputPath)

    // create the sequences
    private val diffSequence: Array<AlignmentElement> = state.value.sequenceObj
    private var videoReferenceFrames: MutableList<Int> = mutableListOf()
    private var videoCurrentFrames: MutableList<Int> = mutableListOf()

    // create the converter
    private val converter = Resettable2DFrameConverter()

    private var insertionBitmap: ImageBitmap
    private var deletionBitmap: ImageBitmap

    var width: Int = 0
    var height: Int = 0

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

    /**
     * Get the reference video frame at a certain index.
     * @param index [Int] containing the index of the frame.
     * @return [ImageBitmap] containing the bitmap of the frame.
     */
    fun getReferenceVideoFrame(index: Int): ImageBitmap {
        return if (videoReferenceFrames[index] != -1) {
            videoReferenceGrabber.setVideoFrameNumber(videoReferenceFrames[index])
            getBitmap(videoReferenceGrabber)
        } else {
            insertionBitmap
        }
    }

    /**
     * Get the current video frame at a certain index.
     * @param index [Int] containing the index of the frame.
     * @return [ImageBitmap] containing the bitmap of the frame.
     */
    fun getCurrentVideoFrame(index: Int): ImageBitmap {
        return if (videoCurrentFrames[index] != -1) {
            videoCurrentGrabber.setVideoFrameNumber(videoCurrentFrames[index])
            getBitmap(videoCurrentGrabber)
        } else {
            deletionBitmap
        }
    }

    /**
     * Get the difference video frame at a certain index.
     * @param index [Int] containing the index of the frame.
     * @return [ImageBitmap] containing the bitmap of the frame.
     */
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

    /**
     * Filters all inserted frames.
     * @return [List]<[ImageBitmap]> containing the inserted frames.
     */
    fun getInsertedFrames(): List<ImageBitmap> {
        val insertedFrames = mutableListOf<ImageBitmap>()

        for (i in diffSequence.indices) {
            if (diffSequence[i] == AlignmentElement.INSERTION) {
                insertedFrames.add(getCurrentVideoFrame(i))
            }
        }

        return insertedFrames
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
