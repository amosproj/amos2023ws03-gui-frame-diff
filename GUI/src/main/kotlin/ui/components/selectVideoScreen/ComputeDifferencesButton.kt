package ui.components.selectVideoScreen

import DifferenceGeneratorException
import DifferenceGeneratorStoppedException
import algorithms.AlgorithmExecutionState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import logic.createThumbnailVideo
import logic.differenceGeneratorWrapper.DifferenceGeneratorWrapper
import logic.getVideoMetadata
import models.AppState
import ui.components.general.AutoSizeText
import ui.components.general.ConfirmationPopup
import ui.components.general.ErrorDialog
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes

/**
 * A Composable function that creates a button to compute the differences between two videos.
 *
 * @param state [AppState] object containing the state of the application.
 * @return [Unit]
 */
@Composable
fun RowScope.ComputeDifferencesButton(
    state: MutableState<AppState>,
    scope: CoroutineScope,
    showDialog: MutableState<Boolean>,
) {
    val showConfirmDialog = remember { mutableStateOf(false) }
    val errorDialogText = remember { mutableStateOf<String?>(null) }

    Button(
        // fills all available space
        modifier = Modifier.weight(0.9f).padding(8.dp).fillMaxSize(1f),
        onClick = {
            try {
                if (referenceIsOlderThanCurrent(state)) {
                    scope.launch {
                        runComputation(scope, state, errorDialogText, showDialog)
                    }
                } else {
                    showConfirmDialog.value = true
                }
            } catch (e: Exception) {
                errorDialogText.value = "An unexpected exception was thrown when checking " +
                    "video creation timestamps:\n\n${e.message}"
            }
        },
        // enable the button only if all the paths are not empty
        enabled = (
            state.value.videoReferencePath != null &&
                state.value.videoCurrentPath != null &&
                state.value.outputPath != null
        ),
    ) {
        AutoSizeText(
            text = "Compute and Display Differences",
            textAlign = TextAlign.Center,
            // remove default centering
            modifier = Modifier,
            minimalFontSize = 27,
        )

        if (errorDialogText.value != null) {
            ErrorDialog(onCloseRequest = { errorDialogText.value = null }, text = errorDialogText.value!!)
        }
    }

    ConfirmationPopup(
        text = "The reference video is newer than the current video. Are you sure you want to continue?",
        showDialog = showConfirmDialog.value,
        onConfirm = {
            scope.launch {
                runComputation(scope, state, errorDialogText, showDialog)
            }
            showConfirmDialog.value = false
        },
        onCancel = {
            showConfirmDialog.value = false
        },
    )
}

suspend fun runComputation(
    scope: CoroutineScope,
    state: MutableState<AppState>,
    errorDialogText: MutableState<String?>,
    isLoading: MutableState<Boolean>,
) {
    isLoading.value = true
    val computeJob =
        scope.launch(Dispatchers.Default) {
            calculateVideoDifferences(state, errorDialogText)
        }

    computeJob.invokeOnCompletion { isLoading.value = false }

    val videoScaleJob =
        scope.launch(Dispatchers.Default) {
            createThumbnailVideos(state)
        }

    // wait for both jobs to finish before transitioning to the diff screen
    listOf(computeJob, videoScaleJob).joinAll()

    state.value = state.value.copy(screen = Screen.DiffScreen, hasUnsavedChanges = true)
}

fun createThumbnailVideos(state: MutableState<AppState>) {
    // create the thumbnail videos
    val tempReference = createThumbnailVideo(state.value.videoReferencePath!!, 0.25f)
    val tempCurrent = createThumbnailVideo(state.value.videoCurrentPath!!, 0.25f)

    state.value = state.value.copy(thumbnailVideoPathReference = tempReference, thumbnailVideoPathCurrent = tempCurrent)
}

private fun calculateVideoDifferences(
    state: MutableState<AppState>,
    errorDialogText: MutableState<String?>,
) {
    AlgorithmExecutionState.getInstance().reset()

    // generate the differences
    lateinit var generator: DifferenceGeneratorWrapper
    try {
        generator = DifferenceGeneratorWrapper(state)
    } catch (e: DifferenceGeneratorException) {
        errorDialogText.value = e.toString()
        return
    } catch (e: Exception) {
        errorDialogText.value = "An unexpected exception was thrown when creating" +
            "the DifferenceGenerator instance:\n\n${e.message}"
        return
    }

    try {
        generator.getDifferences(state.value.outputPath!!)
    } catch (e: DifferenceGeneratorStoppedException) {
        println("stopped by canceling...")
        return
    } catch (e: Exception) {
        errorDialogText.value = "An unexpected exception was thrown when running" +
            "the difference computation:\n\n${e.message}"
        return
    }

    // check for cancellation one last time before switching to the diff screen
    if (!AlgorithmExecutionState.getInstance().isAlive()) {
        return
    }

    // set the sequence
    state.value = state.value.copy(sequenceObj = generator.getSequence())
}

fun getVideoCreationDate(videoPath: String): Long {
    // attempt to get metadata
    val metadata = getVideoMetadata(videoPath)

    // Expect creation_time (ffmpeg standard) to be in ISO 8601 datetime format
    if (metadata.containsKey("creation_time")) {
        return java.time.Instant.parse(metadata["creation_time"]!!).toEpochMilli()
    }

    // if metadata is not available, use file creation date
    val path: Path = Paths.get(videoPath)
    val attributes = Files.readAttributes(path, BasicFileAttributes::class.java)
    return attributes.creationTime().toMillis()
}

fun referenceIsOlderThanCurrent(state: MutableState<AppState>): Boolean {
    val creationDate1 = getVideoCreationDate(state.value.videoReferencePath!!)
    val creationDate2 = getVideoCreationDate(state.value.videoCurrentPath!!)
    return creationDate1 < creationDate2
}
