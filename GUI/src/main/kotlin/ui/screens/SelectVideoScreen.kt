package ui.screens

import algorithms.AlgorithmExecutionState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import models.AppState
import org.bytedeco.javacv.FFmpegFrameGrabber
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

    val referenceErrorDialogText = remember { mutableStateOf<String?>(null) }
    val currentErrorDialogText = remember { mutableStateOf<String?>(null) }

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
                        referenceErrorDialogText,
                        true,
                    )
                },
                directoryPath = state.value.videoReferencePath,
                buttonDescription = "Please upload a video with format mkv or mov.",
                allowedFileExtensions = arrayOf("mkv", "mov"),
            )
            if (referenceErrorDialogText.value != null) {
                ErrorDialog(
                    onCloseRequest = { referenceErrorDialogText.value = null },
                    text = referenceErrorDialogText.value!!,
                )
            }
            FileSelectorButton(
                buttonText = "Select Current Video",
                buttonPath = state.value.videoCurrentPath,
                onUpdateResult = { selectedFilePath ->
                    checkVideoFormatAndCodec(
                        selectedFilePath,
                        state,
                        currentErrorDialogText,
                        false,
                    )
                },
                directoryPath = state.value.videoCurrentPath,
                buttonDescription = "Please upload a video with format mkv or mov.",
                allowedFileExtensions = arrayOf("mkv", "mov"),
            )
            if (currentErrorDialogText.value != null) {
                ErrorDialog(
                    onCloseRequest = { currentErrorDialogText.value = null },
                    text = currentErrorDialogText.value!!,
                )
            }
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
    val grabber = FFmpegFrameGrabber(selectedFilePath)
    grabber.start()
    if (!(grabber.videoMetadata["encoder"] ?: grabber.videoCodecName).lowercase().contains("ffv1")) {
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
