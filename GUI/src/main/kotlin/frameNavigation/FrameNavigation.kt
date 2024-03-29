// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: AlperK61 <92909013+alperk61@users.noreply.github.com>
// SPDX-FileCopyrightText: AlperK61 <92909013+AlperK61@users.noreply.github.com>
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: Fabian Seitz <github@seitzfabian.de>
// SPDX-FileCopyrightText: fs3itz <147770064+fs3itz@users.noreply.github.com>
// SPDX-FileCopyrightText: simonsasse <simonsasse97@gmail.com>
package frameNavigation

import FrameNavigationInterface
import algorithms.AlignmentElement
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import models.AppState
import kotlin.math.roundToInt

/**
 * A class that implements the [FrameNavigationInterface] interface.
 * @param state [MutableState]<[AppState]> containing the global state.
 */
class FrameNavigation(state: MutableState<AppState>) : FrameNavigationInterface {
    // create the sequences
    var diffSequence: Array<AlignmentElement> = state.value.sequenceObj

    // state variables for the current frame index
    private var currentIndex: Int = 0
    var currentDiffIndex: MutableState<Int> = mutableStateOf(0)

    // holds the relative position of the current frame in the diff video
    // 0.0 means the first frame, 1.0 means the last frame
    var currentRelativePosition: MutableState<Double> = mutableStateOf(0.0)

    private var onNavigateCallback: () -> Unit = {}

    init {
        // jump to the first frame
        jump()
    }

    /**
     * Jump n frames in a specified direction.
     * @param frames [Int] containing the number of frames to jump.
     * @return [Unit]
     */
    override fun jumpFrames(frames: Int) {
        currentIndex += frames
        jump()
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
        jump()
    }

    /**
     * Jump to a concretely specified frame.
     * @param index [Int] containing the index of the frame to jump to.
     * @return [Unit]
     */
    override fun jumpToFrame(index: Int) {
        currentIndex = index
        jump()
    }

    /**
     * Jump to the frame with the previously set index.
     *
     * Triggers the onNavigateCallback at the end.
     * @return [Unit]
     */
    private fun jump() {
        // calculate the index to jump to; round to the nearest whole integer
        val coercedIndex = currentIndex.coerceIn(0, diffSequence.size - 1)

        // update the percentage and diff index used for rendering the timeline position
        currentRelativePosition.value = coercedIndex.toDouble() / (diffSequence.size - 1).toDouble()
        currentDiffIndex.value = coercedIndex

        // trigger specific actions for the new navigation index
        onNavigateCallback()
    }

    /**
     * Jump to the next diff. If no next diff exists, nothing happens.
     * @param forward [Boolean] containing whether to jump forward or backward.
     * @return [Unit]
     */
    override fun jumpToNextDiff(forward: Boolean) {
        val index = nextDiffIndex(forward)

        // dont jump to a frame, if no next diff exists
        if (index == -1) {
            return
        }

        // jump to the frame
        currentIndex = index
        jump()
    }

    /**
     * Check if there is a next frame in the specified direction.
     * @param forward [Boolean] containing whether to check forward or backward.
     * @return [Boolean] containing whether there is a next frame.
     */
    fun hasNextFrame(forward: Boolean): Boolean {
        return if (forward) {
            currentDiffIndex.value < diffSequence.size - 1
        } else {
            currentDiffIndex.value > 0
        }
    }

    /**
     * Check if there is a next difference (insertion, deletion, pixel diff) in the specified direction.
     * @param forward [Boolean] containing whether to check forward or backward.
     * @return [Boolean] containing whether there is a next diff.
     */
    fun hasNextDiff(forward: Boolean): Boolean {
        return nextDiffIndex(forward) != -1
    }

    /**
     * Get the index of the next diff in the specified direction.
     *
     * IMPORTANT: This has to use the mutableState variable currentDiffIndex instead of currentIndex,
     * as otherwise no UI update will be triggered with a state change.
     *
     * @param forward [Boolean] containing whether to check forward or backward.
     * @return [Int] containing the index of the next diff.
     */
    private fun nextDiffIndex(forward: Boolean): Int {
        return if (forward) {
            val idx = diffSequence.drop(currentDiffIndex.value + 1).indexOfFirst { it != AlignmentElement.PERFECT }
            if (idx == -1) -1 else idx + currentDiffIndex.value + 1
        } else {
            diffSequence.take(currentDiffIndex.value).indexOfLast { it != AlignmentElement.PERFECT }
        }
    }

    /**
     * Set the callback to be triggered when the navigator jumps.
     * @param callback [Function0]<[Unit]> containing the callback to be triggered.
     * @return [Unit]
     */
    fun setOnNavigateCallback(callback: () -> Unit) {
        onNavigateCallback = callback
    }
}
