import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
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
        // create the global state
        val globalState = remember { mutableStateOf(AppState()) }

        Window(
            title = "GUI Frame Diff v${AppConfig.VERSION}",
            onCloseRequest = ::exitApplication,
            state = WindowState(width = 1800.dp, height = 1000.dp),
        ) {
            // applies the default Theme to the application
            wrapTheming { App(globalState) }
        }

        FilePicker(show = globalState.value.showFilePicker, fileExtensions = listOf("mov", "mkv")) { file ->
            globalState.value = globalState.value.copy(showFilePicker = false)

            if (file != null) {
                globalState.value.filePickerCallback(file.path)
            }
        }
    }

/**
 * This function is a composable function that defines the UI structure and behavior of the application.
 *
 * @return Unit
 */
@Composable
fun App(globalState: MutableState<AppState>) {
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
    const val VERSION = "1.0.0" // has to be updated manually, when the version in gradle is updated
}
