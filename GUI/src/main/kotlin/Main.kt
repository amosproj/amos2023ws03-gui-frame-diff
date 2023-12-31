import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import models.AppState
import ui.screens.DiffScreen
import ui.screens.SelectVideoScreen
import ui.screens.SettingsScreen
import ui.themes.wrapTheming

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
            wrapTheming { App() }
        }
    }

/**
 * This function is a composable function that defines the UI structure and behavior of the application.
 *
 * @return Unit
 */
@Composable
fun App() {
    // create the global state
    val globalState = remember { mutableStateOf(AppState()) }
    // pass the global state to the screen, access data using state.value.*
    // to update the global state, use state.value = state.value.copy(...)
    when (globalState.value.screen) {
        is Screen.SelectVideoScreen -> SelectVideoScreen(globalState)
        is Screen.DiffScreen -> DiffScreen(globalState)
        is Screen.SettingsScreen -> SettingsScreen(globalState)
        else -> throw Exception("Screen not implemented")
    }
}
