package ui.components.general

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InfoIconWithHover(text: String) {
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
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onBackground.copy(
                        alpha = LocalContentAlpha.current,
                    )
                },
            modifier = Modifier.size(24.dp),
        )

        // Use Popup to show the tooltip
        if (isHovered) {
            Tooltip(text = text)
        }
    }
}
