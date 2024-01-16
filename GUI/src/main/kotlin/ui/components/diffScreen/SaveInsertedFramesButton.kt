package ui.components.diffScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import models.AppState
import ui.components.general.openFileSaverAndGetPath
import ui.components.general.showOverwriteConfirmation
import java.io.File

/**
 * Button to save all inserted frames as pngs to a zip File Archive.
 *
 * @param navigator The navigator to use to create the archive
 * @param modifier The modifier for the button
 * @return [Unit]
 */
@Composable
fun SaveInsertedFramesButton(
    navigator: FrameNavigation,
    modifier: Modifier,
    state: MutableState<AppState>,
) {
    Button(
        modifier = modifier.padding(8.dp).fillMaxSize(),
        onClick = {
            openFileSaverAndGetPath(
                state.value.saveInsertionsPath,
            ) { path -> saveInsertedFramesCallback(navigator, path, state) }
        },
    ) {
        Text(text = "Export Inserted")
    }
}

/**
 * Callback for when the user selects a file to save.
 * Saves the path and creates the zip archive.
 * @param navigator The navigator to use to create the archive
 * @param path The path to the file to save to
 * @param state The state of the app
 */
fun saveInsertedFramesCallback(
    navigator: FrameNavigation,
    path: String,
    state: MutableState<AppState>,
) {
    // check path for suffix
    var savePath = path
    if (!savePath.endsWith(".zip")) {
        savePath = "$savePath.zip"
    }
    if (File(savePath).exists()) {
        val overwrite = showOverwriteConfirmation()
        if (!overwrite) {
            return
        }
    }
    state.value = state.value.copy(saveInsertionsPath = savePath)
    navigator.createInsertionsExport(savePath)
}
