package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import models.AppState
import ui.components.CustomSlider
import ui.components.FileSelectorButton
import ui.components.textTitle

/**
 * SettingsScreen is the screen where the user can change the settings of the app.
 *
 * @param state the state of the app
 */
@Composable
fun SettingsScreen(state: MutableState<AppState>) {
    // Contains the whole Screen
    Column(modifier = Modifier.fillMaxSize()) {
        // Title
        Row(modifier = Modifier.weight(0.2f)) {
            textTitle("Settings")
        }
        // gap open penalty
        Row(modifier = Modifier.weight(0.2f)) {
            CustomSlider(
                title = " gapOpenPenalty",
                default = state.value.gapOpenPenalty,
                minValue = -1.0,
                maxValue = 0.5,
                onChange = { state.value = state.value.copy(gapOpenPenalty = it) },
            )
        }
        // gap extend penalty
        Row(modifier = Modifier.weight(0.2f)) {
            CustomSlider(
                title = "gapExtensionPenalty",
                default = state.value.gapExtendPenalty,
                minValue = -1.0,
                maxValue = 0.5,
                onChange = { state.value = state.value.copy(gapExtendPenalty = it) },
            )
        }
        // mask
        Row(modifier = Modifier.weight(0.2f)) {
            FileSelectorButton(
                buttonText = "Upload Mask",
                buttonPath = state.value.maskPath,
                onUpdateResult = { selectedFilePath ->
                    state.value = state.value.copy(maskPath = selectedFilePath)
                },
            )
        }
        // save
        Row(modifier = Modifier.weight(0.2f)) {
            SaveButton(state)
        }
    }
}

@Composable
fun RowScope.SaveButton(state: MutableState<AppState>) {
    Button(
        // fills all available space
        modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxSize(1f),
        onClick = {
            // set the screen
            state.value = state.value.copy(screen = Screen.SelectVideoScreen)
        },
    ) {
        Image(
            painter = painterResource("save.svg"),
            contentDescription = "save",
            modifier = Modifier.fillMaxSize().alpha(0.8f).padding(4.dp),
        )
    }
}
