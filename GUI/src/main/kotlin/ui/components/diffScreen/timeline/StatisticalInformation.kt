import algorithms.AlignmentElement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import ui.components.general.TooltipText
import util.ColorEncoding

/**
 * A Composable function that creates a tooltip hover box with statistical information about the selected videos
 * and their differences.
 *
 * @param navigator [FrameNavigation] containing the navigation logic.
 */
@Composable
fun StatisticalInformation(navigator: FrameNavigation) {
    @Composable
    fun ColoredCircle(alignmentElement: AlignmentElement) {
        Box(
            modifier =
                Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color(ColorEncoding.elementToColor[alignmentElement]!!.rgb)),
        )
    }

    @Composable
    fun RowWithCircle(
        alignmentElement: AlignmentElement,
        text: String,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ColoredCircle(alignmentElement)
            TooltipText(text, modifier = Modifier.padding(start = 6.dp))
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        TooltipText("Total Frames Reference Video: ${navigator.getSizeOfVideoReference()}", modifier = Modifier)
        TooltipText("Total Frames Current Video: ${navigator.getSizeOfVideoCurrent()}", modifier = Modifier)
        RowWithCircle(AlignmentElement.DELETION, "Deleted Frames: ${navigator.getDeletions()}")
        RowWithCircle(AlignmentElement.MATCH, "Frames with Differences: ${navigator.getFramesWithPixelDifferences()}")
        RowWithCircle(AlignmentElement.INSERTION, "Inserted Frames: ${navigator.getInsertions()}")
    }
}
