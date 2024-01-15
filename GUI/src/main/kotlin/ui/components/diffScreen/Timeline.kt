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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import ui.components.general.AutoSizeText

/**
 * A class that caches thumbnails for the timeline.
 *
 * @param getImages A function that returns the thumbnails for a given diff index.
 */
class ThumbnailCache(val maxCacheSize: Int, val getImages: (Int) -> List<ImageBitmap>) {
    private val cache = mutableMapOf<Int, List<ImageBitmap>>()

    // queue of diff indices ordered by most recently used
    private val usageQueue = mutableListOf<Int>()

    /**
     * Returns the thumbnails for a given diff index.
     *
     * If the thumbnails are not already cached, they are loaded via the `getImages` function and cached.
     *
     * @param index [Int] containing the index of the diff.
     * @return [List]<[ImageBitmap]> containing the thumbnails for the given diff index.
     */
    fun get(index: Int): List<ImageBitmap> {
        usageQueue.remove(index)

        if (!cache.containsKey(index)) {
            cache[index] = getImages(index)
        }

        // if the queue is full, remove the least recently used item
        if (usageQueue.size >= maxCacheSize) {
            cache.remove(usageQueue.removeAt(0))
        }

        usageQueue.add(index)
        return cache[index]!!
    }
}

/**
 * A Composable function that creates a box to display the timeline.
 * @param navigator [FrameNavigation] containing the navigation logic.
 * @return [Unit]
 */
@Composable
fun Timeline(navigator: FrameNavigation) {
    val navigatorUpdated by rememberUpdatedState(navigator)
    // set the width of the timeline box
    var componentWidth by remember { mutableStateOf(0.0f) }
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

    fun getThumbnailWidth(): Float {
        return navigator.width.toFloat() / navigator.height * componentHeight * 0.5f
    }

    indicatorOffset = getCenteredThumbnailOffset(scrollState, navigatorUpdated.currentDiffIndex.value, thumbnailWidth)

    // set the modifier applied to all timeline components
    val generalModifier = Modifier.fillMaxWidth(0.8f)

    Column(
        modifier = Modifier.background(color = Color.Gray).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
        val lastVisibleIndex = scrollState.firstVisibleItemIndex + scrollState.layoutInfo.visibleItemsInfo.size

        // thumbnail labels with ticks over thumbnail centers
        for (i in scrollState.firstVisibleItemIndex..lastVisibleIndex) {
            val offset = getCenteredThumbnailOffset(scrollState, i, thumbnailWidth)

            // don't draw a label if thumbnail center outside of timeline
            if (offset < 0 || offset > componentWidth) {
                continue
            }

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
                        .offset(x = (offset - textWidth / 2).dp)
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
