package ui.components.diffScreen

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.onKeyEvent
import frameNavigation.FrameNavigation
import ui.components.general.SaveableImage

/**
 * A Composable function that displays a differenceImage with a button to open the image in a
 * full screen window.
 * @param bitmap [MutableState]<[ImageBitmap]> containing the bitmap to display.
 * @param modifier [Modifier] to apply to the element.
 * @param navigator [FrameNavigation] containing the navigation logic.
 * @param title [String] containing the title of the window.
 * @return [Unit]
 */
@Composable
fun RowScope.DisplayDifferenceImage(
    bitmap: MutableState<ImageBitmap>,
    modifier: Modifier = Modifier,
    navigator: FrameNavigation,
    title: String,
) {
    // pop-out window
    val window = remember { mutableStateOf<Unit?>(null) }

    // handles the window creation if the window is not null
    WindowCreator(window, title) { FullScreenContent(bitmap = bitmap, navigator = navigator) }

    Column(modifier = Modifier.fillMaxSize().weight(1f)) {
        // button sets the window to null and then to not null, which triggers the window render
        FullScreenButton {
            window.value = null
            window.value = Unit
        }
        SaveableImage(bitmap = bitmap, modifier = modifier.weight(0.92f))
    }
}

/**
 * The Content being displayed in the full screen window.
 * @param bitmap [MutableState]<[ImageBitmap]> containing the bitmap to display.
 * @param navigator [FrameNavigation] containing the navigation logic.
 * @return [Unit]
 */
@Composable
fun FullScreenContent(
    bitmap: MutableState<ImageBitmap>,
    navigator: FrameNavigation,
) {
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier =
            Modifier.fillMaxSize().focusRequester(focusRequester).focusable()
                .onKeyEvent { event -> KeyEventHandler(event, navigator) },
    ) {
        // #####   Focus   #####
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        // #####   Difference Videos   #####
        SaveableImage(bitmap = bitmap, modifier = Modifier.weight(0.85f))
        // #####   Navigation   #####
        NavigationButtons(navigator = navigator, buttonModifier = Modifier.weight(1f), rowModifier = Modifier.weight(0.15f))
    }
}
