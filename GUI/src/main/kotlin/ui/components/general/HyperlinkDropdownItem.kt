package ui.components.general

import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler

/**
 * A dropdown menu item that opens a hyperlink when clicked.
 * @param text The text to display in the dropdown menu item.
 * @param uri The URI to open when the dropdown menu item is clicked.
 * @return [Unit]
 */
@Composable
fun HyperlinkDropdownMenuItem(
    text: String,
    uri: String,
) {
    val uriHandler = LocalUriHandler.current
    DropdownMenuItem(
        onClick = {
            uriHandler.openUri(uri)
        },
    ) {
        Text(text, fontSize = MaterialTheme.typography.body2.fontSize)
    }
}