package ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import models.AppState
import ui.components.CustomSlider
import ui.components.FileSelectorButton
import ui.components.textTitle

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
        "The hyperparameter are settings to adjust the distinction between added and deleted frames and" +
            " a pixel difference within a frame."
    val textForGapOpen = "add here explanation for gap open"
    val textForGapExtended = "add here explanation for gap extended"
    val textForMask =
        "Upload a png with white and black rectangles" +
            ".\nEverything that is within white rectangles in video will be considered in the video difference computation " +
            "and everything that is within black rectangles will be not considered in the video difference computation."

    // Contains the whole Screen
    Column(modifier = Modifier.fillMaxSize()) {
        // Title
        Row(modifier = Modifier.weight(0.2f)) {
            textTitle("Settings")
        }
        Row(modifier = Modifier.weight(0.15f)) {
            textTitle("Hyperparameters")
            InfoIconWithHover(textForHyper)
        }
        // gap open penalty
        Row(modifier = Modifier.weight(0.125f)) {
            CustomSlider(
                title = "gapOpenPenalty",
                default = state.value.gapOpenPenalty,
                minValue = -1.0,
                maxValue = 0.5,
                onChange = {
                    state.value = state.value.copy(gapOpenPenalty = it)
                },
            )
            InfoIconWithHover(textForGapOpen)
        }
        // gap extend penalty
        Row(modifier = Modifier.weight(0.125f)) {
            CustomSlider(
                title = "gapExtensionPenalty",
                default = state.value.gapExtendPenalty,
                minValue = -1.0,
                maxValue = 0.5,
                onChange = { state.value = state.value.copy(gapExtendPenalty = it) },
            )
            InfoIconWithHover(textForGapExtended)
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
            InfoIconWithHover(textForMask)
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
        onClick = {
            state.value =
                oldState.value.copy(screen = Screen.SelectVideoScreen)
        },
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
            state.value =
                oldState.value.copy(screen = Screen.SelectVideoScreen)
        },
    ) {
        Image(
            painter = painterResource("save.svg"),
            contentDescription = "save",
            modifier = Modifier.fillMaxSize().alpha(0.8f).padding(4.dp),
        )
    }
}

@Composable
fun InfoIconWithHover(text: String) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // Remember the current hover state to use in DisposableEffect
    var currentIsHovered by remember { mutableStateOf(isHovered) }

    DisposableEffect(currentIsHovered) {
        onDispose {
            // Close the tooltip when the composable is disposed
            currentIsHovered = false
        }
    }

    Box(
        modifier =
            Modifier
                .size(40.dp)
                .hoverable(
                    interactionSource = interactionSource,
                ),
        contentAlignment = Alignment.Center,
    ) {
        // Info icon
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            tint =
                if (isHovered) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onBackground.copy(
                        alpha = LocalContentAlpha.current,
                    )
                },
            modifier = Modifier.size(24.dp),
        )

        // Tooltip
        if (isHovered) {
            // Set the current hover state to true
            currentIsHovered = true
            Tooltip(text = text)
        }
    }
}

@Composable
fun Tooltip(text: String) {
    Box {
        val cornerSize = 16.dp

        Popup(alignment = Alignment.CenterEnd) {
            // Draw a rectangle shape with rounded corners inside the popup
            Box(
                Modifier
                    .background(Color.DarkGray, RoundedCornerShape(cornerSize)),
            ) {
                Text(
                    text = text,
                    modifier = Modifier,
                    color = Color.White,
                )
            }
        }
    }
}
