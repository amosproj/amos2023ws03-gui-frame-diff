package ui.components.general

import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
            title = { Text(text = "Confirmation") },
            text = { Text(text) },
            confirmButton = {
                TextButton(onClick = { onConfirm() }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { onCancel() }) {
                    Text("No")
                }
            },
        )
    }
}
