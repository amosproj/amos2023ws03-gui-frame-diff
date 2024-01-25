package ui.components.diffScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation

@Composable
fun StatisticalInformation(navigator: FrameNavigation) {
    Row(modifier = Modifier.padding(16.dp)) {
        Column {
            Text(
                "Statistical Information:",
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                "Total Frames Reference Video: ${navigator.getSizeOfVideoReference()}\n" +
                    "Total Frames Current Video: ${navigator.getSizeOfVideoCurrent()}\n" +
                    "Frames with Differences: ${navigator.getFramesWithPixelDifferences()}\n" +
                    "Inserted Frames: ${navigator.getInsertions()}\n" +
                    "Deleted Frames: ${navigator.getDeletions()}",
                fontSize = MaterialTheme.typography.labelMedium.fontSize,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.labelMedium.lineHeight,
            )
        }
    }
}
