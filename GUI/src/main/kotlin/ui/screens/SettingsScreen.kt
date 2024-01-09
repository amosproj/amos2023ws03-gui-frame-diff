package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import models.AppState
import ui.components.*

/**
 * SettingsScreen is the screen where the user can change the settings of the app.
 *
 * @param state the state of the app
 * @param oldState the previous state of the app
 */
@Composable
fun SettingsScreen(state: MutableState<AppState>) {
    val oldState = remember { mutableStateOf(state.value.copy()) }
    val textForHyper =
        "Settings to adjust the distinction between added/deleted frames and" +
            " pixel differences within frames."
    val textForGapOpen = "explanation for gap open"
    val textForGapExtended = "explanation for gap extended"
    val textForMask =
        "Upload a png with white and black rectangles" +
            ".\nThe area marked with white rectangles will be included in the video difference computation " +
            "and the area with black rectangles will not be included in the computation."

    // Contains the whole Screen
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Title
        Row(modifier = Modifier.weight(0.2f)) { textTitle("Settings") }
        TitleWithInfo(Modifier.weight(0.15f), "Hyperparameters", textForHyper)
        // gap open penalty
        Row(modifier = Modifier.weight(0.2f)) {
            CustomSlider(
                title = "gapOpenPenalty",
                default = state.value.gapOpenPenalty,
                minValue = -1.0,
                maxValue = 0.5,
                tooltipText = textForGapOpen,
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
                tooltipText = textForGapExtended,
                onChange = { state.value = state.value.copy(gapExtendPenalty = it) },
            )
        }
        // mask
        Row(modifier = Modifier.weight(0.175f)) {
            FileSelectorButton(
                buttonText = "Upload Mask",
                buttonPath = state.value.maskPath,
                tooltipText = textForMask,
                onUpdateResult = { selectedFilePath ->
                    state.value = state.value.copy(maskPath = selectedFilePath)
                },
            )
        }
        Row(modifier = Modifier.weight(0.2f)) {
            // back
            BackButton(state, oldState)
            // save
            SaveButton(state, oldState)
        }
    }
}

@Composable
fun RowScope.BackButton(
    state: MutableState<AppState>,
    oldState: MutableState<AppState>,
) {
    Button(
        // fills all available space
        modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxSize(1f),
        onClick = { state.value = oldState.value.copy(screen = Screen.SelectVideoScreen) },
    ) {
        Image(
            painter = painterResource("back-arrow.svg"),
            contentDescription = "back",
            modifier = Modifier.fillMaxSize().alpha(0.8f).padding(4.dp),
        )
    }
}

@Composable
fun RowScope.SaveButton(
    state: MutableState<AppState>,
    oldState: MutableState<AppState>,
) {
    Button(
        // fills all available space
        modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxSize(1f),
        onClick = {
            oldState.value = state.value
            state.value = oldState.value.copy(screen = Screen.SelectVideoScreen)
        },
    ) {
        Image(
            painter = painterResource("save.svg"),
            contentDescription = "save",
            modifier = Modifier.fillMaxSize().alpha(0.8f).padding(4.dp),
        )
    }
}
