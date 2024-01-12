package ui.components.diffScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.components.general.openSaveChooserAndGetPath

@Composable
fun saveCollageButton(
    navigator: FrameNavigation,
    modifier: Modifier,
) {
    val scope = rememberCoroutineScope()
    Button(
        modifier = modifier.padding(8.dp).fillMaxSize(),
        onClick = { scope.launch(Dispatchers.IO) { openSaveChooserAndGetPath { path -> navigator.createCollage(path) } } },
    ) {
        Text(text = "Save Collage")
    }
}
