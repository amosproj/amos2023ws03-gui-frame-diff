package ui.components.general

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import models.AppState

/**
 * A Composable function that creates a dialog to check if the user wants to save the changes.
 *
 * @param text the text to display in the dialog
 * @param oldState the old state of the app
 * @param state the current state of the app
 * @param onConfirm the function to execute if the user confirms
 * @param onCancel the function to execute if the user cancels
 * @param showDialog whether to show the dialog or not
 */
@Composable
fun CheckIfSavedDialog(
    text: String,
    oldState: MutableState<AppState>,
    state: MutableState<AppState>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    showDialog: Boolean,
) {
    if (oldState.value != state.value) {
        ConfirmationPopup(
            text = text,
            showDialog = showDialog,
            onConfirm = onConfirm,
            onCancel = onCancel,
        )
    } else {
        onConfirm()
    }
}
