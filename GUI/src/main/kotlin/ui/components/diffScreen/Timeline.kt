package ui.components.diffScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import ui.components.general.AutoSizeText

/**
 * A Composable function that creates a box to display the timeline.
 * @param navigator [FrameNavigation] containing the navigator.
 * @return [Unit]
 */
@Composable
fun Timeline(navigator: FrameNavigation) {
    val navigatorUpdated by rememberUpdatedState(navigator)
    // set the width of the timeline-box
    var componentWidth by remember { mutableStateOf(0.8f) }
    // current percentage on the cursor as Int between 0 and 100
    val currentPercentage = (navigatorUpdated.currentRelativePosition.value * 100).toInt()
    // current x-offset of the indicator
    val currentOffset = (navigatorUpdated.currentRelativePosition.value * componentWidth).toFloat()
    // current x-offset of the indicator as dp to show current percentage
    val currentOffsetDp = with(LocalDensity.current) { currentOffset.toDp() }
    // width of text component to center the current percentage over the cursor
    var cursorOffset = Offset.Zero

    fun jumpPercentageHandler(offset: Offset) {
        cursorOffset = offset
        navigatorUpdated.jumpToPercentage((cursorOffset.x.toDouble() / componentWidth).coerceIn(0.0, 1.0))
    }

    Column(
        modifier = Modifier.background(color = Color.Gray).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // #### timeline labeling ####
        TimelineTopLabels(currentPercentage, currentOffsetDp, navigatorUpdated)
        // #### timeline box ####
        Box(
            modifier =
                Modifier
                    .background(color = Color.LightGray)
                    .fillMaxWidth(0.8f)
                    .height(100.dp)
                    .weight(1f)
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                    )
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        // Store the width
                        componentWidth = placeable.width.toFloat()
                        layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
                    }
                    .pointerInput(Unit) { detectTapGestures { offset -> jumpPercentageHandler(offset) } }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset -> jumpPercentageHandler(offset) },
                            onDrag = { _, dragAmount -> jumpPercentageHandler(cursorOffset + dragAmount) },
                        )
                    },
        ) {
            DrawRedLine(currentOffset)
            // #### clickable timeline ####
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                AlignedSizedText("0%", Alignment.CenterStart, 20.dp)
                AlignedSizedText("50%", Alignment.Center, 20.dp)
                AlignedSizedText("100%", Alignment.CenterEnd, 20.dp)
            }
        }
    }
}

/**
 * A Composable function that draws a red line on the timeline.
 * @param currentOffset [Float] containing the current x-offset of the indicator.
 * @return [Unit]
 */
@Composable
private fun DrawRedLine(currentOffset: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(
            start = Offset(currentOffset, 0f),
            end = Offset(currentOffset, size.height),
            color = Color.Red,
            strokeWidth = 6f,
        )
    }
}

/**
 * A Composable function that creates labels for the timeline.
 * @param currentPercentage [Int] containing the current percentage on the cursor as Int between 0 and 100.
 * @param currentOffsetDp [Dp] containing the current x-offset of the indicator as dp to show current percentage.
 * @param navigator [FrameNavigation] containing the navigator.
 * @return [Unit]
 */
@Composable
private fun TimelineTopLabels(
    currentPercentage: Int,
    currentOffsetDp: Dp,
    navigator: FrameNavigation,
) {
    var textWidth by remember { mutableStateOf(0f) }
    // Labels Container
    Box(
        modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.3f),
    ) {
        // Starting Label
        AlignedSizedText("0", Alignment.CenterStart, 2.dp)
        // Current Percentage Label
        AlignedSizedText(
            text = "$currentPercentage%",
            alignment = Alignment.TopStart,
            padding = 2.dp,
            modifier =
                Modifier.onGloballyPositioned { coordinates ->
                    textWidth = coordinates.size.width.toFloat()
                }.offset(x = (currentOffsetDp - (textWidth / 3).dp)),
        )
        // Ending Label
        AlignedSizedText("${navigator.getSizeOfDiff()}", Alignment.CenterEnd, 2.dp)
    }
}

/**
 * A Composable function that creates a text component with a given alignment and padding.
 * @param text [String] containing the text to display.
 * @param alignment [Alignment] containing the alignment of the text.
 * @param padding [Dp] containing the padding of the text.
 * @param modifier [Modifier] containing the modifier of the text.
 * @return [Unit]
 */
@Composable
fun BoxScope.AlignedSizedText(
    text: String,
    alignment: Alignment,
    padding: Dp = 0.dp,
    modifier: Modifier = Modifier,
) {
    AutoSizeText(
        text = text,
        color = Color.Black,
        modifier = modifier.align(alignment).padding(padding),
    )
}
