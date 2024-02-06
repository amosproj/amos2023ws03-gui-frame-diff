// SPDX-License-Identifier: MIT
// SPDX-FileCopyrightText: AlperK61 <92909013+alperk61@users.noreply.github.com>
// SPDX-FileCopyrightText: a-miscellaneous <96189996+a-miscellaneous@users.noreply.github.com>
// SPDX-FileCopyrightText: Anton Kriese <anton.kriese@fu-berlin.de>
// SPDX-FileCopyrightText: simonsasse <simonsasse97@gmail.com>
// SPDX-FileCopyrightText: zino212 <luisguenther@gmx.de>
package ui.components.diffScreen

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import frameNavigation.FrameNavigation
import models.AppState
import ui.components.general.SaveableImage

/**
 * A Composable function that displays a differenceImage with a button to open the image in a
 * full screen window.
 * @param modifier [Modifier] to apply to the element.
 * @param navigator [FrameNavigation] containing the navigation logic.
 * @param grabImage [Function]<[Int], [ImageBitmap]> containing the function to grab the image at a diff index.
 * @param title [String] containing the title of the window.
 * @param state [mutableStateOf]<[AppState]> containing the global state of the application.
 * @return [Unit]
 */
@Composable
fun RowScope.DisplayDifferenceImage(
    modifier: Modifier = Modifier,
    navigator: FrameNavigation,
    grabImage: (Int) -> ImageBitmap,
    title: String,
    state: MutableState<AppState>,
) {
    // pop-out window
    val window = remember { mutableStateOf<Unit?>(null) }
    val bitmap = remember { mutableStateOf(ImageBitmap(1, 1)) }

    bitmap.value = grabImage(navigator.currentDiffIndex.value)

    // handles the window creation if the window is not null
    WindowCreator(window, title) { FullScreenContent(bitmap = bitmap, navigator = navigator, state) }

    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Bottom) {
        // button sets the window to null and then to not null, which triggers the window render

        Row(Modifier, verticalAlignment = Alignment.CenterVertically) {
            VideoTitle(title)
            FullScreenButton {
                window.value = null
                window.value = Unit
            }
        }
        SaveableImage(bitmap = bitmap, modifier = modifier, fullScreen = false, state = state)
    }
}

/**
 * The Content being displayed in the full screen window.
 * @param bitmap [MutableState]<[ImageBitmap]> containing the bitmap to display.
 * @param navigator [FrameNavigation] containing the navigation logic.
 * @param state [mutableStateOf]<[AppState]> containing the global state of the application.
 * @return [Unit]
 */
@Composable
fun FullScreenContent(
    bitmap: MutableState<ImageBitmap>,
    navigator: FrameNavigation,
    state: MutableState<AppState>,
) {
    val focusRequester = remember { FocusRequester() }
    Column(
        modifier =
            Modifier.fillMaxSize().focusRequester(focusRequester).focusable()
                .onKeyEvent { event -> keyEventHandler(event, navigator) },
    ) {
        // #####   Focus   #####
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        // #####   Difference Videos   #####
        SaveableImage(bitmap = bitmap, modifier = Modifier.fillMaxSize().weight(0.85f), fullScreen = true, state = state)
        // #####   Navigation   #####
        NavigationButtons(navigator = navigator, buttonModifier = Modifier.weight(1f), rowModifier = Modifier.weight(0.10f))
    }
}

/**
 * A Composable function that creates a centered title with 30% width.
 * @param text [String] containing the text of the title.
 * @return [Unit]
 */
@Composable
fun RowScope.VideoTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(0.dp, 5.dp).weight(0.7f),
        fontSize = MaterialTheme.typography.displaySmall.fontSize,
        textAlign = TextAlign.Center,
    )
}
