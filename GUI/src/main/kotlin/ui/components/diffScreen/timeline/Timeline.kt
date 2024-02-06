package ui.components.diffScreen.timeline

import StatisticalInformation
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import logic.DiffSequenceInfo
import logic.FrameGrabber
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
fun Timeline(
    navigator: FrameNavigation,
    frameGrabber: FrameGrabber,
) {
    val fillWidth = 0.8f
    val navigatorUpdated by rememberUpdatedState(navigator)

    // scroll state of the timeline
    val scrollState = rememberLazyListState()

    // set the modifier applied to all timeline components
    val generalModifier = Modifier.fillMaxWidth(fillWidth)

    val diffSequenceInfo = DiffSequenceInfo(navigator.diffSequence)

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
            Box(modifier = Modifier.weight(0.2f)) {
                TitleWithInfo(
                    text = "Statistical Information",
                    tooltipContent = { StatisticalInformation(diffSequenceInfo) },
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    topSpace = 0.dp,
                )
            }
        }

        OverviewBar(navigatorUpdated, modifier = generalModifier.weight(0.3f))

        ThumbnailBar(
            navigator = navigatorUpdated,
            frameGrabber = frameGrabber,
            scrollState = scrollState,
            modifier = generalModifier.weight(0.9f).fillMaxSize(),
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
