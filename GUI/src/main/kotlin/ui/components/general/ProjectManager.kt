package ui.components.general

import Screen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.fasterxml.jackson.module.kotlin.readValue
import logic.getVideoMetadata
import models.AppState
import models.JsonMapper
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.FFmpegFrameRecorder
import org.bytedeco.javacv.Frame

/**
 * Dropdown menu to open and save projects
 * @param state the current state of the app
 * @param modifier the modifier to apply to the component
 *
 * @return a dropdown menu to open and save projects
 */
@Composable
fun ProjectMenu(
    state: MutableState<AppState>,
    modifier: Modifier = Modifier,
) {
    var errorDialogText = remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    val padding = 8.dp
    var showConfirmationDialog = remember { mutableStateOf(false) }

    if (errorDialogText.value != null) {
        ErrorDialog(onCloseRequest = { errorDialogText.value = null }, text = errorDialogText.value!!)
    }

    Box {
        Button(
            modifier = modifier.padding(padding),
            onClick = { expanded = !expanded },
        ) {
            Text(
                text = "Project",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Bold,
            )
        }

        DropdownMenu(
            modifier = modifier.padding(padding),
            offset = DpOffset(padding, 0.dp),
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                {
                    Text("Open Project", fontSize = MaterialTheme.typography.bodyMedium.fontSize)
                },
                onClick = {
                    if (state.value.hasUnsavedChanges) {
                        showConfirmationDialog.value = true
                    } else {
                        openFileChooserAndGetPath(
                            directoryPath = state.value.openProjectPath,
                            onResult = { path ->
                                handleOpenProject(state, path, errorDialogText)
                                expanded = false
                                showConfirmationDialog.value = false
                            },
                            allowedFileExtensions = arrayOf("mkv"),
                        )
                    }
                },
            )
            DropdownMenuItem(
                {
                    Text("Save Project", fontSize = MaterialTheme.typography.bodyMedium.fontSize)
                },
                onClick = {
                    openFileSaverAndGetPath(state.value.saveProjectPath) { path -> handleSaveProject(state, path) }
                    expanded = false
                },
                enabled = state.value.screen == Screen.DiffScreen,
            )
        }
    }
    ConfirmationPopup(
        showDialog = showConfirmationDialog.value,
        onCancel = {
            expanded = false
            showConfirmationDialog.value = false
        },
        onConfirm = {
            openFileChooserAndGetPath(
                directoryPath = state.value.openProjectPath,
                onResult = { path ->
                    handleOpenProject(state, path, errorDialogText)
                    expanded = false
                    showConfirmationDialog.value = false
                },
                allowedFileExtensions = arrayOf("mkv"),
            )
        },
        text = "Are you sure to open a project? The existing Difference Video will not be saved.",
    )
}

/**
 * Reads the given json file and sets the state to the parsed AppState
 * @param state the current state of the app
 * @param path the path to the file to open
 */
fun handleOpenProject(
    state: MutableState<AppState>,
    path: String,
    errorText: MutableState<String?>,
) {
    state.value.openProjectPath = path

    // grab metadata
    val metadata = getVideoMetadata(path)

    // if metadata contains APP-STATE, load it
    if (metadata.containsKey("APP-STATE")) {
        state.value = JsonMapper.mapper.readValue<AppState>(metadata["APP-STATE"]!!)
        // in case the video moved, set the output path to the new location
        state.value.outputPath = path
        // reset unsaved changes
        state.value.hasUnsavedChanges = false
    } else {
        errorText.value = "The selected file does not contain a valid project."
    }
}

/**
 * Creates a new diff video containing a serialized app state, which can be loaded in a later session.
 *
 * @param state the current state of the app
 * @param path the path to the file to save
 */
fun handleSaveProject(
    state: MutableState<AppState>,
    path: String,
) {
    var savePath = path
    // add .mkv extension if not present
    if (!savePath.endsWith(".mkv")) {
        savePath = "$savePath.mkv"
    }
    // set save path
    state.value.saveProjectPath = savePath

    val grabber = FFmpegFrameGrabber(state.value.outputPath)
    grabber.start()

    val recorder = FFmpegFrameRecorder(savePath, grabber.imageWidth, grabber.imageHeight)

    // set metadata
    recorder.setMetadata("APP-STATE", JsonMapper.mapper.writeValueAsString(state.value))

    // copy codec and framerate from original diff video
    recorder.frameRate = grabber.frameRate
    recorder.videoCodec = grabber.videoCodec

    recorder.start()
    // clone
    var frame: Frame? = grabber.grabFrame()
    while (frame != null) {
        recorder.record(frame)
        frame = grabber.grabFrame()
    }

    // stop and release
    grabber.stop()
    grabber.release()

    recorder.stop()
    recorder.release()

    // reset unsaved changes
    state.value.hasUnsavedChanges = false
}
