// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
package ui.components.selectVideoScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingDialog(onCancel: () -> Unit = {}) {
    AlertDialog(
        modifier = Modifier.size(300.dp, 300.dp),
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Computing ...",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(100.dp))
            }
        },
        confirmButton = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                TextButton(
                    onClick = { onCancel() },
                    colors =
                        ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        },
    )
}
