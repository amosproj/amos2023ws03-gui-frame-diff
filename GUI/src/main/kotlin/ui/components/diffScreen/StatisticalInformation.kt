package ui.components.diffScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import frameNavigation.FrameNavigation

@Composable
fun RowScope.StatisticalInformation(navigator: FrameNavigation) {
    Row(modifier = Modifier.weight(0.2f).fillMaxHeight().fillMaxWidth()) {
        Column {
            Text("Statistical Information:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("Total Frames Video1: ${navigator.getSizeOfVideo1()}", fontSize = 12.sp)
            Text("Total Frames Video2: ${navigator.getSizeOfVideo2()}", fontSize = 12.sp)
            Text("Frames with Differences: ${navigator.getFramesWithPixelDifferences()}", fontSize = 12.sp)
            Text("added Frames: ${navigator.getInsertions()}", fontSize = 12.sp)
            Text("deleted Frames: ${navigator.getDeletions()}", fontSize = 12.sp)
        }
    }
}
