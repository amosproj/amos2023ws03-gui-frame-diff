package ui.components.diffScreen.timeline

import algorithms.AlignmentElement
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import frameNavigation.FrameNavigation

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

    var boxWidth by remember { mutableStateOf(0.0f) }

    indicatorOffset.value = (navigator.currentDiffIndex.value + 0.5f) * boxWidth

    fun jumpOffsetHandler(offset: Offset) {
        cursorOffset = offset
        val clickedFrame = ((offset.x) / boxWidth).toInt()
        navigator.jumpToFrame(clickedFrame)
    }

    Box(modifier = modifier) {
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
                            onDrag = { _, dragAmount -> jumpOffsetHandler(cursorOffset + dragAmount) },
                        )
                    },
        ) {
            for (item in 0 until navigator.diffSequence.size) {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .layout { measurable, constraints ->
                                val placeable1 = measurable.measure(constraints)
                                // Store the width
                                boxWidth = placeable1.width.toFloat()

                                layout(placeable1.width, placeable1.height) {
                                    placeable1.placeRelative(0, 0)
                                }
                            }
                            .background(
                                when (navigator.diffSequence[item]) {
                                    AlignmentElement.DELETION -> Color.Blue
                                    AlignmentElement.INSERTION -> Color.Green
                                    AlignmentElement.MATCH -> Color.Yellow
                                    AlignmentElement.PERFECT -> Color.Black
                                },
                            ),
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
