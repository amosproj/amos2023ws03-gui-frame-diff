package ui.components.diffScreen.timeline

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import ui.components.general.TitleWithInfo

/**
 * A Composable function that displays an interactive timeline containing multiple subcomponents.
 *
 * The Timeline includes:
 * - An overview timeline showing all difference positions at once, indicating alignment elements by color.
 * - A thumbnail preview timeline with index labels, showing a subset of all frames.
 * - A scrollbar to scroll through the thumbnail timeline.
 *
 * @param navigator [FrameNavigation] containing the navigation logic.
 * @return [Unit]
 */
@Composable
fun Timeline(navigator: FrameNavigation) {
    val fillWidth = 0.8f
    val navigatorUpdated by rememberUpdatedState(navigator)

    // scroll state of the timeline
    val scrollState = rememberLazyListState()

    // set the modifier applied to all timeline components
    val generalModifier = Modifier.fillMaxWidth(fillWidth)

    Column(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = generalModifier.padding(0.dp, 0.dp, 0.dp, 4.dp),
        ) {
            Box {
                val statisticalInformation =
                    "Total Frames Reference Video: ${navigator.getSizeOfVideoReference()}\n" +
                        "Total Frames Current Video: ${navigator.getSizeOfVideoCurrent()}\n" +
                        "Frames with Differences: ${navigator.getFramesWithPixelDifferences()}\n" +
                        "Inserted Frames: ${navigator.getInsertions()}\n" +
                        "Deleted Frames: ${navigator.getDeletions()}"
                TitleWithInfo(
                    text = "Statistical Information",
                    tooltipText = statisticalInformation,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    topSpace = 4.dp,
                )
            }
        }

        OverviewBar(navigatorUpdated, modifier = generalModifier.weight(0.3f))

        LabeledThumbnailPreview(
            navigatorUpdated,
            scrollState,
            modifier = generalModifier.weight(0.8f),
        )

        HorizontalScrollbar(
            modifier =
                generalModifier
                    .fillMaxHeight(0.1f)
                    .padding(top = 5.dp)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.large),
            adapter = rememberScrollbarAdapter(scrollState = scrollState),
            style =
                LocalScrollbarStyle.current.copy(
                    hoverDurationMillis = 500,
                    unhoverColor = MaterialTheme.colorScheme.secondary,
                    hoverColor = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.large,
                ),
        )
    }
}
