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
import models.AppState
import models.defaultOutputPath
import ui.components.diffScreen.*
import ui.components.general.HelpMenu
import ui.components.general.ProjectMenu
import java.io.File

/**
 * A Composable function that creates a screen to display the differences between two videos.
 * Shows 3 videos: the reference video, the difference between the two videos, and the current video.
 * Gets recomposed when the state object changes, not when state properties change.
 * @param state [MutableState]<[AppState]> containing the global state.
 * @return [Unit]
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiffScreen(state: MutableState<AppState>) {
    // create the navigator, which implements the jumping logic
    val scope = rememberCoroutineScope()
    val navigator = FrameNavigation(state, scope)
    DisposableEffect(Unit) {
        onDispose {
            navigator.close()
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
            Modifier.fillMaxSize().focusRequester(focusRequester).focusable()
                .onKeyEvent { event -> keyEventHandler(event, navigator) },
    ) {
        // #####   Focus   #####
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

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
                        onClick = { state.value = state.value.copy(screen = Screen.SelectVideoScreen) },
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
                                    SaveCollageButton(navigator = navigator, state = state)
                                    SaveInsertedFramesButton(navigator = navigator, state = state)
                                },
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            )
                        } else {
                            Row {
                                SaveCollageButton(navigator = navigator, state = state)
                                SaveInsertedFramesButton(navigator = navigator, state = state)
                            }
                        }
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
            actions = {
                HelpMenu()
            },
        )
        // #####   Difference Videos   #####
        Row(modifier = Modifier.fillMaxWidth().weight(0.45f), verticalAlignment = Alignment.Bottom) {
            DisplayDifferenceImage(bitmap = navigator.videoReferenceBitmap, navigator = navigator, title = "Reference Video", state = state)
            DisplayDifferenceImage(bitmap = navigator.diffBitmap, navigator = navigator, title = "Difference", state = state)
            DisplayDifferenceImage(bitmap = navigator.videoCurrentBitmap, navigator = navigator, title = "Current Video", state = state)
        }
        // #####   Timeline   #####
        Row(
            modifier = Modifier.fillMaxSize().weight(0.29f),
        ) {
            Timeline(navigator)
        }

        // #####   Navigation   #####
        NavigationButtons(navigator, Modifier.weight(1f), Modifier.weight(0.10f))
    }
}
