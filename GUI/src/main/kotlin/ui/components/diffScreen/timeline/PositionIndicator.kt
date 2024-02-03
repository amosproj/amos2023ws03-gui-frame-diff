package ui.components.diffScreen.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * A vertical line with different colored outline that indicates the current position in the timeline.
 *
 * @param offset [Float] The offset of the line from the left side of the timeline.
 * @return [Unit]
 */
@Composable
fun PositionIndicator(offset: Float) {
    val strokeWidth = 6f
    Canvas(modifier = Modifier.fillMaxSize()) {
        // indicator line
        drawLine(
            start = Offset(offset, 0f),
            end = Offset(offset, size.height),
            color = Color.Cyan,
            strokeWidth = strokeWidth,
        )

        // outline of the indicator
        drawRect(
            color = Color.Black,
            topLeft = Offset(offset - strokeWidth / 2, 0f),
            size = Size(strokeWidth, size.height),
            style = Stroke(width = 0.5f),
        )
    }
}
