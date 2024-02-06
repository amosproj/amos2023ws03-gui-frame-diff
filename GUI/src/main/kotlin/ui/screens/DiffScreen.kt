package ui.screens

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import logic.FrameGrabber
import models.AppState
import models.defaultOutputPath
import ui.components.diffScreen.*
import ui.components.diffScreen.timeline.Timeline
import ui.components.general.ConfirmationPopup
import ui.components.general.HelpMenu
import ui.components.general.ProjectMenu
import java.io.File

/**
 * A Composable function that creates a screen to display the differences between two videos. Shows
 * 3 videos: the reference video, the difference between the two videos, and the current video. Gets
 * recomposed when the state object changes, not when state properties change.
 * @param state [MutableState]<[AppState]> containing the global state.
 * @return [Unit]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiffScreen(state: MutableState<AppState>) {
    // create the navigator, which implements the jumping logic
    val navigator = remember { FrameNavigation(state) }
    val showConfirmationDialog = remember { mutableStateOf(false) }
    val frameGrabber =
        FrameGrabber(state.value.videoReferencePath!!, state.value.videoCurrentPath!!, state.value.outputPath!!, state.value.sequenceObj)
    val thumbnailGrabber =
        FrameGrabber(
            state.value.thumbnailVideoPathReference!!,
            state.value.thumbnailVideoPathCurrent!!,
            diffSequence = state.value.sequenceObj,
        )

    DisposableEffect(Unit) {
        onDispose {
            frameGrabber.close()
            thumbnailGrabber.close()
            val f = File(defaultOutputPath)
            if (f.exists()) f.delete()
        }
    }

    // force into focus to intercept key presses
    val focusRequester = FocusRequester()

    // ################################   Complete Screen   ################################
    Column(
        // grab focus, fill all available space, assign key press handler
        modifier =
            Modifier.fillMaxSize().focusRequester(focusRequester).focusable().onKeyEvent {
                    event ->
                keyEventHandler(event, navigator)
            },
    ) {
        // #####   Focus   #####
        LaunchedEffect(Unit) { focusRequester.requestFocus() }

        // #####   Top Bar   #####
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Difference Screen",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                )
            },
            navigationIcon = {
                Row {
                    IconButton(
                        modifier = Modifier.padding(8.dp),
                        content = { Icon(Icons.Default.ArrowBack, "back button") },
                        onClick = {
                            if (state.value.hasUnsavedChanges) {
                                showConfirmationDialog.value = true
                            } else {
                                state.value = state.value.copy(screen = Screen.SelectVideoScreen)
                            }
                        },
                    )
                    ProjectMenu(state)
                    // Decide whether to show the menu as a dropdown or as a row of buttons
                    BoxWithConstraints {
                        if (maxWidth < 1200.dp) {
                            var expanded by remember { mutableStateOf(false) }
                            IconButton(
                                modifier = Modifier.padding(8.dp),
                                content = { Icon(Icons.Default.Menu, "Menu button") },
                                onClick = { expanded = true },
                            )
                            DropdownMenu(
                                content = {
                                    SaveCollageButton(frameGrabber, navigator.currentDiffIndex.value, state = state)
                                    SaveInsertedFramesButton(
                                        frameGrabber = frameGrabber,
                                        state = state,
                                    )
                                },
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            )
                        } else {
                            Row {
                                SaveCollageButton(frameGrabber, navigator.currentDiffIndex.value, state = state)
                                SaveInsertedFramesButton(frameGrabber = frameGrabber, state = state)
                            }
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
            actions = { HelpMenu() },
        )

        // #####   Difference Videos   #####
        Row(
            modifier = Modifier.fillMaxWidth().weight(0.45f),
            verticalAlignment = Alignment.Bottom,
        ) {
            DisplayDifferenceImage(
                navigator = navigator,
                grabImage = frameGrabber::getReferenceVideoFrame,
                title = "Reference Video",
                state = state,
            )
            DisplayDifferenceImage(
                navigator = navigator,
                grabImage = frameGrabber::getDiffVideoFrame,
                title = "Difference",
                state = state,
            )
            DisplayDifferenceImage(
                navigator = navigator,
                grabImage = frameGrabber::getCurrentVideoFrame,
                title = "Current Video",
                state = state,
            )
        }
        // #####   Timeline   #####
        Row(modifier = Modifier.fillMaxSize().weight(0.29f)) { Timeline(navigator, frameGrabber = thumbnailGrabber) }

        // #####   Navigation   #####
        NavigationButtons(navigator, Modifier.weight(1f), Modifier.weight(0.10f))
    }
    // #####   Confirmation Dialog   #####
    ConfirmationPopup(
        showDialog = showConfirmationDialog.value,
        onConfirm = {
            state.value =
                state.value.copy(
                    screen = Screen.SelectVideoScreen,
                    hasUnsavedChanges = false,
                )
        },
        onCancel = { showConfirmationDialog.value = false },
        text =
            "Are you sure you want to go back to the main screen without saving the Difference Video?",
    )
}
