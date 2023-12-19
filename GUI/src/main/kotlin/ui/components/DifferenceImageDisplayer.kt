package ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import frameNavigation.FrameNavigation

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
    windowCreator(window, title) { fullScreenContent(bitmap = bitmap, navigator = navigator) }

    Column(modifier = Modifier.fillMaxSize().weight(1f)) {
        // button sets the window to not null, which triggers the window creator
        fullScreenButton { window.value = Unit }
        wrappedImage(bitmap = bitmap, modifier = modifier.weight(0.85f))
    }
}

/**
 * The Contentent being displayed in the full screen window.
 * @param bitmap [MutableState]<[ImageBitmap]> containing the bitmap to display.
 * @param navigator [FrameNavigation] containing the navigation logic.
 * @return [Unit]
 */
@Composable
fun fullScreenContent(
    bitmap: MutableState<ImageBitmap>,
    navigator: FrameNavigation,
) {
    Column {
        wrappedImage(bitmap = bitmap)
        NavigationButtons(navigator = navigator, buttonModifier = Modifier.weight(1f))
    }
}
