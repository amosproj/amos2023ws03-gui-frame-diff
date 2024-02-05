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
 * @param xOffset [Float] The offset of the line from the left side of the timeline.
 * @param yStartOffset [Float] The offset of the line from the top side of the timeline.
 * @return [Unit]
 */
@Composable
fun PositionIndicator(
    xOffset: Float,
    yStartOffset: Float = 0f,
) {
    val strokeWidth = 6f
    Canvas(modifier = Modifier.fillMaxSize()) {
        // indicator line
        drawLine(
            start = Offset(xOffset, yStartOffset),
            end = Offset(xOffset, size.height),
            color = Color.Cyan,
            strokeWidth = strokeWidth,
        )

        // outline of the indicator
        drawRect(
            color = Color.Black,
            topLeft = Offset(xOffset - strokeWidth / 2, yStartOffset),
            size = Size(strokeWidth, size.height - yStartOffset),
            style = Stroke(width = 0.5f),
        )
    }
}
