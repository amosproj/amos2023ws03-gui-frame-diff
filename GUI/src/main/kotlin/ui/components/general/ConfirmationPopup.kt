// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: simonsasse <simonsasse97@gmail.com>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
package ui.components.general

import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConfirmationPopup(
    text: String,
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onCancel() },
            title = {
                Text(
                    text = "Confirmation",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                )
            },
            text = {
                Text(
                    text,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                )
            },
            backgroundColor = MaterialTheme.colorScheme.secondary,
            confirmButton = {
                TextButton(
                    onClick = { onConfirm() },
                    colors =
                        ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                ) {
                    Text(
                        "Yes",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onCancel() },
                    colors =
                        ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                ) {
                    Text(
                        "No",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            },
        )
    }
}
