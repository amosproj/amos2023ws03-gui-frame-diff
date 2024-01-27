import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import models.createAppState
import ui.screens.DiffScreen
import ui.screens.SelectVideoScreen
import ui.screens.SettingsScreen
import ui.themes.WrapTheming

/**
 * The main entry point of the application.
 *
 * @return [Unit]
 */
fun main(): Unit =
    application {
        Window(
            title = "GUI Frame Diff v${AppConfig.VERSION}",
            onCloseRequest = ::exitApplication,
            state = WindowState(width = 1800.dp, height = 1000.dp),
        ) {
            // applies the default Theme to the application
            WrapTheming { App() }
        }
    }

/**
 * This function is a composable function that defines the UI structure and behavior of the application.
 * It handles screen switching and passes the global state to the screens.
 * @return Unit
 */
@Composable
fun App() {
    // create the global state
    val globalState =
        remember {
            mutableStateOf(
                createAppState(useDefaultPaths = System.getenv("GUI_USE_DEFAULT_PATHS").toBoolean()),
            )
        }

    // pass the global state to the screen, access data using state.value.*
    // to update the global state, use state.value = state.value.copy(...)
    when (globalState.value.screen) {
        is Screen.SelectVideoScreen -> SelectVideoScreen(globalState)
        is Screen.DiffScreen -> DiffScreen(globalState)
        is Screen.SettingsScreen -> SettingsScreen(globalState)
        else -> throw Exception("Screen not implemented")
    }
}

object AppConfig {
    const val VERSION = "1.11.0" // has to be updated manually, when new version is released
}
