package ui.components.selectVideoScreen

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.*
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
import ui.components.general.ConfirmationPopup
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
fun RowScope.ComputeDifferencesButton(state: MutableState<AppState>) {
    val scope = rememberCoroutineScope()
    val showConfirmDialog = remember { mutableStateOf(false) }
    ConfirmationPopup(
        text = "The reference video is newer than the current video. Are you sure you want to continue?",
        showDialog = showConfirmDialog.value,
        onConfirm = {
            calculateVideoDifferences(scope, state)
            showConfirmDialog.value = false
        },
        onCancel = {
            showConfirmDialog.value = false
        },
    )
    Button(
        // fills all available space
        modifier = Modifier.weight(0.9f).padding(8.dp).fillMaxSize(1f),
        onClick = {
            if (referenceIsOlderThanCurrent(state)) {
                calculateVideoDifferences(scope, state)
            } else {
                showConfirmDialog.value = true
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
    }
}

private fun calculateVideoDifferences(
    scope: CoroutineScope,
    state: MutableState<AppState>,
) {
    scope.launch(Dispatchers.IO) {
        // generate the differences
        val generator = DifferenceGeneratorWrapper(state)
        generator.getDifferences(state.value.outputPath)
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
    var creationData = metadata.getOrDefault("CREATION-TIME", "0").toLong()

    if (creationData != 0L) {
        return creationData
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
