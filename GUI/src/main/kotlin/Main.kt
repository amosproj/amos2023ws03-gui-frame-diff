import algorithms.AlignmentElement
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import models.AllVideos
import ui.screens.DiffScreen
import ui.screens.SelectVideoScreen
import ui.themes.defaultTheme

/**
 * The main entry point of the application.
 *
 * @return [Unit]
 */

fun main(): Unit =
    application {
        Window(
            title = "amos2023ws03-gui-frame-diff",
            onCloseRequest = ::exitApplication,
            state = WindowState(width = 1800.dp, height = 1000.dp),
        ) {
            // applies the default Theme to the application
            defaultTheme { App() }
        }
    }

/**
 * This function is a composable function that defines the UI structure and behavior of the application.
 *
 * @return Unit
 */
@Composable
fun App() {
    // Background Color
    Surface(color = Color.hsv(340f, 0.83f, 0.04f)) {
        var screen by remember { mutableStateOf<Screen>(Screen.SelectVideoScreen) }
        var pathObj by remember { mutableStateOf(AllVideos("", "", "")) }
        var sequenceObj by remember { mutableStateOf(arrayOf<AlignmentElement>()) }

        fun setDiffScreen(
            pathsObj: AllVideos<String>,
            sequences: Array<AlignmentElement>,
        ) {
            screen = Screen.DiffScreen
            sequenceObj = sequences
            pathObj = pathsObj
        }

        when (screen) {
            is Screen.SelectVideoScreen -> SelectVideoScreen(::setDiffScreen)
            is Screen.DiffScreen -> DiffScreen(pathObj, sequenceObj)
            else -> {
                throw Exception("Screen not implemented")
            }
        }
    }
}
