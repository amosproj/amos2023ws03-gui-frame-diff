package ui.components.diffScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import frameNavigation.FrameNavigation

@Composable
fun RowScope.StatisticalInformation(navigator: FrameNavigation) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val textSize = MaterialTheme.typography.bodySmall.fontSize
    Row(modifier = Modifier.weight(0.2f).fillMaxHeight().fillMaxWidth()) {
        Column {
            Text("Statistical Information:", fontSize = textSize, fontWeight = FontWeight.Bold, color = textColor)
            Text("Total Frames Reference Video: ${navigator.getSizeOfVideoReference()}", fontSize = textSize, color = textColor)
            Text("Total Frames Current Video: ${navigator.getSizeOfVideoCurrent()}", fontSize = textSize, color = textColor)
            Text("Frames with Differences: ${navigator.getFramesWithPixelDifferences()}", fontSize = textSize, color = textColor)
            Text("Inserted Frames: ${navigator.getInsertions()}", fontSize = textSize, color = textColor)
            Text("Deleted Frames: ${navigator.getDeletions()}", fontSize = textSize, color = textColor)
        }
    }
}
