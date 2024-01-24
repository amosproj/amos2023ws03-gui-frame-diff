package ui.components.general

import androidx.compose.material.*
import androidx.compose.runtime.Composable

/**
 * A Composable function that creates a dialog to display an error message.
 *
 * @param onCloseRequest A function that is called when the dialog is closed.
 * @param text The text to be displayed in the dialog.
 * @return [Unit]
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ErrorDialog(
    onCloseRequest: () -> Unit,
    text: String,
) {
    AlertDialog(
        onDismissRequest = { onCloseRequest() },
        title = { Text(text = "Error") },
        text = { Text(text) },
        backgroundColor = MaterialTheme.colors.error,
        confirmButton = {
            TextButton(onClick = { onCloseRequest() }) {
                Text("OK")
            }
        },
    )
}