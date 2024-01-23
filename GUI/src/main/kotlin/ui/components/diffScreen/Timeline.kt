package ui.components.diffScreen

import algorithms.AlignmentElement
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
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import kotlinx.coroutines.launch
import logic.caches.ThumbnailCache
import ui.components.general.AutoSizeText

/**
 * A Composable function that creates a box to display the timeline.
 * @param navigator [FrameNavigation] containing the navigation logic.
 * @return [Unit]
 */
@Composable
fun Timeline(navigator: FrameNavigation) {
    val navigatorUpdated by rememberUpdatedState(navigator)
    val scope = rememberCoroutineScope()
    // set the width of the timeline box
    var componentWidth by remember { mutableStateOf(0.0f) }
    var boxWidth by remember { mutableStateOf(0.0f) }
    var componentHeight by remember { mutableStateOf(0.0f) }

    // width of text component to center the current percentage over the cursor
    var cursorOffset = Offset.Zero

    // scroll state of the timeline
    val scrollState = rememberLazyListState()

    // thumbnail cache
    val thumbnailCache = remember { ThumbnailCache(maxCacheSize = 30, navigatorUpdated::getImagesAtDiff) }

    // display width of a single thumbnail in the timeline (2 thumbnails are stacked)
    var thumbnailWidth by remember { mutableStateOf(0.0f) }

    var indicatorOffset by remember { mutableStateOf(0.0f) }
    val overviewIndicatorOffset = remember { mutableStateOf(0f) }
    val totalDiffFrames = navigator.getSizeOfDiff()

    fun overviewJumpOffsetHandler(offset: Offset) {
        cursorOffset = offset
        val clickedFrame =
            (
                (offset.x) / boxWidth
            ).toInt()

        navigatorUpdated.currentIndex = clickedFrame
        navigatorUpdated.currentDiffIndex.value = clickedFrame
        navigatorUpdated.jumpToFrame()
    }

    fun jumpOffsetHandler(offset: Offset) {
        cursorOffset = offset
        val clickedFrame =
            (
                (offset.x + scrollState.firstVisibleItemScrollOffset) / thumbnailWidth
            ).toInt() + scrollState.firstVisibleItemIndex
        navigatorUpdated.jumpToFrame(clickedFrame)
    }

    fun getThumbnailWidth(): Float {
        return navigator.width.toFloat() / navigator.height * componentHeight * 0.5f
    }

    indicatorOffset = getCenteredThumbnailOffset(scrollState, navigatorUpdated.currentDiffIndex.value, thumbnailWidth)
    overviewIndicatorOffset.value = (navigatorUpdated.currentDiffIndex.value + 0.5f) * boxWidth

    // set the modifier applied to all timeline components
    val generalModifier = Modifier.fillMaxWidth(0.8f)

    navigator.setOnNavigateCallback {
        indicatorOffset = getCenteredThumbnailOffset(scrollState, navigator.currentDiffIndex.value, thumbnailWidth)

        if (indicatorOffset < 0 || indicatorOffset > componentWidth) {
            scope.launch {
                val thumbnailsInView = (componentWidth / thumbnailWidth).toInt()
                scrollState.animateScrollToItem(
                    (navigator.currentDiffIndex.value - thumbnailsInView / 2).coerceIn(
                        0,
                        totalDiffFrames - 1,
                    ),
                )
            }
        }
    }

    Column(
        modifier = Modifier.background(color = Color.Gray).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.weight(0.5f).fillMaxWidth(0.8f)) {
            Row(
                modifier =
                    Modifier
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                overviewJumpOffsetHandler(offset)
                            }
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset -> overviewJumpOffsetHandler(offset) },
                                onDrag = { _, dragAmount -> overviewJumpOffsetHandler(cursorOffset + dragAmount) },
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

//                                thumbnailWidth = getThumbnailWidth()
                                    layout(placeable1.width, placeable1.height) {
                                        placeable1.placeRelative(0, 0)
                                    }
                                }
                                .background(
                                    if (navigator.diffSequence[item] == AlignmentElement.PERFECT) {
                                        Color.Black
                                    } else if (navigator.diffSequence[item] == AlignmentElement.INSERTION) {
                                        Color.Green
                                    } else {
                                        Color.Blue
                                    },
                                ),
                    ) {
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                PositionIndicator(overviewIndicatorOffset.value)
            }
        }

        // #### timeline labeling ####
        TimelineTopLabels(
            scrollState,
            thumbnailWidth,
            componentWidth,
            modifier = generalModifier.fillMaxHeight(0.15f),
        )

        // #### timeline box ####
        Box(
            modifier =
                generalModifier
                    .fillMaxHeight(0.75f)
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
                thumbnailCache = thumbnailCache,
                nFrames = totalDiffFrames,
                scrollState = scrollState,
            )
            if (indicatorOffset > 0 && indicatorOffset < componentWidth) {
                PositionIndicator(indicatorOffset)
            }
        }

        HorizontalScrollbar(
            modifier =
                generalModifier
                    .fillMaxHeight(0.1f)
                    .padding(top = 5.dp)
                    .border(width = 1.dp, color = Color.LightGray, shape = CircleShape),
            adapter = rememberScrollbarAdapter(scrollState = scrollState),
            style =
                LocalScrollbarStyle.current.copy(
                    hoverDurationMillis = 500,
                    unhoverColor = Color.LightGray,
                    hoverColor = Color.White,
                    shape = CircleShape,
                ),
        )
    }
}

