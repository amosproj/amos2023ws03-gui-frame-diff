// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: AlperK61 <92909013+AlperK61@users.noreply.github.com>
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: Fabian Seitz <github@seitzfabian.de>
// SPDX-FileCopyrightText: Luis Günther <52202194+zino212@users.noreply.github.com>
// SPDX-FileCopyrightText: simonsasse <59832939+simonsasse@users.noreply.github.com>
// SPDX-FileCopyrightText: simonsasse <simonsasse97@gmail.com>
// SPDX-FileCopyrightText: Simon Sasse <simonsasse97@gmail.com>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
package ui.screens

import AcceptedCodecs
import algorithms.AlgorithmExecutionState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import models.AppState
import ui.components.general.ErrorDialog
import ui.components.general.HelpMenu
import ui.components.general.ProjectMenu
import ui.components.selectVideoScreen.AdvancedSettingsButton
import ui.components.selectVideoScreen.ComputeDifferencesButton
import ui.components.selectVideoScreen.FileSelectorButton
import ui.components.selectVideoScreen.LoadingDialog

/**
 * A Composable function that creates a screen to select the videos to compare.
 * @param state [MutableState]<[AppState]> containing the global state.
 * @return [Unit]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectVideoScreen(state: MutableState<AppState>) {
    val scope = rememberCoroutineScope()
    val showLoadingDialog = remember { mutableStateOf(false) }

    val errorDialogText = remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // menu bar
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Select Video Screen",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                )
            },
            navigationIcon = {
                ProjectMenu(state)
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
            actions = {
                HelpMenu()
            },
        )
        // video selection
        Row(modifier = Modifier.weight(0.85f)) {
            FileSelectorButton(
                buttonText = "Select Reference Video",
                buttonPath = state.value.videoReferencePath,
                onUpdateResult = { selectedFilePath ->
                    checkVideoFormatAndCodec(
                        selectedFilePath,
                        state,
                        errorDialogText,
                        true,
                    )
                },
                directoryPath = state.value.videoReferencePath ?: state.value.videoCurrentPath,
                buttonDescription = "Please upload a video with format mkv or mov.",
                allowedFileExtensions = arrayOf("mkv", "mov"),
            )
            if (errorDialogText.value != null) {
                ErrorDialog(
                    onCloseRequest = { errorDialogText.value = null },
                    text = errorDialogText.value!!,
                )
            }
            FileSelectorButton(
                buttonText = "Select Current Video",
                buttonPath = state.value.videoCurrentPath,
                onUpdateResult = { selectedFilePath ->
                    checkVideoFormatAndCodec(
                        selectedFilePath,
                        state,
                        errorDialogText,
                        false,
                    )
                },
                directoryPath = state.value.videoCurrentPath ?: state.value.videoReferencePath,
                buttonDescription = "Please upload a video with format mkv or mov.",
                allowedFileExtensions = arrayOf("mkv", "mov"),
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
        })
    }
}

/**
 * Checks if the selected file is in the correct format and codec.
 */
private fun checkVideoFormatAndCodec(
    selectedFilePath: String,
    state: MutableState<AppState>,
    errorDialogText: MutableState<String?>,
    isReference: Boolean,
) {
    if (!selectedFilePath.endsWith(".mkv") && !selectedFilePath.endsWith(".mov")) {
        errorDialogText.value =
            "Uploaded Video is not in the correct format. Please upload a video with format mkv or mov."
        return
    }
    if (!AcceptedCodecs.checkFile(selectedFilePath)) {
        errorDialogText.value =
            "Uploaded Video is not in the correct codec. Please upload a video encoded with ffv1."
        return
    }
    if (isReference) {
        state.value = state.value.copy(videoReferencePath = selectedFilePath)
    } else {
        state.value = state.value.copy(videoCurrentPath = selectedFilePath)
    }
}
