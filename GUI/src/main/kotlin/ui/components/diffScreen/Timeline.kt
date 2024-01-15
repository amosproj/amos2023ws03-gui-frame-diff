package ui.components.diffScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
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
    // set the width of the timelinebox
    var componentWidth by remember { mutableStateOf(0.0f) }
    var componentHeight by remember { mutableStateOf(0.0f) }

    // width of text component to center the current percentage over the cursor
    var cursorOffset = Offset.Zero

    val scrollState = rememberLazyListState()
    var thumbnailWidth by remember { mutableStateOf(0.0f) }

    var indicatorOffset by remember { mutableStateOf(0.0f) }
    val totalDiffFrames = navigator.getSizeOfDiff()

    fun jumpOffsetHandler(offset: Offset) {
        cursorOffset = offset
        val clickedFrame =
            (
                (offset.x + scrollState.firstVisibleItemScrollOffset) / thumbnailWidth
            ).toInt() + scrollState.firstVisibleItemIndex
        navigatorUpdated.currentIndex = clickedFrame
        navigatorUpdated.currentDiffIndex.value = clickedFrame
        navigatorUpdated.jumpToFrame()
    }

    fun getIndicatorOffset(): Float {
        return (
            (navigatorUpdated.currentDiffIndex.value - scrollState.firstVisibleItemIndex) *
                thumbnailWidth + thumbnailWidth / 2 - scrollState.firstVisibleItemScrollOffset
        )
    }

    fun getThumbnailWidth(): Float {
        return navigator.width.toFloat() / navigator.height * componentHeight * 0.5f
    }

    indicatorOffset = getIndicatorOffset()

    Column(
        modifier = Modifier.background(color = Color.Gray).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // #### timeline labeling ####

        // #### timeline box ####
        Box(
            modifier =
                Modifier
                    .background(color = Color.LightGray)
                    .fillMaxWidth(0.8f)
                    .height(200.dp)
                    .weight(1f)
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                    )
                    // calculate the width of the timeline-box
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        // Store the width
                        componentWidth = placeable.width.toFloat()
                        componentHeight = placeable.height.toFloat()

                        thumbnailWidth = getThumbnailWidth()
                        layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
                    }
                    // handle clicks and drags on the timeline
                    .pointerInput(Unit) { detectTapGestures { offset -> jumpOffsetHandler(offset) } }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset -> jumpOffsetHandler(offset) },
                            onDrag = { _, dragAmount -> jumpOffsetHandler(cursorOffset + dragAmount) },
                        )
                    },
        ) {
            // #### clickable timeline ####
            TimelineThumbnails(
                modifier = Modifier,
                navigator = navigatorUpdated,
                nFrames = totalDiffFrames,
                scrollState = scrollState,
            )
            if (indicatorOffset > 0 && indicatorOffset < componentWidth) {
                DrawRedLine(indicatorOffset)
            }
        }

        HorizontalScrollbar(
            modifier =
                Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 5.dp)
                    .border(width = 1.dp, color = Color.LightGray, shape = CircleShape),
            adapter = rememberScrollbarAdapter(scrollState = scrollState),
        )
    }
}

@Composable
private fun TimelineThumbnails(
    modifier: Modifier,
    navigator: FrameNavigation,
    nFrames: Int,
    scrollState: LazyListState,
) {
    LazyRow(
        state = scrollState,
        modifier = modifier.fillMaxSize(),
    ) {
        items(nFrames) { i ->
            val images = navigator.getImagesAtDiff(i)
            Column {
                Box(modifier = Modifier.weight(0.5f)) {
                    Image(
                        bitmap = images[0],
                        contentDescription = null,
                        modifier = Modifier.fillMaxHeight(),
                    )
                }

                Box(modifier = Modifier.weight(0.5f)) {
                    Image(
                        bitmap = images[1],
                        contentDescription = null,
                        modifier = Modifier.fillMaxHeight(),
                    )
                }
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
private fun DrawRedLine(offset: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(
            start = Offset(offset, 0f),
            end = Offset(offset, size.height),
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
        modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.2f),
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
