package ui.themes

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable

// sets the default color palette
@Composable
fun wrapTheming(content: @Composable () -> Unit) {
    defaultTheme {
        Surface(color = defaultBackgroundColor) {
            content()
        }
    }
}
