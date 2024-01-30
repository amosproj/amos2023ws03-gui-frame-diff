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
import frameNavigation.FrameNavigation
import models.AppState
import ui.components.general.openFileSaverAndGetPath

/**
 * Button to save the collage
 *
 * @param navigator The navigator to use to create the collage
 * @param modifier The modifier for the button
 * @param state The state of the app
 * @return [Unit]
 */
@Composable
fun SaveCollageButton(
    navigator: FrameNavigation,
    modifier: Modifier = Modifier,
    state: MutableState<AppState>,
) {
    Button(
        modifier = modifier.padding(8.dp),
        onClick = { openFileSaverAndGetPath(state.value.saveCollagePath) { path -> saveCollageCallback(navigator, path, state) } },
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
 * @param navigator The navigator to use to create the collage
 * @param path The path to the file to save to
 * @param state The state of the app
 */
fun saveCollageCallback(
    navigator: FrameNavigation,
    path: String,
    state: MutableState<AppState>,
) {
    // check path for suffix
    var savePath = path
    if (!savePath.endsWith(".png")) {
        savePath = "$savePath.png"
    }
    state.value.saveCollagePath = savePath
    navigator.createCollage(savePath)
}
