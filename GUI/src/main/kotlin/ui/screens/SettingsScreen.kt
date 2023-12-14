package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import models.AppState
import ui.components.AutoSizeText
import ui.components.FileSelectorButton

@Composable
fun SettingsScreen(state: MutableState<AppState>) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Title
        Row(modifier = Modifier.weight(0.2f)) {
            Title("Settings")
        }
        // gap open penalty
        Row(modifier = Modifier.weight(0.2f)) {
            CustomSlider(
                title = "Gap Open Penalty",
                default = state.value.gapOpenPenalty,
                minValue = -1.0,
                maxValue = 0.5,
                onChange = { state.value = state.value.copy(gapOpenPenalty = it) },
            )
        }
        // gap extend penalty
        Row(modifier = Modifier.weight(0.2f)) {
            CustomSlider(
                title = "Gap Extend Penalty",
                default = state.value.gapExtendPenalty,
                minValue = -1.0,
                maxValue = 0.5,
                onChange = { state.value = state.value.copy(gapExtendPenalty = it) },
            )
        }
        // mask
        Row(modifier = Modifier.weight(0.2f)) {
            FileSelectorButton(
                buttonText = "Upload  Mask",
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
fun RowScope.CustomSlider(
    title: String,
    default: Double,
    minValue: Double,
    maxValue: Double,
    onChange: (Double) -> Unit,
) {
    var sliderValue by remember { mutableStateOf(default) }
    Column(modifier = Modifier.weight(1f).padding(8.dp).fillMaxHeight(1f)) {
        AutoSizeText(text = title, modifier = Modifier.weight(0.5f).padding(8.dp).fillMaxHeight(1f).align(Alignment.CenterHorizontally))
        Row(modifier = Modifier.weight(0.2f)) {
            AutoSizeText(text = minValue.toString(), modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxHeight(1f))
            Slider(
                value = sliderValue.toFloat(),
                steps = ((maxValue - minValue) * 100).toInt() - 1,
                onValueChange = {
                    onChange(it.toDouble())
                    sliderValue = it.toDouble()
                },
                valueRange = minValue.toFloat()..maxValue.toFloat(),
                modifier = Modifier.weight(0.5f).padding(8.dp).fillMaxHeight(1f),
            )
            AutoSizeText(text = maxValue.toString(), modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxHeight(1f))
            AutoSizeText(text = String.format("%.2f", sliderValue), modifier = Modifier.weight(0.1f).padding(8.dp).fillMaxHeight(1f))
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