/**
 * Calculates the offset of the thumbnail center from the left side of the timeline.
 *
 * @param scrollState [LazyListState] containing the scroll state of the timeline.
 * @param thumbnailIndex [Int] containing the index of the thumbnail in the diff sequence.
 * @param thumbnailWidth [Float] containing the width of all thumbnails.
 * @return [Float] containing the offset of the thumbnail center from the left side of the timeline.
 */
fun getCenteredThumbnailOffset(
    scrollState: LazyListState,
    thumbnailIndex: Int,
    thumbnailWidth: Float,
): Float {
    return (
        (thumbnailIndex - scrollState.firstVisibleItemIndex + 0.5f) *
            thumbnailWidth - scrollState.firstVisibleItemScrollOffset
    )
}

/**
 * A [LazyRow] that displays the thumbnails of the aligned input videos.
 *
 * Thumbnails are only loaded when they are visible.
 *
 * @param modifier [Modifier] to apply to the [LazyRow].
 * @param navigator [FrameNavigation] object that contains the navigation logic.
 * @param nFrames [Int] The number of frames in the diff sequence.
 * @param scrollState [LazyListState] containing the scroll state of the timeline.
 * @return [Unit]
 */
@Composable
private fun TimelineThumbnails(
    modifier: Modifier,
    thumbnailCache: ThumbnailCache,
    nFrames: Int,
    scrollState: LazyListState,
) {
    LazyRow(
        state = scrollState,
        modifier = modifier.fillMaxSize(),
    ) {
        items(nFrames) { i ->
            val images = thumbnailCache.get(i)
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
 * A vertical line that indicates the current position in the timeline.
 *
 * @param offset [Float] The offset of the line from the left side of the timeline.
 * @return [Unit]
 */
@Composable
private fun PositionIndicator(offset: Float) {
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
 * Labels that are drawn above the center points of timeline thumbnails.
 *
 * @param scrollState [LazyListState] containing the scroll state of the timeline.
 * @param thumbnailWidth [Float] containing the width of all thumbnails.
 * @param componentWidth [Float] containing the width of the timeline component.
 * @param modifier [Modifier] to apply to the element.
 * @return [Unit]
 */
@Composable
private fun TimelineTopLabels(
    scrollState: LazyListState,
    thumbnailWidth: Float,
    componentWidth: Float,
    modifier: Modifier,
) {
    var textWidth by remember { mutableStateOf(0f) }
    var textHeight by remember { mutableStateOf(0f) }

    // Labels Container
    Box(modifier = modifier) {
        val lastVisibleIndex = scrollState.firstVisibleItemIndex + scrollState.layoutInfo.visibleItemsInfo.size - 1

        // thumbnail labels with ticks over thumbnail centers
        for (i in scrollState.firstVisibleItemIndex..lastVisibleIndex) {
            val offset = getCenteredThumbnailOffset(scrollState, i, thumbnailWidth)

            // don't draw a label if thumbnail center outside of timeline
            if (offset < 0 || offset > componentWidth) {
                continue
            }

            // somehow, we need to manually convert the offset for text only dependening on the
            // density of the device (e.g. on Windows: window scaling)
            val textOffset = with(LocalDensity.current) { (offset - textWidth / 2).toDp() }

            // draw label with diff index
            AutoSizeText(
                text = i.toString(),
                color = Color.Black,
                modifier =
                    Modifier
                        .onGloballyPositioned { coordinates ->
                            textWidth = coordinates.size.width.toFloat()
                            textHeight = coordinates.size.height.toFloat()
                        }
                        .offset(x = textOffset)
                        .align(Alignment.TopStart),
            )

            // draw a tick for the text
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    start = Offset(offset, textHeight),
                    end = Offset(offset, size.height),
                    color = Color.Black,
                    strokeWidth = 1f,
                )
            }
        }
    }
}
