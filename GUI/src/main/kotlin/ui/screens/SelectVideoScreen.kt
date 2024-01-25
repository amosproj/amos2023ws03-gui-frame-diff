package ui.screens

import algorithms.AlgorithmExecutionState
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import models.AppState
import ui.components.general.HelpMenu
import ui.components.general.ProjectMenu
import ui.components.selectVideoScreen.AdvancedSettingsButton
import ui.components.selectVideoScreen.ComputeDifferencesButton
import ui.components.selectVideoScreen.FileSelectorButton

/**
 * A Composable function that creates a screen to select the videos to compare.
 * @param state [MutableState]<[AppState]> containing the global state.
 * @return [Unit]
 */
@Composable
fun SelectVideoScreen(state: MutableState<AppState>) {
    val scope = rememberCoroutineScope()
    val showLoadingDialog = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // menu bar
        TopAppBar(
            backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ProjectMenu(state, Modifier.weight(0.1f))
                Spacer(modifier = Modifier.weight(0.8f))
                HelpMenu(Modifier.weight(0.1f))
            }
        }

        // video selection
        Row(modifier = Modifier.weight(0.85f)) {
            FileSelectorButton(
                buttonText = "Select Reference Video",
                buttonPath = state.value.videoReferencePath,
                onUpdateResult = { selectedFilePath ->
                    state.value = state.value.copy(videoReferencePath = selectedFilePath)
                },
                directoryPath = state.value.videoReferencePath,
            )

            FileSelectorButton(
                buttonText = "Select Current Video",
                buttonPath = state.value.videoCurrentPath,
                onUpdateResult = { selectedFilePath ->
                    state.value = state.value.copy(videoCurrentPath = selectedFilePath)
                },
                directoryPath = state.value.videoCurrentPath,
            )
        }
        // screen switch buttons
        Row(modifier = Modifier.weight(0.15f)) {
            ComputeDifferencesButton(state, scope, showLoadingDialog)
            AdvancedSettingsButton(state)
        }
    }

    if (showLoadingDialog.value) {
        LoadingDialog(onCancel = {
            AlgorithmExecutionState.getInstance().stop()
            showLoadingDialog.value = false
        })
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun LoadingDialog(onCancel: () -> Unit = {}) {
    AlertDialog(
        modifier = Modifier.size(300.dp, 300.dp),
        onDismissRequest = onCancel,
        title = { Text(text = "Computing") },
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
                Button(onClick = onCancel) {
                    Text("Cancel")
                }
            }
        },
    )
}
