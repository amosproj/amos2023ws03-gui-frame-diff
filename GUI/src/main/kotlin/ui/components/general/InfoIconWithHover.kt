package ui.components.general

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp

/**
 * A [Icon] with a tooltip that shows when the mouse hovers over it.
 * @param text The text to show in the tooltip.
 * @return A [Icon] with a tooltip that shows when the mouse hovers over it.
 */
@Composable
fun InfoIconWithHover(text: String) {
    InfoIconWithHover(content = { TooltipText(text) })
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InfoIconWithHover(content: @Composable () -> Unit) {
    var isHovered by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .onPointerEvent(PointerEventType.Enter) { isHovered = true }
                .onPointerEvent(PointerEventType.Exit) { isHovered = false },
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint =
                if (isHovered) {
                    LocalContentColor.current
                } else {
                    if (LocalContentColor.current == MaterialTheme.colorScheme.onBackground) {
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = LocalContentAlpha.current,
                        )
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(
                            alpha = LocalContentAlpha.current,
                        )
                    }
                },
            modifier = Modifier.size(24.dp),
        )

        // Use Popup to show the tooltip
        if (isHovered) {
            Tooltip(content = content)
        }
    }
}
