package ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import models.AppState
import ui.components.general.HelpMenu
import ui.components.general.ProjectMenu
import ui.components.selectVideoScreen.AdvancedSettingsButton
import ui.components.selectVideoScreen.ComputeDifferencesButton
import ui.components.selectVideoScreen.FileSelectorButton

/**
 * A Composable function that creates a screen to select the videos to compare.
 * @param state [MutableState]<[AppState]> containing the global state.
 * @return [Unit]
 */
@Composable
fun SelectVideoScreen(state: MutableState<AppState>) {
    // column represents the whole screen
    Column(modifier = Modifier.fillMaxSize()) {
        // menu bar
        TopAppBar {
            Row(modifier = Modifier.fillMaxWidth()) {
                ProjectMenu(state, Modifier.weight(0.1f))
                Spacer(modifier = Modifier.weight(0.8f))
                HelpMenu(Modifier.weight(0.1f))
            }
        }

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
        // screen switch buttons
        Row(modifier = Modifier.weight(0.15f)) {
            ComputeDifferencesButton(state)
            AdvancedSettingsButton(state)
        }
    }
}
