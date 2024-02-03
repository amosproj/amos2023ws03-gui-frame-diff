package ui.components.diffScreen.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logic.FrameGrabber
import logic.caches.ThumbnailCache
import ui.components.general.AutoSizeText

/**
 * A vertical line that indicates the current position in the timeline.
 *
 * @param navigator [FrameNavigation] containing the logic to jump to a specific frame.
 * @param scrollState [LazyListState] containing the scroll state of the timeline.
 * @param modifier [Modifier] to apply to the element.
 */
@Composable
fun LabeledThumbnailPreview(
    frameGrabber: FrameGrabber,
    navigator: FrameNavigation,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    // display width of a single thumbnail in the timeline (2 thumbnails are stacked)
    val thumbnailWidth = remember { mutableStateOf(0.0f) }

    // set the width of the timeline box
    val componentWidth = remember { mutableStateOf(0.0f) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                // calculate the width of the timeline-box
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    // Store the width
                    componentWidth.value = placeable.width.toFloat()

                    layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
                },
    ) {
        // #### timeline labeling ####
        ThumbnailLabels(
            scrollState,
            thumbnailWidth.value,
            componentWidth.value,
            modifier = Modifier.weight(1f),
        )

        // #### timeline box ####
        ThumbnailBar(
            navigator,
            frameGrabber,
            thumbnailWidth,
            componentWidth.value,
            scrollState,
            modifier = Modifier.weight(4f),
        )
    }
}

/**
 * A [LazyRow] that displays the thumbnails of the aligned input videos.
 *
 * Thumbnails are only loaded when they are visible.
 *
 * @param modifier [Modifier] to apply to the [LazyRow].
 * @param thumbnailCache [ThumbnailCache] that contains logic to grab and cache thumbnails.
 * @param nFrames [Int] The number of frames in the diff sequence.
 * @param scrollState [LazyListState] containing the scroll state of the timeline.
 * @return [Unit]
 */
@Composable
private fun ThumbnailRow(
    modifier: Modifier,
    thumbnailCache: ThumbnailCache,
    nFrames: Int,
    scrollState: LazyListState,
    thumbnailSize: Size,
) {
    LazyRow(
        state = scrollState,
        modifier = modifier.fillMaxSize(),
    ) {
        items(nFrames) { i ->
            AsyncDiffColumn(thumbnailCache, i, thumbnailSize)
        }
    }
}

