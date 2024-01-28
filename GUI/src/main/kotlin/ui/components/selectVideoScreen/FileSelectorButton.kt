package ui.components.selectVideoScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
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
 * @param buttonPath The path to the selected file.
 * @param onUpdateResult A function that will be called with the selected file path as a parameter.
 * Should update the AppState with the selected file path for the chosen file(e.g.
 * VideoReferencePath).
 * @param tooltipText The text to be displayed in the tooltip.
 * @param directoryPath The path to the directory to be opened in the file chooser.
 * @return [Unit]
 */
@Composable
fun RowScope.FileSelectorButton(
    buttonText: String,
    buttonPath: String?,
    onUpdateResult: (String) -> Unit,
    tooltipText: String? = null,
    directoryPath: String? = null,
) {
    val scope = rememberCoroutineScope()
    Button(
        modifier = Modifier.weight(1f).padding(8.dp).fillMaxHeight(1f),
        onClick = {
            scope.launch(Dispatchers.IO) {
                openFileChooserAndGetPath(directoryPath) { path -> onUpdateResult(path) }
            }
        },
        shape = MaterialTheme.shapes.medium,
    ) {
        // column to display the button text and the selected file path
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // row to display the upload icon
            Row(modifier = Modifier.weight(0.75f)) {
                Image(
                    painter = painterResource("upload.svg"),
                    contentDescription = "Upload",
                    modifier = Modifier.alpha(0.8f),
                    colorFilter = ColorFilter.tint(LocalContentColor.current),
                )
                if (tooltipText != null) {
                    InfoIconWithHover(tooltipText)
                }
            }
            // row to display the button text
            Row(modifier = Modifier.weight(0.15f)) {
                AutoSizeText(text = buttonText, minimalFontSize = 27)
            }
            // row to display the selected file path
            Row(modifier = Modifier.weight(0.1f)) {
                AutoSizeText(text = buttonPath ?: "No file selected", minimalFontSize = 25)
            }
        }
    }
}
