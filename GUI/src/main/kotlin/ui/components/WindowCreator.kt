package ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import ui.themes.wrapTheming

/**
 * A Composable function that creates a window if the window is not null.
 * @param window [MutableState]<[Unit]> containing the window state. Used to keep track of this window.
 * @param title [String] containing the title of the window.
 * @param content [Composable] function to display in the window.
 * @return [Unit]
 */
@Composable
fun windowCreator(
    window: MutableState<Unit?>,
    title: String,
    content: @Composable () -> Unit,
) {
    // if no window, return
    if (window.value == null) {
        return
    }
    // remember the window state
    val state = rememberWindowState(size = DpSize(1800.dp, 1000.dp))
    window.value =
        Window(
            title = title,
            onCloseRequest = { window.value = null },
            state = state,
        ) { wrapTheming(content) }
}
