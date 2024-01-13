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
 * Shows 3 videos: the first video, the difference between the two videos, and the second video.
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
                .onKeyEvent { event -> KeyEventHandler(event, navigator) },
    ) {
        // #####   Focus   #####
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        // #####   Top Bar   #####
        TopAppBar {
            Row(modifier = Modifier.fillMaxWidth()) {
                ProjectMenu(state, Modifier.weight(0.1f))
                SaveCollageButton(navigator, Modifier.weight(0.1f))
                Spacer(modifier = Modifier.weight(0.7f))
                HelpMenu(Modifier.weight(0.1f))
            }
        }

        // #####   Titles   #####
        Row(modifier = Modifier.fillMaxWidth().weight(0.1f)) {
            TextTitle(text = "Video 1")
            TextTitle(text = "Diff")
            TextTitle(text = "Video 2")
        }

        Row(modifier = Modifier.fillMaxWidth().weight(0.1f)) {
            StatisticalInformation(navigator)
        }

        // #####   Difference Videos   #####
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(0.5f)) {
            DisplayDifferenceImage(bitmap = navigator.video1Bitmap, navigator = navigator, title = "Video 1")
            DisplayDifferenceImage(bitmap = navigator.diffBitmap, navigator = navigator, title = "Diff")
            DisplayDifferenceImage(bitmap = navigator.video2Bitmap, navigator = navigator, title = "Video 2")
        }
        // #####   Timeline   #####
        Row(modifier = Modifier.fillMaxSize().weight(0.15f)) { Timeline(navigator) }

        // #####   Navigation   #####
        NavigationButtons(navigator, Modifier.weight(1f), Modifier.weight(0.15f))
    }
}
