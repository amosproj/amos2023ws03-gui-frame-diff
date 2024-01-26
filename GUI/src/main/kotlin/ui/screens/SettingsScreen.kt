package ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import models.AppState
import ui.components.general.HelpMenu
import ui.components.general.ProjectMenu
import ui.components.general.TitleWithInfo
import ui.components.selectVideoScreen.MaskSelectorButton
import ui.components.settingsScreen.CustomSlider
import ui.components.settingsScreen.SaveButton

/**
 * SettingsScreen is the screen where the user can change the settings of the app.
 *
 * @param state the state of the app
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(state: MutableState<AppState>) {
        val oldState = remember { mutableStateOf(state.value.copy()) }
        val textForHyper =
                        "Settings to adjust behavior of the Gotoh alignment algorithm,\n" +
                                        "which determines the matches between frames from both input videos."
        val textForGapOpen =
                        "Penalty score for starting a new gap (insertion/deletion) in the alignment.\n" +
                                        "Decreasing this value will favor fewer, larger gaps in the alignment."
        val textForGapExtended =
                        "Penalty score for extending an existing gap (insertion/deletion) in the alignment.\n" +
                                        "Increasing this value will favor longer gaps in the alignment.\n" +
                                        "The gapOpenPenalty should be smaller than the gapExtensionPenalty.\n" +
                                        "If both parameters are equal, the algorithm will behave like the Needleman-Wunsch algorithm,\n" +
                                        "meaning that new gaps are as likely as extending existing gaps."
        val textForMask =
                        "Upload a png with a clear background and colored areas.\n" +
                                        "The transparent areas will be included in the video difference computation \n" +
                                        "while the opaque areas will be excluded."
        // Contains the whole Screen
        Column(
                        modifier = Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
        ) {
                CenterAlignedTopAppBar(
                                title = {
                                        Text(
                                                        text = "Settings",
                                                        style =
                                                                        MaterialTheme.typography
                                                                                        .titleMedium,
                                                        color = MaterialTheme.colorScheme.secondary,
                                        )
                                },
                                navigationIcon = {
                                        Row {
                                                IconButton(
                                                                modifier = Modifier.padding(8.dp),
                                                                content = {
                                                                        Icon(
                                                                                        Icons.Default
                                                                                                        .ArrowBack,
                                                                                        "back button"
                                                                        )
                                                                },
                                                                onClick = {
                                                                        state.value =
                                                                                        state.value
                                                                                                        .copy(
                                                                                                                        screen =
                                                                                                                                        Screen.SelectVideoScreen
                                                                                                        )
                                                                },
                                                )
                                                ProjectMenu(state)
                                        }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
                                actions = { HelpMenu() },
                )

                TitleWithInfo(
                                "Hyperparameters",
                                textForHyper,
                                MaterialTheme.typography.headlineMedium.fontSize,
                                20.dp
                )

                // gap open penalty
                Row(modifier = Modifier.weight(0.2f)) {
                        CustomSlider(
                                        title = "gapOpenPenalty",
                                        default = state.value.gapOpenPenalty,
                                        minValue = -1.0,
                                        maxValue = 0.5,
                                        tooltipText = textForGapOpen,
                                        onChange = {
                                                state.value = state.value.copy(gapOpenPenalty = it)
                                        },
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
                                        onChange = {
                                                state.value =
                                                                state.value.copy(
                                                                                gapExtendPenalty =
                                                                                                it
                                                                )
                                        },
                        )
                }

                // mask
                Row(
                                modifier = Modifier.weight(0.175f),
                                horizontalArrangement = Arrangement.Center,
                ) {
                        Column(modifier = Modifier.weight(0.2f)) {}
                        MaskSelectorButton(
                                        buttonText = "Upload Mask",
                                        buttonPath = state.value.maskPath,
                                        tooltipText = textForMask,
                                        onUpdateResult = { selectedFilePath ->
                                                state.value =
                                                                state.value.copy(
                                                                                maskPath =
                                                                                                selectedFilePath
                                                                )
                                        },
                                        directoryPath = state.value.maskPath,
                        )
                        Column(modifier = Modifier.weight(0.2f)) {
                                // Implement remove mask button here
                        }
                        Column(modifier = Modifier.weight(0.2f)) {}
                }

                // back and save button
                Row(
                                modifier = Modifier.weight(0.2f),
                ) {
                        Column(modifier = Modifier.weight(0.3f)) {}
                        SaveButton(state, oldState)
                        Column(modifier = Modifier.weight(0.3f)) {}
                }
        }
}
