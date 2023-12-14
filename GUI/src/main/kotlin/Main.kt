import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import models.AppState
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
        // create the global state
        var globalState = remember { mutableStateOf(AppState()) }
        // pass the global state to the screen, access data using state.value.*
        // to update the global state, use state.value = state.value.copy(...)
        when (globalState.value.screen) {
            is Screen.SelectVideoScreen -> SelectVideoScreen(globalState)
            is Screen.DiffScreen -> DiffScreen(globalState)
            else -> throw Exception("Screen not implemented")
        }
    }
}
