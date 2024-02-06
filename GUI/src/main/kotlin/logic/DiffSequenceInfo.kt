// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
package logic

import algorithms.AlignmentElement

/**
 * Utility class to get information about a diff sequence.
 * @param diffSequence The diff sequence to get information about.
 */
class DiffSequenceInfo(private val diffSequence: Array<AlignmentElement>) {
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
     * Get count of frames in reference video
     * @return [Int] containing the number of frames in the reference video.
     */
    fun getSizeOfVideoReference(): Int {
        return diffSequence.size - getInsertions()
    }

    /**
     * Get count of frames in current video
     * @return [Int] containing the number of frames in the current video.
     */
    fun getSizeOfVideoCurrent(): Int {
        return diffSequence.size - getDeletions()
    }

    /**
     * Get count of frames in diff
     * @return [Int] containing the number of frames in the diff.
     */
    fun getSizeOfDiff(): Int {
        return diffSequence.size
    }
}