@Composable
fun AsyncDiffColumn(
    thumbnailCache: ThumbnailCache,
    index: Int,
    placeholderSize: Size,
) {
    val images = remember { mutableStateOf<List<ImageBitmap>?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(thumbnailCache, index) {
        scope.launch(Dispatchers.IO) {
            println("thumbnail $index")
            images.value = thumbnailCache.get(index)
        }
    }

    val modifier = Modifier.border(0.5.dp, Color.Black)

    val width = with(LocalDensity.current) { placeholderSize.width.toDp() }
    val height = with(LocalDensity.current) { placeholderSize.height.toDp() }

    Column {
        Box(modifier = modifier.weight(0.5f).background(Color.Gray).size(width, height)) {
            if (images.value != null) {
                Image(
                    bitmap = images.value!![0],
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight(),
                )
            }
        }

        Box(modifier = modifier.weight(0.5f).background(Color.Gray).size(width, height)) {
            if (images.value != null) {
                Image(
                    bitmap = images.value!![1],
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight(),
                )
            }
        }
    }
}

/**
 * A [Box] containing the thumbnails of the aligned input videos and the indicator to where the current frame is.
 *
 * @param navigator [FrameNavigation] containing the logic to jump to a specific frame.
 * @param thumbnailWidth [MutableState]<[Float]> containing the width of all thumbnails.
 * @param componentWidth [Float] containing the width of the timeline component.
 * @param scrollState [LazyListState] containing the scroll state of the timeline.
 * @param modifier [Modifier] to apply to the element.
 */
@Composable
private fun ThumbnailBar(
    navigator: FrameNavigation,
    frameGrabber: FrameGrabber,
    thumbnailWidth: MutableState<Float>,
    componentWidth: Float,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    var cursorOffset = Offset.Zero
    val height = remember { mutableStateOf(0.0f) }

    // thumbnail cache
    val thumbnailCache = remember { ThumbnailCache(maxCacheSize = 30, frameGrabber::getImagesAtDiff) }

    var indicatorOffset by remember { mutableStateOf(0.0f) }
    indicatorOffset = getCenteredThumbnailOffset(scrollState, navigator.currentDiffIndex.value, thumbnailWidth.value)

    val scope = rememberCoroutineScope()
    val totalDiffFrames = navigator.diffSequence.size

    navigator.setOnNavigateCallback {
        indicatorOffset = getCenteredThumbnailOffset(scrollState, navigator.currentDiffIndex.value, thumbnailWidth.value)

        if (indicatorOffset < 0 || indicatorOffset > componentWidth) {
            scope.launch {
                val thumbnailsInView = (componentWidth / thumbnailWidth.value).toInt()
                scrollState.animateScrollToItem(
                    (navigator.currentDiffIndex.value - thumbnailsInView / 2).coerceIn(
                        0,
                        totalDiffFrames - 1,
                    ),
                )
            }
        }
    }

    fun getThumbnailWidth(): Float {
        return frameGrabber.width.toFloat() / frameGrabber.height * height.value * 0.5f
    }

    fun jumpOffsetHandler(offset: Offset) {
        cursorOffset = offset
        val clickedFrame =
            (
                (offset.x + scrollState.firstVisibleItemScrollOffset) / thumbnailWidth.value
            ).toInt() + scrollState.firstVisibleItemIndex
        navigator.jumpToFrame(clickedFrame)
    }

    Box(
        modifier =
            modifier
                .border(width = 2.dp, color = Color.Black)
                // handle clicks and drags on the timeline
                .pointerInput(Unit) { detectTapGestures { offset -> jumpOffsetHandler(offset) } }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> jumpOffsetHandler(offset) },
                        onDrag = { _, dragAmount -> jumpOffsetHandler(cursorOffset + dragAmount) },
                    )
                }
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    // Store the height
                    height.value = placeable.height.toFloat()

                    thumbnailWidth.value = getThumbnailWidth()
                    layout(placeable.width, placeable.height) { placeable.placeRelative(0, 0) }
                },
    ) {
        // #### clickable timeline ####
        ThumbnailRow(
            modifier = Modifier,
            thumbnailCache = thumbnailCache,
            nFrames = totalDiffFrames,
            scrollState = scrollState,
            thumbnailSize = Size(thumbnailWidth.value, height.value / 2),
        )
        if (indicatorOffset > 0 && indicatorOffset < componentWidth) {
            PositionIndicator(indicatorOffset)
        }
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
private fun ThumbnailLabels(
    scrollState: LazyListState,
    thumbnailWidth: Float,
    componentWidth: Float,
    modifier: Modifier = Modifier,
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

            // somehow, we need to manually convert the offset for text only depending on the
            // density of the device (e.g. on Windows: window scaling)
            val textOffset = with(LocalDensity.current) { (offset - textWidth / 2).toDp() }

            // draw label with diff index
            AutoSizeText(
                text = i.toString(),
                color = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier
                        .onGloballyPositioned { coordinates ->
                            textWidth = coordinates.size.width.toFloat()
                            textHeight = coordinates.size.height.toFloat()
                        }
                        .offset(x = textOffset)
                        .align(Alignment.TopStart),
            )
            val lineColor = MaterialTheme.colorScheme.primary
            // draw a tick for the text
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    start = Offset(offset, textHeight),
                    end = Offset(offset, size.height),
                    color = lineColor,
                    strokeWidth = 1f,
                )
            }
        }
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
private fun getCenteredThumbnailOffset(
    scrollState: LazyListState,
    thumbnailIndex: Int,
    thumbnailWidth: Float,
): Float {
    return (
        (thumbnailIndex - scrollState.firstVisibleItemIndex + 0.5f) *
            thumbnailWidth - scrollState.firstVisibleItemScrollOffset
    )
}
