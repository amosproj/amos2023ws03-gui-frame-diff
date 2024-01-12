package ui.components.selectVideoScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.components.general.AutoSizeText
import ui.components.general.InfoIconWithHover
import ui.components.general.openFileChooserAndGetPath

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
    onUpdateResult: (String) -> Unit,
    tooltipText: String? = null,
) {
    val scope = rememberCoroutineScope()
    Button(
        modifier = Modifier.weight(1f).padding(8.dp).fillMaxHeight(1f),
        onClick = { scope.launch(Dispatchers.IO) { openFileChooserAndGetPath { path -> onUpdateResult(path) } } },
    ) {
        // column to display the button text and the selected file path
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            // row to display the upload icon
            Row(modifier = Modifier.weight(0.75f)) {
                Image(
                    painter = painterResource("upload.svg"),
                    contentDescription = "Upload",
                    modifier = Modifier.alpha(0.8f),
                )
                if (tooltipText != null) {
                    InfoIconWithHover(tooltipText)
                }
            }
            // row to display the button text
            Row(modifier = Modifier.weight(0.15f)) { AutoSizeText(text = buttonText) }
            // row to display the selected file path
            Row(modifier = Modifier.weight(0.1f)) { AutoSizeText(text = buttonPath, minimalFontSize = 20) }
        }
    }
}
