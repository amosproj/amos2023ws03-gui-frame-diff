

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import javax.swing.JFileChooser

/**
 * Represents the different screens of a video application.
 */
sealed class Screen {
    /**
     * Represents the screen for selecting a video.
     *
     * This class extends the abstract class `Screen` to handle the specific functionalities
     * required for selecting a video.
     */
    object SelectVideoScreen : Screen()

    /**
     * The DisplayVideoScreen class represents the screen that displays a video.
     * It is a subclass of the Screen class.
     *
     * @constructor Creates a new DisplayVideoScreen object.
     * @extends Screen
     * @property {string} videoUrl - The URL of the video to be displayed.
     * @property {boolean} isPlaying - Indicates whether the video is currently playing or not.
     */
    object DisplayVideoScreen : Screen()
}

/**
 * The main entry point of the application.
 *
 * Starts the application by creating a window and initializing the App scene.
 *
 * @return [Unit]
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main(): Unit =
    application {
        Window(
            title = "amos2023ws03-gui-frame-diff",
            onCloseRequest = ::exitApplication,
            state = WindowState(width = 1800.dp, height = 1000.dp),
            onKeyEvent = { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight && !event.isCtrlPressed) {
                    println("right was pressed ")
                } else if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft && !event.isCtrlPressed) {
                    println("left was pressed ")
                } else if (event.type == KeyEventType.KeyDown && event.isCtrlPressed && event.key == Key.DirectionRight) {
                    println("Ctrl + right was pressed")
                } else if (event.type == KeyEventType.KeyDown && event.isCtrlPressed && event.key == Key.DirectionLeft) {
                    println("Ctrl + left was pressed")
                }
                false
            },
        ) {
            App()
        }
    }

/**
 * This function is a composable function that defines the UI structure and behavior of the application.
 * It sets up a `screen` mutable state using the `remember` function and sets its initial value to `Screen.SelectVideoScreen`.
 * It then uses a `when` statement to determine the current value of the `screen` state and renders the corresponding screen.
 *
 * @return Unit
 */
@Composable
fun App() {
    var screen by remember { mutableStateOf<Screen>(Screen.SelectVideoScreen) }
    when (screen) {
        is Screen.SelectVideoScreen -> SelectVideoScreen { screen = Screen.DisplayVideoScreen }
        is Screen.DisplayVideoScreen -> DisplayVideoScreen { screen = Screen.SelectVideoScreen }
    }
}

@Composable
fun testScreen(onNavigate: () -> Unit) {
}

/**
 * Composable method to display the select video screen.
 *
 * @param onNavigate Callback function to be called when user clicks on the "Compute differences and navigate" button.
 */
@Composable
fun SelectVideoScreen(onNavigate: () -> Unit) {
    // Variables to store paths of the selected videos
    var video1Path by remember { mutableStateOf<String?>("") }
    var video2Path by remember { mutableStateOf<String?>("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row {
            FileSelectorButton(buttonText = "Select Video 1") { selectedFilePath ->
                // Update video1Path after file being selected
                video1Path = selectedFilePath
            }
            Spacer(modifier = Modifier.width(8.dp))
            FileSelectorButton(buttonText = "Select Video 2") { selectedFilePath ->
                // Update video2Path after file being selected
                video2Path = selectedFilePath
            }
        }
        // Perform your video difference computation here
        Button(
            onClick = onNavigate,
            // enabled = video1Path?.isNotEmpty() == true && video2Path?.isNotEmpty() == true
        ) {
            Text("Compute differences and navigate")
        }
        Text("Selected Video 1 Path: $video1Path")
        Text("Selected Video 2 Path: $video2Path")
    }
}

/**
 * Displays a screen for video playback.
 *
 * @param onNavigate A function to be called when the user wants to navigate back from the video screen.
 *
 */
@Composable
fun DisplayVideoScreen(onNavigate: () -> Unit) {
    VideoEdit().DisplayVideoScreen()

    Button(onClick = onNavigate) {
        Text("Back")
    }
}

/**
 * A Composable function that creates a button with a file selector functionality.
 *
 * @param buttonText The text to be displayed on the button.
 * @param onUpdateResult A function that will be called with the selected file path as a parameter.
 */
@Composable
fun FileSelectorButton(
    buttonText: String,
    onUpdateResult: (String) -> Unit,
) {
    Button(onClick = {
        try {
            val path = openFileChooserAndGetPath()
            if (path != null) {
                onUpdateResult(path)
            }
        } catch (e: Exception) {
            println("Error updating result: ${e.localizedMessage}")
        }
    }) {
        Text(buttonText)
    }
}

/**
 * Opens a file chooser dialog and returns the selected file path.
 *
 * @return The selected file path, or null if no file was selected.
 */
fun openFileChooserAndGetPath(): String? {
    val fileChooser = JFileChooser()
    val result = fileChooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) fileChooser.selectedFile.absolutePath else null
}
