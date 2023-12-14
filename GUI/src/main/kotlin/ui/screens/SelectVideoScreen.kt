package ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import logic.differenceGeneratorWrapper.DifferenceGeneratorWrapper
import models.AppState
import ui.components.AutoSizeText
import ui.components.FileSelectorButton

/**
 * A Composable function that creates a screen to select the videos to compare.
 * @param state [MutableState]<[AppState]> containing the global state.
 * @return [Unit]
 */
@Composable
fun SelectVideoScreen(state: MutableState<AppState>) {
    // column represents the whole screen
    Column(modifier = Modifier.fillMaxSize()) {
        // video selection
        Row(modifier = Modifier.weight(0.85f)) {
            FileSelectorButton(
                buttonText = "Select Video 1",
                buttonPath = state.value.video1Path,
                onUpdateResult = { selectedFilePath ->
                    state.value = state.value.copy(video1Path = selectedFilePath)
                },
            )

            FileSelectorButton(
                buttonText = "Select Video 2",
                buttonPath = state.value.video2Path,
                onUpdateResult = { selectedFilePath ->
                    state.value = state.value.copy(video2Path = selectedFilePath)
                },
            )
        }
        // button to compute the differences
        Row(modifier = Modifier.weight(0.15f)) {
            ComputeDifferencesButton(state)
        }
    }
}

/**
 * A Composable function that creates a button to compute the differences between two videos.
 *
 * @param state [AppState] object containing the state of the application.
 * @return [Unit]
 */
@Composable
fun RowScope.ComputeDifferencesButton(state: MutableState<AppState>) {
    Button(
        // fills all available space
        modifier = Modifier.weight(1f).padding(8.dp).fillMaxSize(1f),
        onClick = {
            // generate the differences
            val generator =
                DifferenceGeneratorWrapper(
                    video1Path = state.value.video1Path,
                    video2Path = state.value.video2Path,
                    outputPath = state.value.outputPath,
                )
            generator.getDifferences()
            // set the sequence and screen
            state.value = state.value.copy(sequenceObj = generator.getSequence(), screen = Screen.DiffScreen)
        },
        // enable the button only if all the paths are not empty
        enabled = state.value.video1Path.isNotEmpty() && state.value.video2Path.isNotEmpty() && state.value.outputPath.isNotEmpty(),
    ) {
        AutoSizeText(
            text = "Compute and Display Differences",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            // remove default centering
            modifier = Modifier,
        )
    }
}
