package ui.components.general

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

/**
 * A Composable function that creates text for a tooltip with default styling.
 *
 * @param text [String] containing the text to be displayed.
 */
@Composable
fun TooltipText(
    text: String,
    modifier: Modifier = Modifier.padding(8.dp),
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
        lineHeight = MaterialTheme.typography.labelLarge.lineHeight,
    )
}

/**
 * A tooltip that appears when the user hovers over a component.
 *
 * @param content The content to display in the tooltip.
 * @return A [Popup] composable that displays the tooltip.
 */
@Composable
fun Tooltip(content: @Composable () -> Unit) {
    Popup(
        alignment = Alignment.BottomEnd,
        offset = IntOffset(-28, 0),
    ) {
        // Draw a rectangle shape with rounded corners inside the popup
        Box(
            Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.medium),
        ) {
            content()
        }
    }
}
