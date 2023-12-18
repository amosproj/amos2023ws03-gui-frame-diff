package ui.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import frameNavigation.FrameNavigation
import models.AppState
import ui.components.AutoSizeText

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
            Title(text = "Video 1")
            Title(text = "Diff")
            Title(text = "Video 2")
        }
//        ###########   Box   ###########
        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(0.6f)) {
            DisplayedImage(bitmap = navigator.video1Bitmap, navigator = navigator)
            DisplayedImage(bitmap = navigator.diffBitmap, navigator = navigator)
            DisplayedImage(bitmap = navigator.video2Bitmap, navigator = navigator)
        }
//        ###########   Buttons   ###########
        Row(modifier = Modifier.fillMaxWidth().weight(0.2f)) {
            jumpButton(onClick = { navigator.jumpToNextDiff(false) }, content = "skipStart.svg")
            jumpButton(onClick = { navigator.jumpFrames(-1) }, content = "skipPrev.svg")
            jumpButton(onClick = { navigator.jumpFrames(1) }, content = "skipNext.svg")
            jumpButton(onClick = { navigator.jumpToNextDiff(true) }, content = "skipEnd.svg")
        }
    }
}

/**
 * A Composable function that creates a box to display text.
 * @param text [String] containing the text to be displayed.
 * @return [Unit]
 */

@Composable
fun RowScope.Title(text: String) {
    AutoSizeText(
        text = text,
        modifier = Modifier.weight(1f).fillMaxSize().padding(20.dp),
    )
}

/**
 * A Composable function that creates a box to display an image.
 * @param bitmap [MutableState] of [ImageBitmap] containing the bitmap to be displayed.
 * @param modifier [Modifier] to be applied to the [Box].
 * @return [Unit]
 */
@Composable
fun windowCreator(
    bitmap: MutableState<ImageBitmap>,
    b: Boolean,
    setB: (Boolean) -> Unit,
    navigator: FrameNavigation,
) {
    if (b) {
        Window(onCloseRequest = { setB(false) }, state = WindowState(width = 1800.dp, height = 1000.dp)) {
            Column {
                Row() {
                    Image(bitmap = bitmap.value, null)
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    jumpButton(onClick = { navigator.jumpToNextDiff(false) }, content = "skipStart.svg")
                    jumpButton(onClick = { navigator.jumpFrames(-1) }, content = "skipPrev.svg")
                    jumpButton(onClick = {
                        navigator.jumpFrames(1)
                    }, content = "skipNext.svg")
                    jumpButton(onClick = { navigator.jumpToNextDiff(true) }, content = "skipEnd.svg")
                }
            }
        }
    }
}

@Composable
fun RowScope.DisplayedImage(
    bitmap: MutableState<ImageBitmap>,
    modifier: Modifier = Modifier,
    navigator: FrameNavigation,
) {
    val isWindowOpen = remember { mutableStateOf(false) }
    windowCreator(bitmap, isWindowOpen.value, { isWindowOpen.value = it }, navigator)
    Column(modifier = Modifier.fillMaxSize().weight(1f)) {
        Row(modifier.weight(0.15f)) {
            Spacer(Modifier.weight(0.7f))
            jumpButton(content = "full-screen.svg", weight = 0.3f, onClick = {
                isWindowOpen.value = true
            })
        }
        Row(
            modifier =
            modifier.weight(0.85f)
                .background(Color.Gray)
                .padding(8.dp)
                .fillMaxWidth(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) { Image(bitmap = bitmap.value, null) }
    }
}

/**
 * A Composable function that creates a button that jumps frames.
 * @param onClick [Function] to be called when the button is clicked.
 * @param content [String] containing the name of the svg file to be displayed.
 * @return [Unit]
 */
@Composable
fun RowScope.jumpButton(
    onClick: () -> Unit,
    content: String,
    modifier: Modifier = Modifier,
    weight: Float = 1f,
) {
    Button(
        onClick = onClick,
        modifier = modifier.weight(weight).padding(40.dp, 20.dp, 40.dp, 20.dp),
    ) {
        Image(
            painter = painterResource(content),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
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
