package ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import models.AppState

/**
* A Composable function that creates a button with a file selector functionality.
*
* @param buttonText The text to be displayed on the button.
* @param onUpdateResult A function that will be called with the selected file path as a parameter.
 * @param buttonPath The path to the selected file.
 * @return [Unit]
*/
@Composable
fun RowScope.FileSelectorButton(
    buttonText: String,
    buttonPath: String,
    state: MutableState<AppState>,
    onUpdateResult: (String) -> Unit,
) {
    Button(
        modifier = Modifier.weight(1f).padding(8.dp).fillMaxHeight(1f),
        onClick = {
            state.value = state.value.copy(showFilePicker = true, filePickerCallback = onUpdateResult)
        },
    ) {
        // column to display the button text and the selected file path
        Column(modifier = Modifier.fillMaxSize()) {
            // row to display the upload icon
            Row(modifier = Modifier.weight(0.75f)) {
                Image(
                    painter = painterResource("upload.svg"),
                    contentDescription = "Upload",
                    modifier = Modifier.fillMaxSize().alpha(0.8f),
                )
            }
            // row to display the button text
            Row(modifier = Modifier.weight(0.15f)) { AutoSizeText(text = buttonText) }
            // row to display the selected file path
            Row(modifier = Modifier.weight(0.1f)) { AutoSizeText(text = buttonPath, minimalFontSize = 20) }
        }
    }
}
