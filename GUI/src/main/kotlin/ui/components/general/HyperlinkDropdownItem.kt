package ui.components.general

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
        {
            Text(text, fontSize = MaterialTheme.typography.bodyMedium.fontSize)
        },
        onClick = {
            uriHandler.openUri(uri)
        },
    )
}
