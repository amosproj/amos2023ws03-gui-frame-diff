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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import ui.components.general.AutoSizeText
import kotlin.math.max

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

    // current percentage on the cursor as Int between 0 and 100
    val currentPercentage = (navigatorUpdated.currentRelativePosition.value * 100).toInt()
    // current x-offset of the indicator
    val currentOffset = (navigatorUpdated.currentRelativePosition.value * componentWidth).toFloat()
    // current x-offset of the indicator as dp to show current percentage
    val currentOffsetDp = with(LocalDensity.current) { currentOffset.toDp() }
    // width of text component to center the current percentage over the cursor
    var cursorOffset = Offset.Zero

    val stateHorizontal = rememberLazyListState()
    var framesPerView by remember { mutableStateOf(1) }

    val frameSize: Pair<Int, Int> = navigator.getFrameSize()
    val totalDiffFrames = navigator.getSizeOfDiff()

    fun jumpPercentageHandler(offset: Offset) {
        cursorOffset = offset
        navigatorUpdated.jumpToPercentage((cursorOffset.x.toDouble() / componentWidth).coerceIn(0.0, 1.0))
    }

    fun determineFramesPerView(): Int {
        val relativeHeightOfTimeLineRow = 0.6
        val aspectRatioTimeline = componentWidth / (componentHeight * relativeHeightOfTimeLineRow)
        val aspectRatioFrame = frameSize.first / frameSize.second
        val framesPerView = (aspectRatioTimeline / aspectRatioFrame).toInt()
        return if (framesPerView == 0) 1 else framesPerView
    }

    fun determineNumSlidingWindows(): Int {
        return max(1, totalDiffFrames - framesPerView + 1)
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

                        // compute how many frame thumbnails fit into the timeline
                        framesPerView = determineFramesPerView()
                        // stateHorizontal.maxValue = determineNumSlidingWindows() - 1
                        layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
                    }
                    // handle clicks and drags on the timeline
                    .pointerInput(Unit) { detectTapGestures { offset -> jumpPercentageHandler(offset) } }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset -> jumpPercentageHandler(offset) },
                            onDrag = { _, dragAmount -> jumpPercentageHandler(cursorOffset + dragAmount) },
                        )
                    },
        ) {
            // #### clickable timeline ####
            TimelineThumbnails(
                modifier = Modifier,
                navigator = navigator,
                nFrames = totalDiffFrames,
                scrollState = stateHorizontal,
            )
            drawRedLine(currentOffset)
        }

        HorizontalScrollbar(
            modifier =
                Modifier
                    .fillMaxWidth(0.8f)
                    .padding(top = 5.dp)
                    .border(width = 1.dp, color = Color.LightGray, shape = CircleShape),
            adapter = rememberScrollbarAdapter(scrollState = stateHorizontal),
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
    println(scrollState.firstVisibleItemIndex)
    println(scrollState.firstVisibleItemScrollOffset)
    LazyRow(
        state = scrollState,
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items(nFrames) { i ->
            val images = navigator.getImagesAtDiff(i)
            Column(modifier = Modifier) {
                Box(
                    modifier =
                        Modifier
                            .weight(0.5f)
                            .border(width = 1.dp, color = Color.Black, shape = RectangleShape),
                ) {
                    Image(
                        bitmap = images[0],
                        contentDescription = null,
                        modifier = Modifier.fillMaxHeight(),
                    )
                }

                Box(
                    modifier =
                        Modifier
                            .weight(0.5f)
                            .border(width = 1.dp, color = Color.Black, shape = RectangleShape),
                ) {
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
