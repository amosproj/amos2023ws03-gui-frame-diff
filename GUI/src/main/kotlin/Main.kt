import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import ui.screens.DiffScreen
import ui.screens.SelectVideoScreen

/**
 * The main entry point of the application.
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
 *
 * @return Unit
 */
@Composable
fun App() {
    var screen by remember { mutableStateOf<Screen>(Screen.SelectVideoScreen) }

    /**
     * This function is used to change the screen.
     *
     * @param newScreen The new screen to be displayed.
     * @return [Unit]
     */
    fun setScreen(newScreen: Screen) {
        screen = newScreen
    }

    when (screen) {
        is Screen.SelectVideoScreen -> SelectVideoScreen(::setScreen)
        is Screen.DiffScreen -> DiffScreen()
        else -> {
            throw Exception("Screen not implemented")
        }
    }
}
