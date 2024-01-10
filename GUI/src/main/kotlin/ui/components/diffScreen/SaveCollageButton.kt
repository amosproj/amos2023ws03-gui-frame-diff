package ui.components.diffScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import ui.components.general.openSaveChooserAndGetPath

@Composable
fun saveCollageButton(
    navigator: FrameNavigation,
    modifier: Modifier,
) {
    Button(
        modifier = modifier.padding(8.dp).fillMaxSize(),
        onClick = { openSaveChooserAndGetPath()?.let { navigator.createCollage(it) } },
    ) {
        Text(text = "Save Collage")
    }
}
