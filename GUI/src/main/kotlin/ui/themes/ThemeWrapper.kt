package ui.themes

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable

/**
 * WrapTheming is a wrapper for the defaultTheme.
 * @param content the content to be wrapped
 */
@Composable
fun WrapTheming(content: @Composable () -> Unit) {
    defaultTheme {
        Surface(color = defaultBackgroundColor) {
            content()
        }
    }
}
