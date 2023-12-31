package ui.screens

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import models.AppState
import ui.components.*

/**
 * A Composable function that creates a screen to display the differences between two videos.
 * Shows 3 videos: the first video, the difference between the two videos, and the second video.
 * @param state [MutableState]<[AppState]> containing the global state.
 * @return [Unit]
 */

@Composable
fun DiffScreen(state: MutableState<AppState>) {
    // create the navigator, which implements the jumping logic
    val navigator = FrameNavigation(state)
    // force into focus to intercept key presses
    val focusRequester = remember { FocusRequester() }

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
                saveCollageButton(navigator, Modifier.weight(0.1f))
                Spacer(modifier = Modifier.weight(0.8f))
                helpMenu(Modifier.weight(0.1f))
            }
        }

        // #####   Titles   #####
        Row(modifier = Modifier.fillMaxWidth().weight(0.2f)) {
            textTitle(text = "Video 1")
            textTitle(text = "Diff")
            textTitle(text = "Video 2")
        }

        // #####   Difference Videos   #####
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(0.5f)) {
            DisplayDifferenceImage(bitmap = navigator.video1Bitmap, navigator = navigator, title = "Video 1")
            DisplayDifferenceImage(bitmap = navigator.diffBitmap, navigator = navigator, title = "Diff")
            DisplayDifferenceImage(bitmap = navigator.video2Bitmap, navigator = navigator, title = "Video 2")
        }

        // #####   Timeline   #####
        Row(modifier = Modifier.fillMaxSize().weight(0.15f)) { timeline(navigator) }

        // #####   Navigation   #####
        NavigationButtons(navigator, Modifier.weight(1f), Modifier.weight(0.15f))
    }
}

@Composable
fun saveCollageButton(
    navigator: FrameNavigation,
    modifier: Modifier,
) {
    // #####   Save Collage Button   #####
    Button(
        modifier = modifier.padding(8.dp).fillMaxSize(),
        onClick = { openFileChooserAndGetPath()?.let { navigator.createCollage(it) } },
    ) {
        Text(text = "Save Collage")
    }
}
