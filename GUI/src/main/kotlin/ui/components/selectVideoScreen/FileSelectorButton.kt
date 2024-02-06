// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: Fabian Seitz <github@seitzfabian.de>
// SPDX-FileCopyrightText: Luis Günther <52202194+zino212@users.noreply.github.com>
// SPDX-FileCopyrightText: simonsasse <simonsasse97@gmail.com>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
package ui.components.selectVideoScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    buttonDescription: String? = null,
    allowedFileExtensions: Array<String>? = null,
) {
    Button(
        modifier = Modifier.weight(1f).fillMaxHeight().padding(8.dp),
        onClick = {
            openFileChooserAndGetPath(
                directoryPath,
                { path -> onUpdateResult(path) },
                allowedFileExtensions,
            )
        },
        shape = MaterialTheme.shapes.medium,
    ) {
        // column to display the button text and the selected file path

        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            // row to display the upload icon
            Row(modifier = Modifier) {
                Image(
                    painter = painterResource("upload.svg"),
                    contentDescription = "Upload",
                    modifier = Modifier.alpha(0.8f).height(150.dp),
                    colorFilter = ColorFilter.tint(LocalContentColor.current),
                )
                if (tooltipText != null) {
                    InfoIconWithHover(tooltipText)
                }
            }

            if (buttonDescription != null) {
                // row to display the button text
                Row(modifier = Modifier) {
                    Text(
                        buttonText,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        textAlign = TextAlign.Center,
                    )
                }
                // row to display the button description
                Row(modifier = Modifier) {
                    Text(
                        buttonDescription,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                // row to display the button text
                Row(modifier = Modifier) {
                    Text(
                        buttonText,
                        fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            // row to display the selected file path
            Row(modifier = Modifier.padding(8.dp)) {
                AutoSizeText(
                    text = buttonPath ?: "No file selected",
                    color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.6f),
                    fixedFontSize = 20,
                )
            }
        }
    }
}
