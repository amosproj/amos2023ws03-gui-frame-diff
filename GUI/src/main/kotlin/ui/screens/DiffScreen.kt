package ui.screens

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import frameNavigation.FrameNavigation
import models.AppState
import ui.components.diffScreen.*
import ui.components.general.HelpMenu
import ui.components.general.ProjectMenu
import ui.components.general.TextTitle

/**
 * A Composable function that creates a screen to display the differences between two videos.
 * Shows 3 videos: the reference video, the difference between the two videos, and the current video.
 * Gets recomposed when the state object changes, not when state properties change.
 * @param state [MutableState]<[AppState]> containing the global state.
 * @return [Unit]
 */

@Composable
fun DiffScreen(state: MutableState<AppState>) {
    // create the navigator, which implements the jumping logic
    val scope = rememberCoroutineScope()
    val navigator = FrameNavigation(state, scope)
    // force into focus to intercept key presses
    val focusRequester = FocusRequester()

    // ################################   Complete Screen   ################################
    Column(
        // grab focus, fill all available space, assign key press handler
        modifier =
            Modifier.fillMaxSize().focusRequester(focusRequester).focusable()
                .onKeyEvent { event -> keyEventHandler(event, navigator) },
    ) {
        // #####   Focus   #####
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        // #####   Top Bar   #####
        TopAppBar {
            Row(modifier = Modifier.fillMaxWidth()) {
                ProjectMenu(state, Modifier.weight(0.1f))
                SaveCollageButton(navigator, Modifier.weight(0.1f), state)
                SaveInsertedFramesButton(navigator, Modifier.weight(0.1f), state)
                Spacer(modifier = Modifier.weight(0.6f))
                HelpMenu(Modifier.weight(0.1f))
            }
        }

        // #####   Titles   #####
        Row(modifier = Modifier.fillMaxWidth().weight(0.1f)) {
            TextTitle(text = "Reference Video")
            TextTitle(text = "Diff")
            TextTitle(text = "Current Video")
        }

        Row(modifier = Modifier.fillMaxWidth().weight(0.1f)) {
            StatisticalInformation(navigator)
        }

        // #####   Difference Videos   #####
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(0.5f)) {
            DisplayDifferenceImage(bitmap = navigator.videoReferenceBitmap, navigator = navigator, title = "Reference Video", state = state)
            DisplayDifferenceImage(bitmap = navigator.diffBitmap, navigator = navigator, title = "Diff", state = state)
            DisplayDifferenceImage(bitmap = navigator.videoCurrentBitmap, navigator = navigator, title = "Current Video", state = state)
        }
        // #####   Timeline   #####
        Row(modifier = Modifier.fillMaxSize().weight(0.15f)) { Timeline(navigator) }

        // #####   Navigation   #####
        NavigationButtons(navigator, Modifier.weight(1f), Modifier.weight(0.15f))
    }
}
