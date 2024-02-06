// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: simonsasse <simonsasse97@gmail.com>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
package ui.components.general

import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
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
        title = {
            Text(
                text = "Error",
                color = MaterialTheme.colorScheme.onError,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
            )
        },
        text = {
            Text(
                text,
                color = MaterialTheme.colorScheme.onError,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
        },
        backgroundColor = MaterialTheme.colorScheme.error,
        confirmButton = {
            TextButton(
                onClick = { onCloseRequest() },
                colors =
                    ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
            ) {
                Text(
                    "OK",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    )
}
