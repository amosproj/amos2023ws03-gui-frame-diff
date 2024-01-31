package ui.screens

import AcceptedCodecs
import algorithms.AlgorithmExecutionState
import androidx.compose.foundation.layout.*
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
@Composable
fun SelectVideoScreen(state: MutableState<AppState>) {
    val scope = rememberCoroutineScope()
    val showLoadingDialog = remember { mutableStateOf(false) }

    val referenceErrorDialogText = remember { mutableStateOf<String?>(null) }
    val currentErrorDialogText = remember { mutableStateOf<String?>(null) }

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
            showLoadingDialog.value = false
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
    if (!(grabber.videoMetadata["encoder"] in AcceptedCodecs.ACTIVE_CODECS || grabber.videoCodecName in AcceptedCodecs.ACTIVE_CODECS)) {
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
