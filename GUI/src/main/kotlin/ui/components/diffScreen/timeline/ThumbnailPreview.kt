// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: AlperK61 <92909013+alperk61@users.noreply.github.com>
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logic.FrameGrabber
import logic.caches.ThumbnailCache
import kotlin.math.round

/**
 * A [Box] containing the thumbnails of the aligned input videos and the indicator to where the current frame is.
 *
 * @param navigator [FrameNavigation] containing the logic to jump to a specific frame.
 * @param frameGrabber [FrameGrabber] containing the logic to grab thumbnails from the input videos.
 * @param scrollState [LazyListState] containing the scroll state of the timeline.
 * @param modifier [Modifier] to apply to the element.
 */
@Composable
fun ThumbnailBar(
    navigator: FrameNavigation,
    frameGrabber: FrameGrabber,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
) {
    var cursorOffset = Offset.Zero
    val height = remember { mutableStateOf(0.0f) }
    val width = remember { mutableStateOf(0.0f) }

    // display width of a single thumbnail in the timeline (2 thumbnails are stacked)
    val thumbnailWidth = remember { mutableStateOf(0.0f) }

    // thumbnail cache
    val thumbnailCache = remember { ThumbnailCache(maxCacheSize = 30, frameGrabber::getImagesAtDiff) }

    var indicatorOffset by remember { mutableStateOf(0.0f) }
    indicatorOffset = getCenteredThumbnailOffset(scrollState, navigator.currentDiffIndex.value, round(thumbnailWidth.value))

    val scope = rememberCoroutineScope()
    val totalDiffFrames = navigator.diffSequence.size

    // install a callback in the navigator to scroll to the position in the timeline when the current frame changes
    // that is only triggered if the indicator is out of view
    navigator.setOnNavigateCallback {
        indicatorOffset = getCenteredThumbnailOffset(scrollState, navigator.currentDiffIndex.value, round(thumbnailWidth.value))

        if (indicatorOffset < 0 || indicatorOffset > width.value) {
            scope.launch {
                val thumbnailsInView = (width.value / thumbnailWidth.value).toInt()
                scrollState.animateScrollToItem(
                    (navigator.currentDiffIndex.value - thumbnailsInView / 2).coerceIn(
                        0,
                        totalDiffFrames - 1,
                    ),
                )
            }
        }
    }

    // decides which portion of the vertical space the labels get
    val verticalLabelSpace = 0.3f

    fun getThumbnailWidth(): Float {
        return frameGrabber.width.toFloat() / frameGrabber.height * height.value * (1 - verticalLabelSpace) / 2
    }

    // gathers information about the scroll state and uses the click offset to determine which thumbnail was clicked
    // or pointed to while dragging
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
                // handle clicks and drags on the timeline
                .pointerInput(Unit) { detectTapGestures { offset -> jumpOffsetHandler(offset) } }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> jumpOffsetHandler(offset) },
                        onDrag = { _, dragAmount -> jumpOffsetHandler(cursorOffset + dragAmount) },
                    )
                }.fillMaxSize()
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    // Store the height
                    height.value = placeable.height.toFloat()
                    width.value = placeable.width.toFloat()
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
            thumbnailSize = Size(thumbnailWidth.value, (1 - verticalLabelSpace) / 2 * height.value),
            verticalLabelSpace = verticalLabelSpace,
        )

        if (indicatorOffset > 0 && indicatorOffset < width.value) {
            PositionIndicator(indicatorOffset, height.value * verticalLabelSpace)
        }
    }
}

/**
 * A [LazyRow] that displays the thumbnails of the aligned input videos.
 *
 * Thumbnails are only loaded when they are visible. If a thumbnail is visible by scrolling, the image data is
 * requested from the [ThumbnailCache] and displayed. If the scrolling causes the thumbnail to be out of view, the
 * image request is cancelled, leading to a more efficient use of resources.
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
    verticalLabelSpace: Float,
) {
    LazyRow(
        state = scrollState,
        modifier = modifier.fillMaxSize(),
    ) {
        items(nFrames) { i ->
            AsyncDiffColumn(
                thumbnailCache = thumbnailCache,
                index = i,
                placeholderSize = thumbnailSize,
                verticalLabelSpace = verticalLabelSpace,
            )
        }
    }
}

/**
 * A [Column] that displays the two thumbnails of the aligned input videos at a given index.
 *
 * Thumbnails are only loaded when they are visible.
 *
 * @param thumbnailCache [ThumbnailCache] that contains logic to grab and cache thumbnails.
 * @param index [Int] contains the index of the displayed column.
 * @param placeholderSize [size] contains the size of the gray placeholder.
 * @return [Unit]
 */
@Composable
fun AsyncDiffColumn(
    thumbnailCache: ThumbnailCache,
    index: Int,
    placeholderSize: Size,
    verticalLabelSpace: Float,
) {
    // the two images for the thumbnail boxes
    val images = remember { mutableStateOf<List<ImageBitmap>?>(null) }

    // coroutine scope for the image request
    val scope = rememberCoroutineScope()

    // load thumbnails in an IO coroutine which is cancelled when the composable is disposed (out of view)
    LaunchedEffect(thumbnailCache, index) {
        scope.launch(Dispatchers.IO) {
            images.value = thumbnailCache.get(index)
        }
    }

    val width = with(LocalDensity.current) { placeholderSize.width.toDp() }
    val height = with(LocalDensity.current) { placeholderSize.height.toDp() }

    val boxModifier = Modifier.border(0.5.dp, Color.Black).background(Color.Gray).height(height).fillMaxWidth()

    // the thumbnail boxes share what's left of the vertical space with the fixed label space
    val verticalBoxSpace = (1 - verticalLabelSpace) / 2

    Column(modifier = Modifier.width(width)) {
        ThumbnailLabel(
            index = index,
            width = width.value,
            modifier = Modifier.weight(verticalLabelSpace).padding(top = 5.dp),
        )

        Box(modifier = boxModifier.weight(verticalBoxSpace)) {
            if (images.value != null) {
                Image(
                    bitmap = images.value!![0],
                    contentDescription = "Reference Thumbnail index $index",
                    modifier = Modifier.fillMaxHeight(),
                )
            }
        }

        Box(modifier = boxModifier.weight(verticalBoxSpace)) {
            if (images.value != null) {
                Image(
                    bitmap = images.value!![1],
                    contentDescription = "Current Thumbnail index $index",
                    modifier = Modifier.fillMaxHeight(),
                )
            }
        }
    }
}

/**
 * Labels that are drawn above the center points of timeline thumbnails.
 *
 * @param index [Int] containing the index to be written.
 * @param width [Float] describing the width of the column to fill.
 * @param modifier [Modifier] to apply to the element.
 * @return [Unit]
 */
@Composable
private fun ThumbnailLabel(
    index: Int,
    width: Float,
    modifier: Modifier = Modifier,
) {
    val densityCorrectedWidth = with(LocalDensity.current) { width.toDp() }

    // Labels Container
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val fontSize = MaterialTheme.typography.titleLarge.fontSize
        val lineColor = MaterialTheme.colorScheme.primary

        // draw label with diff index
        Text(
            text = index.toString(),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 6.dp),
            fontSize = fontSize,
        )

        // draw a tick for the text
        Canvas(modifier = Modifier.width(densityCorrectedWidth).fillMaxHeight()) {
            drawLine(
                start = Offset(densityCorrectedWidth.toPx() / 2, 0f),
                end = Offset(densityCorrectedWidth.toPx() / 2, size.height),
                color = lineColor,
                strokeWidth = 3f,
            )
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
