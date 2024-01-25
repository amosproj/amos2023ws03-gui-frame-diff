package ui.components.selectVideoScreen

import DifferenceGeneratorException
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logic.differenceGeneratorWrapper.DifferenceGeneratorWrapper
import models.AppState
import org.bytedeco.javacv.FFmpegFrameGrabber
import ui.components.general.AutoSizeText
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
    isLoading: MutableState<Boolean>,
) {
    val showConfirmDialog = remember { mutableStateOf(false) }
    val errorDialogText = remember { mutableStateOf<String?>(null) }

    Button(
        // fills all available space
        modifier = Modifier.weight(0.9f).padding(8.dp).fillMaxSize(1f),
        onClick = {
            try {
                if (referenceIsOlderThanCurrent(state)) {
                    calculateVideoDifferences(scope, state, errorDialogText, isLoading)
                } else {
                    showConfirmDialog.value = true
                }
            } catch (e: Exception) {
                errorDialogText.value = "An unexpected exception was thrown when checking" +
                    "video creation timestamps:\n\n${e.message}"
            }
        },
        // enable the button only if all the paths are not empty
        enabled = (
            state.value.videoReferencePath.isNotEmpty() &&
                state.value.videoCurrentPath.isNotEmpty() &&
                state.value.outputPath.isNotEmpty()
        ),
    ) {
        AutoSizeText(
            text = "Compute and Display Differences",
            textAlign = TextAlign.Center,
            // remove default centering
            modifier = Modifier,
        )

        if (errorDialogText.value != null) {
            ErrorDialog(onCloseRequest = { errorDialogText.value = null }, text = errorDialogText.value!!)
        }
    }
}

private fun calculateVideoDifferences(
    scope: CoroutineScope,
    state: MutableState<AppState>,
    errorDialogText: MutableState<String?>,
    isLoading: MutableState<Boolean>,
) {
    scope.launch(Dispatchers.IO) {
        isLoading.value = true

        // generate the differences
        lateinit var generator: DifferenceGeneratorWrapper
        try {
            generator = DifferenceGeneratorWrapper(state)
        } catch (e: DifferenceGeneratorException) {
            errorDialogText.value = e.message
            return@launch
        } catch (e: Exception) {
            errorDialogText.value = "An unexpected exception was thrown when creating" +
                "the DifferenceGenerator instance:\n\n${e.message}"
            return@launch
        }

        try {
            generator.getDifferences(state.value.outputPath)
        } catch (e: Exception) {
            errorDialogText.value = "An unexpected exception was thrown when running" +
                "the difference computation:\n\n${e.message}"
            return@launch
        }

        isLoading.value = false

        // set the sequence and screen
        state.value = state.value.copy(sequenceObj = generator.getSequence(), screen = Screen.DiffScreen)
    }
}

fun getVideoCreationDate(videoPath: String): Long {
    // attempt to get metadata
    val grabber = FFmpegFrameGrabber(videoPath)
    grabber.start()
    val metadata = grabber.metadata
    grabber.stop()
    grabber.release()

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
    val creationDate1 = getVideoCreationDate(state.value.videoReferencePath)
    val creationDate2 = getVideoCreationDate(state.value.videoCurrentPath)
    return creationDate1 < creationDate2
}
