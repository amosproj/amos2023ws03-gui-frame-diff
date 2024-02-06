// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: Simon Sasse <simonsasse97@gmail.com>

/**
 * Abstract class for the DifferenceGenerator.
 *
 * @param videoReferencePath the path to the reference video
 * @param videoCurrentPath the path to the current video
 * @param outputPath the path to the output file
 */
abstract class AbstractDifferenceGenerator {
    /**
     * Generates the difference between the two videos.
     *
     * Calls the saveDifferences() method.
     */
    abstract fun generateDifference()
}
