package ui.components.diffScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import ui.components.general.openFileSaverAndGetPath

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
) {
    Button(
        modifier = modifier.padding(8.dp).fillMaxSize(),
        onClick = { openFileSaverAndGetPath { path -> navigator.createInsertionsExport(path) } },
    ) {
        Text(text = "Export Inserted")
    }
}
