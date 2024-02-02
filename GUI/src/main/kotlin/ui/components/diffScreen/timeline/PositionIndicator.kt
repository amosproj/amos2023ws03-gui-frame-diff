package ui.components.diffScreen.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * A vertical line that indicates the current position in the timeline.
 *
 * @param offset [Float] The offset of the line from the left side of the timeline.
 * @return [Unit]
 */
@Composable
fun PositionIndicator(offset: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawLine(
            start = Offset(offset, 0f),
            end = Offset(offset, size.height),
            color = Color.Red,
            strokeWidth = 6f,
        )
    }
}
