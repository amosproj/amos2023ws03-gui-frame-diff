// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
package ui.components.diffScreen.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import util.ColorEncoding

/**
 * A Composable function that displays an overview timeline showing all difference positions at once,
 * indicating alignment elements by color.
 *
 * Different colored rectangles are drawn for each alignment element (insertion, deletion, match, perfect match).
 * The bar reacts to point and drag events leading to invocations of the given [FrameNavigation] object.
 *
 * @param navigator [FrameNavigation] containing the navigation logic.
 * @param modifier [Modifier] The modifier for the timeline.
 */
@Composable
fun OverviewBar(
    navigator: FrameNavigation,
    modifier: Modifier = Modifier,
) {
    val indicatorOffset = remember { mutableStateOf(0f) }

    // width of text component to center the current percentage over the cursor
    var cursorOffset = Offset.Zero

    // approximation of the boxes' width, only used to render the indicator centered
    var boxWidth by remember { mutableStateOf(0.0f) }

    var componentWidth by remember { mutableStateOf(0.0f) }

    // list of offsets for the boxes, so we can search for the correct frame when clicked
    val offsetList = remember { mutableStateOf(MutableList(navigator.diffSequence.size) { 1f }) }

    indicatorOffset.value = (offsetList.value[navigator.currentDiffIndex.value] + 0.5 * boxWidth).toFloat()

    /**
     * Handles the jump to a specific frame when the user clicks on the overview bar.
     *
     * Usually, we would calculate the frame by dividing the x offset by the box width.
     * Problem is, that for a huge number of boxes, we are not guaranteed to get the same render width
     * for every box. Therefore, we need to save all render offsets for the boxes and
     * perform a binary search to find the correct frame when clicked or dragged.
     *
     * @param offset The offset of the click event.
     */
    fun jumpOffsetHandler(
        offset: Offset,
        dragOffset: Offset = Offset.Zero,
    ) {
        cursorOffset = offset + dragOffset

        // binary search for the frame
        // for a diff sequence of 1000, this takes at most log2(1000) = 10 iterations, so that's not a bottleneck
        var lower = 0
        var upper = navigator.diffSequence.size - 1

        // if the drag offset is not zero, we limit the search space
        if (dragOffset.x < 0f) {
            upper = navigator.currentDiffIndex.value + 1
        } else if (dragOffset.x > 0f) {
            lower = navigator.currentDiffIndex.value
        }

        // perform the binary search
        while (lower < upper) {
            val mid = (lower + upper) / 2
            if (offsetList.value[mid] < cursorOffset.x) {
                lower = mid + 1
            } else {
                upper = mid
            }
        }

        // edge case of the last frame
        if (lower == navigator.diffSequence.size - 1 && offset.x > offsetList.value[lower]) {
            lower += 1
        }

        navigator.jumpToFrame(lower - 1)
    }

    Box(
        modifier =
            modifier.layout
                { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    componentWidth = placeable.width.toFloat()
                    boxWidth = componentWidth / navigator.diffSequence.size
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(0, 0)
                    }
                },
    ) {
        Row(
            modifier =
                Modifier
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            jumpOffsetHandler(offset)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset -> jumpOffsetHandler(offset) },
                            onDrag = { _, dragAmount -> jumpOffsetHandler(cursorOffset, dragAmount) },
                        )
                    }
                    .fillMaxSize(),
        ) {
            for (i in 0 until navigator.diffSequence.size) {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(ColorEncoding.elementToColor[navigator.diffSequence[i]]!!.rgb))
                            .padding(0.dp)
                            .onGloballyPositioned {
                                val pos = it.positionInParent()
                                offsetList.value[i] = pos.x
                            },
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            PositionIndicator(indicatorOffset.value)
        }
    }
}
