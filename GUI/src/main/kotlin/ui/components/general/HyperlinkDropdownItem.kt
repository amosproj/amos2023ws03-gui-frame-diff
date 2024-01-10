package ui.components.general

import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler

@Composable
fun hyperlinkDropdownMenuItem(
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
