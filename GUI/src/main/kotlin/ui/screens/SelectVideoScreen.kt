package ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import models.AppState
import ui.components.general.AutoSizeText
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
    var showDialog = mutableStateOf(false)
    var isCancelling = remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
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
                ComputeDifferencesButton(state, scope, showDialog, isCancelling)
                AdvancedSettingsButton(state)
            }
        }
        if (showDialog.value) {
            ShowDialog(isCancelling)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ShowDialog(isCancelling: MutableState<Boolean>) {
    AlertDialog(
        modifier = Modifier.fillMaxSize(0.6f),
        onDismissRequest = { },
        title = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            ) {
                Text(text = "Computing")
            }
        },
        text = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
            ) {
                CircularProgressIndicator(modifier = Modifier.size(150.dp))
            }
        },
        confirmButton = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                TextButton(onClick = { isCancelling.value = true }) {
                    if (isCancelling.value) {
                        CircularProgressIndicator()
                    } else {
                        Text("Cancel")
                    }
                }
            }
        },
    )
}

@Composable
private fun Loading(isCancelling: MutableState<Boolean>) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = Color.Black.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(modifier = Modifier.weight(0.6f)) {
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.2f)
                            .align(Alignment.Center),
                )
            }

            Button(
                modifier = Modifier.weight(0.2f).fillMaxWidth(0.3f),
                onClick = {
                    isCancelling.value = true
                },
            ) {
                if (isCancelling.value) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AutoSizeText("is Cancelling", modifier = Modifier.padding(10.dp))
                        CircularProgressIndicator(color = Color.Black)
                    }
                } else {
                    Column {
                    }
                    Spacer(modifier = Modifier.fillMaxHeight(0.3f))
                    AutoSizeText("X", textAlign = TextAlign.Center)
                }
            }
        }
    }
}
