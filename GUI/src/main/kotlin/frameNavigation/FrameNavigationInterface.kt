// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: AlperK61 <92909013+AlperK61@users.noreply.github.com>
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>

/**
 * Provides navigation methods for frames in a video.
 *
 * This interface defines methods to navigate frames in various ways, including jumping a specified number
 * of frames in a given direction, jumping to a specific percentage of the video, jumping to a specific frame,
 * and jumping to the next difference frame. Implementing classes should provide implementation for these methods.
 */
interface FrameNavigationInterface {
    /**
     * Jump n frames in a specified direction
     */
    fun jumpFrames(frames: Int)

    /**
     * Jump to a specified percentage of the diff video
     */
    fun jumpToPercentage(percentage: Double)

    /**
     * Jump to a concretely specified frame
     */
    fun jumpToFrame(index: Int)

    /**
     * Jump to the next diff
     */
    fun jumpToNextDiff(forward: Boolean)
}
