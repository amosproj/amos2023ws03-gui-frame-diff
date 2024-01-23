package ui.components.general

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * A Composable function that creates a dialog to display an error message.
 *
 * @param onCloseRequest A function that is called when the dialog is closed.
 * @param text The text to be displayed in the dialog.
 * @return [Unit]
 */
@Composable
fun ErrorDialog(
    onCloseRequest: () -> Unit,
    text: String,
) {
    Dialog(onCloseRequest = onCloseRequest, title = "An error occurred") {
        Column(modifier = Modifier.padding(8.dp).fillMaxSize()) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                color = Color.Black,
                // remove default centering
                modifier = Modifier,
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(onClick = { onCloseRequest() }) {
                    Text("OK")
                }
            }
        }
    }
}
