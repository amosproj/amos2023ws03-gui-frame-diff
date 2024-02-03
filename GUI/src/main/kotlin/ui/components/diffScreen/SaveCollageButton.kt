package ui.components.diffScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import logic.FrameGrabber
import logic.createCollage
import models.AppState
import ui.components.general.openFileSaverAndGetPath

/**
 * Button to save the collage
 *
 * @param frameGrabber The frame grabber to use to get the current frames from
 * @param diffIndex The index of the current diff
 * @param modifier The modifier for the button
 * @param state The state of the app
 * @return [Unit]
 */
@Composable
fun SaveCollageButton(
    frameGrabber: FrameGrabber,
    diffIndex: Int,
    modifier: Modifier = Modifier,
    state: MutableState<AppState>,
) {
    Button(
        modifier = modifier.padding(8.dp),
        onClick = {
            openFileSaverAndGetPath(
                state.value.saveCollagePath,
            ) { path -> saveCollageCallback(frameGrabber, diffIndex, path, state) }
        },
    ) {
        Text(
            text = "Save Collage",
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Bold,
        )
    }
}

/**
 * Callback for when the user selects a file to save.
 * Saves the path and creates the collage.
 * @param frameGrabber The frame grabber to use to get the current frames from
 * @param diffIndex The index of the current diff
 * @param path The path to the file to save to
 * @param state The state of the app
 */
fun saveCollageCallback(
    frameGrabber: FrameGrabber,
    diffIndex: Int,
    path: String,
    state: MutableState<AppState>,
) {
    // check path for suffix
    var savePath = path
    if (!savePath.endsWith(".png")) {
        savePath = "$savePath.png"
    }
    state.value.saveCollagePath = savePath
    createCollage(frameGrabber = frameGrabber, diffIndex = diffIndex, savePath)
}
