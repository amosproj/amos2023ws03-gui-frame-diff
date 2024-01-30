package ui.themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

/**
 * WrapTheming is a wrapper for the defaultTheme.
 * @param content the content to be wrapped
 */
@Composable
fun WrapTheming(content: @Composable () -> Unit) {
    defaultTheme {
        Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
            content()
        }
    }
}
