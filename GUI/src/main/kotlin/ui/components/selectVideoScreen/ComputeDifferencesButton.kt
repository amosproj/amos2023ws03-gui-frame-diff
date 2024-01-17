package ui.components.selectVideoScreen

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logic.differenceGeneratorWrapper.DifferenceGeneratorWrapper
import models.AppState
import ui.components.general.AutoSizeText

/**
 * A Composable function that creates a button to compute the differences between two videos.
 *
 * @param state [AppState] object containing the state of the application.
 * @return [Unit]
 */
@Composable
fun RowScope.ComputeDifferencesButton(state: MutableState<AppState>) {
    val scope = rememberCoroutineScope()
    Button(
        // fills all available space
        modifier = Modifier.weight(0.9f).padding(8.dp).fillMaxSize(1f),
        onClick = {
            scope.launch(Dispatchers.IO) {
                // generate the differences
                val generator = DifferenceGeneratorWrapper(state)
                generator.getDifferences(state.value.outputPath)
                // set the sequence and screen
                state.value = state.value.copy(sequenceObj = generator.getSequence(), screen = Screen.DiffScreen)
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
