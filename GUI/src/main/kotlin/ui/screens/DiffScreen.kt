package ui.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import frameNavigation.FrameNavigation
import models.AppState
import ui.components.NavigationButtons
import ui.components.svgButton
import ui.components.textTitle
import ui.themes.wrapTheming

/**
 * A Composable function that creates a screen to display the differences between two videos.
 * @param state [MutableState]<[AppState]> containing the global state.
 * @return [Unit]
 */

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DiffScreen(state: MutableState<AppState>) {
    // create the navigator, which implements the jumping logic
    val navigator = FrameNavigation(state)
    // force into focus to intercept key presses
    val focusRequester = remember { FocusRequester() }
    // map of key presses to actions
    val keyActions =
        mapOf(
            Pair(false, Key.DirectionRight) to { navigator.jumpFrames(1) },
            Pair(false, Key.DirectionLeft) to { navigator.jumpFrames(-1) },
            Pair(true, Key.DirectionRight) to { navigator.jumpToNextDiff(true) },
            Pair(true, Key.DirectionLeft) to { navigator.jumpToNextDiff(false) },
        )

    // create the screen
    Column(
        // grab focus, fill all available space, assign key press handler
        modifier =
            Modifier.fillMaxSize().focusRequester(focusRequester).focusable()
                .onKeyEvent { event -> keyEventHandler(event, keyActions) },
    ) {
//        ###########   Focus   ###########
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

//        ###########   Text   ###########
        Row(modifier = Modifier.fillMaxWidth().weight(0.2f)) {
            textTitle(text = "Video 1")
            textTitle(text = "Diff")
            textTitle(text = "Video 2")
        }
//        ###########   Box   ###########
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(0.6f)) {
            DisplayedImage(bitmap = navigator.video1Bitmap, navigator = navigator, title = "Video 1")
            DisplayedImage(bitmap = navigator.diffBitmap, navigator = navigator, title = "Diff")
            DisplayedImage(bitmap = navigator.video2Bitmap, navigator = navigator, title = "Video 2")
        }
//        ###########   Buttons   ###########
        NavigationButtons(navigator = navigator, buttonModifier = Modifier.weight(1f), rowModifier = Modifier.weight(0.2f))
    }
}

/**
 * A Composable function that creates a box to display an image.
 * @param bitmap [MutableState] of [ImageBitmap] containing the bitmap to be displayed.
 * @return [Unit]
 */
@Composable
fun windowCreator(
    bitmap: MutableState<ImageBitmap>,
    isWindowOpen: MutableState<Boolean>,
    navigator: FrameNavigation,
    window: MutableState<Unit?>,
    title: String,
) {
    val state = rememberWindowState(size = DpSize(1800.dp, 1000.dp))
    if (!isWindowOpen.value) {
        return
    }
    window.value =
        Window(
            title = title,
            onCloseRequest = {
                isWindowOpen.value = false
                window.value = null
            },
            state = state,
        ) { wrapTheming { fullScreenContent(bitmap = bitmap, navigator = navigator) } }
}

@Composable
fun fullScreenContent(
    bitmap: MutableState<ImageBitmap>,
    navigator: FrameNavigation,
) {
    Column {
        wrappedDifferenceImage(bitmap = bitmap)
        NavigationButtons(navigator = navigator, buttonModifier = Modifier.weight(1f))
    }
}

@Composable
fun RowScope.DisplayedImage(
    bitmap: MutableState<ImageBitmap>,
    modifier: Modifier = Modifier,
    navigator: FrameNavigation,
    title: String,
) {
    val isWindowOpen = remember { mutableStateOf(false) }
    val window = remember { mutableStateOf<Unit?>(null) }
    windowCreator(bitmap, isWindowOpen, navigator, window, title)

    Column(modifier = Modifier.fillMaxSize().weight(1f)) {
        // Button to pop out window
        Row(modifier.weight(0.15f)) {
            Spacer(Modifier.weight(0.7f))
            svgButton(content = "full-screen.svg", modifier = Modifier.weight(0.3f), onClick = {
                isWindowOpen.value = true
            })
        }
        // Image
        wrappedDifferenceImage(bitmap = bitmap, modifier = modifier.weight(0.85f))
    }
}

@Composable
fun wrappedDifferenceImage(
    bitmap: MutableState<ImageBitmap>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier.background(Color.Gray)
                .padding(8.dp)
                .fillMaxWidth(1f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) { Image(bitmap = bitmap.value, null) }
}

/**
 * A function that handles key presses.
 * @param event [KeyEvent] containing the key press event.
 * @param keyActions [Map] of [Pair]s of [Boolean] and [Key] to [Function]s to be called when the key is pressed.
 */
fun keyEventHandler(
    event: KeyEvent,
    keyActions: Map<Pair<Boolean, Key>, () -> Unit>,
): Boolean {
    // only handle key down events
    if (event.type != KeyEventType.KeyDown) return false
    // if the key is in the map, call the function
    val keyAction = keyActions[Pair(event.isCtrlPressed, event.key)]?.invoke()
    // return true if the key was in the map
    return keyAction != null
}
